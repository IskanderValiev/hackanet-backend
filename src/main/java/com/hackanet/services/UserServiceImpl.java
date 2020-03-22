package com.hackanet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hackanet.application.AppConstants;
import com.hackanet.application.Patterns;
import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.*;
import com.hackanet.push.enums.ClientType;
import com.hackanet.repositories.PasswordChangeRequestRepository;
import com.hackanet.repositories.UserPhoneTokenRepository;
import com.hackanet.repositories.UserRepository;
import com.hackanet.repositories.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.security.enums.TokenType;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.utils.DateTimeUtil;
import com.hackanet.utils.PhoneUtil;
import com.hackanet.utils.RandomString;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hackanet.security.utils.ProviderUtils.*;
import static com.hackanet.security.utils.SecurityUtils.checkUserProfileForViewing;
import static com.hackanet.utils.DateTimeUtil.localDateTimeToLong;
import static com.hackanet.utils.StringUtils.generateRandomString;
import static com.hackanet.utils.StringUtils.getJsonOfTokenDtoFromPrincipalName;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // FIXME: 10/21/19 change the value
    private static final Integer DEFAULT_LIMIT = 10;
    private static final Integer PASSWORD_REQUEST_EXPIRED_TIME = 15;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SkillService skillService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private UserPhoneTokenRepository userPhoneTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordChangeRequestRepository passwordChangeRequestRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Override
    @Transactional
    public TokenDto register(UserRegistrationForm form) {
        String email = form.getEmail().toLowerCase();
        String password = form.getPassword();
        if (exists(email))
            throw new BadRequestException("User with such email already exists");
        String phone = PhoneUtil.formatPhone(form.getPhone());
        if (existsByPhone(phone))
            throw new BadRequestException("User with such phone already exists");
        User user = User.builder()
                .email(email)
                .hashedPassword(passwordUtil.hash(password))
                .phone(PhoneUtil.formatPhone(form.getPhone().trim()))
                .name(form.getName())
                .lastname(form.getLastname())
                .city(form.getCity())
                .country(form.getCountry())
                .about(form.getAbout())
                .role(Role.USER)
                .lookingForTeam(Boolean.FALSE)
                .accessTokenParam(new RandomString().nextString())
                .refreshTokenParam(new RandomString().nextString())
                .picture(fileInfoService.get(AppConstants.DEFAULT_PROFILE_IMAGE_ID))
                .build();

        if (form.getSkills() != null && !form.getSkills().isEmpty())
            user.setSkills(skillService.getByIds(form.getSkills()));
        user = userRepository.save(user);

        userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        portfolioService.getByUserId(user.getId());
        emailService.sendWelcomeEmail(user);
        UserToken token = getOrCreateTokenIfNotExists(user);
        return buildTokenDtoByUser(user, token);
    }

    @Override
    @Transactional
    public TokenDto login(UserLoginForm form) {
        String email = form.getEmail().toLowerCase();
        String password = form.getPassword();

        User user = get(email);

        if (passwordUtil.matches(password, user.getHashedPassword())) {
            String value = getTokenValue(user, TokenType.ACCESS);

            UserToken token = userTokenRepository.findByUserId(user.getId());
            if (token == null) {
                token = getOrCreateTokenIfNotExists(user);
            }
            if (userTokenExpired(token, true)) {
                user.setRefreshTokenParam(new RandomString().nextString());
                userRepository.save(user);
            }

            final String prefix = jwtConfig.getPrefix() + " ";
            return TokenDto.builder()
                    .accessToken(prefix + value)
                    .accessTokenExpiresAt(localDateTimeToLong(token.getAccessTokenExpiresAt()))
                    .refreshToken(token.getRefreshToken())
                    .refreshTokenExpiresAt(localDateTimeToLong(token.getRefreshTokenExpiresAt()))
                    .userId(user.getId())
                    .role(user.getRole().toString())
                    .build();
        } else throw new BadRequestException("Login/Password is incorrect");
    }


    @Override
    public Boolean exists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhone(String phone) {
        phone = PhoneUtil.formatPhone(phone);
        return userRepository.existsByPhone(phone);
    }

    @Override
    public User get(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> NotFoundException.forUser(email));
    }

    @Override
    public User getUserInfo(Long id, User currentUser) {
        User user = get(id);
        if (currentUser == null)
            return user;
        checkUserProfileForViewing(user, currentUser);
        return user;
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> NotFoundException.forUser(id));
    }

    @Override
    public Set<User> getByIds(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    @Override
    public List<User> userList(UserSearchForm form) {
        if (form.getLimit() == null)
            form.setLimit(DEFAULT_LIMIT);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> usersListQuery = getUsersListQuery(criteriaBuilder, form);
        TypedQuery<User> query = entityManager.createQuery(usersListQuery);
        if (form.getPage() != null) {
            query.setFirstResult((form.getPage() - 1) * form.getLimit());
        } else {
            form.setPage(1);
        }
        query.setMaxResults(form.getLimit());
        return query.getResultList();
    }

    @Override
    public void updateUsersHackathonList(User user, Hackathon hackathon, boolean add) {
        if (Boolean.TRUE.equals(add))
            user.getAttendedHackathons().add(hackathon);
        else user.getAttendedHackathons().remove(hackathon);

        userRepository.save(user);
    }

    @Transactional
    protected TokenDto saveFromGoogle(Map<String, Object> userDetails) {
        String email = (String) userDetails.get("email");
        boolean exists = exists(email.toLowerCase());
        User user;
        if (exists) {
            user = get(email.toLowerCase());
            UserToken userToken = getOrCreateTokenIfNotExists(user);
            return buildTokenDtoByUser(user, userToken);
        }

//            map.get("email_verified"); in case we need to change user status
        FileInfo fileInfo = FileInfo.builder()
                .name(generateRandomString())
                .previewLink((String) userDetails.get("picture"))
                .build();

        fileInfoService.save(fileInfo);

        user = User.builder()
                .email(email.toLowerCase())
                .name((String) userDetails.get("given_name"))
                .lastname((String) userDetails.get("family_name"))
                .picture(fileInfo)
                .role(Role.USER)
                .lookingForTeam(Boolean.FALSE)
                .refreshTokenParam(new RandomString().nextString())
                .accessTokenParam(new RandomString().nextString())
                .build();
        user = userRepository.save(user);
        UserToken userToken = getOrCreateTokenIfNotExists(user);
        emailService.sendWelcomeEmail(user);
        return buildTokenDtoByUser(user, userToken);
    }

    @Override
    public TokenDto saveFromGoogle(Authentication authentication) {
        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        return saveFromGoogle(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromGithub(Map<String, Object> userDetails) {
        User.UserBuilder userBuilder = User.builder();
        String email = (String) userDetails.get("email");
        String login = (String) userDetails.get("login");
        if (!StringUtils.isBlank(email)) {
            boolean exists = exists(email.toLowerCase());
            if (exists) {
                User user = get(email.toLowerCase());
                UserToken userToken = getOrCreateTokenIfNotExists(user);
                return buildTokenDtoByUser(user, userToken);
            }
            userBuilder.email(email);
        } else if (!StringUtils.isBlank(login)) {
            boolean exists = exists(login.toLowerCase());
            if (exists) {
                User user = get(login.toLowerCase());
                UserToken userToken = getOrCreateTokenIfNotExists(user);
                return buildTokenDtoByUser(user, userToken);
            }
            userBuilder.email(login);
        } else throw new BadRequestException("Email and login are null or empty.");

        String name = (String) userDetails.get("name");
        if (!StringUtils.isBlank(name))
            userBuilder.name(name);
        else
            userBuilder.name(login);
        userBuilder.role(Role.USER)
                .accessTokenParam(new RandomString().nextString())
                .refreshTokenParam(new RandomString().nextString())
                .country((String) userDetails.get("location"));

        User user = userRepository.save(userBuilder.build());
        UserToken token = getOrCreateTokenIfNotExists(user);

        String avatarUrl = (String) userDetails.get("avatar_url");
        fileInfoService.createAndSave(user, avatarUrl);
        return buildTokenDtoByUser(user, token);
    }

    @Override
    public TokenDto saveFromGithub(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromGithub(oidcUser.getAttributes());
    }

    @Override
    public TokenDto saveFromSocialNetwork(OAuth2AuthenticationToken principal) {
        if (isGoogle(principal)) return saveFromGoogle(principal);
        if (isGithub(principal)) return saveFromGithub(principal);
        if (isFacebook(principal)) return saveFromFacebook(principal);
        if (isLinkedIn(principal)) return saveFromLinkedIn(principal);
        throw new BadRequestException("Provider has not been found");
    }

    @Override
    public TokenDto saveFromFacebook(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromFacebook(oidcUser.getAttributes());
    }

    @Override
    public TokenDto saveFromLinkedIn(Authentication authentication) {
        DefaultOAuth2User oidcUser = (DefaultOAuth2User) authentication.getPrincipal();
        return saveFromLinkedIn(oidcUser.getAttributes());
    }

    @Transactional
    protected TokenDto saveFromLinkedIn(Map<String, Object> userDetails) {
        String name = (String) userDetails.get("localizedFirstName");
        String lastName = (String) userDetails.get("localizedLastName");
        String email = (String) userDetails.get("email");

        // TODO: 11/20/19 get email and save user if he does not exist with received email
        User user = User.builder()
                .name(name)
                .lastname(lastName)
                .email(email)
                .role(Role.USER)
                .refreshTokenParam(new RandomString().nextString())
                .accessTokenParam(new RandomString().nextString())
                .build();
        userRepository.save(user);
        return new TokenDto();
    }

    @Transactional
    protected TokenDto saveFromFacebook(Map<String, Object> userDetails) {
        return new TokenDto();
    }

    @Override
    public User update(@NotNull Long id, @NotNull User currentUser, @NotNull UserUpdateForm form) {
        User user = get(id);
        SecurityUtils.checkProfileAccess(currentUser, user);
        user.setName(form.getName());
        user.setLastname(form.getLastname());
        user.setAbout(form.getAbout());
        user.setAbout(form.getCity());
        user.setCountry(form.getCountry());
        user.setLookingForTeam(Boolean.TRUE.equals(form.getLookingForTeam()));

        if (form.getImage() == null) {
            form.setImage(AppConstants.DEFAULT_PROFILE_IMAGE_ID);
        }
        user.setPicture(fileInfoService.get(form.getImage()));

        if (form.getSkills() != null) {
            user.setSkills(skillService.getByIds(form.getSkills()));
        }
        user = userRepository.save(user);
        return user;
    }

    @Override
    public Multimap<ClientType, UserPhoneToken> getTokensForUser(Long userId) {
        Multimap<ClientType, UserPhoneToken> multimap = ArrayListMultimap.create();
        List<UserPhoneToken> availableTokens = userPhoneTokenRepository.findAllByUserId(userId);
        availableTokens.forEach(it -> multimap.put(it.getDeviceType(), it));
        return multimap;
    }

    @Override
    public void passwordResetRequest(String email) {
        if (StringUtils.isBlank(email)) throw new BadRequestException("Email is empty or null");

        RandomString randomString = new RandomString(21);
        String code = randomString.nextString();
        Optional<PasswordChangeRequest> optional = passwordChangeRequestRepository.findByCode(code);
        if (optional.isPresent()) {
            do {
                log.warn("Password change request with such code already exists. Generating new code.");
                code = randomString.nextString();
                optional = passwordChangeRequestRepository.findByCode(code);
            } while (optional.isPresent());
        }

        email = email.trim().toLowerCase();
        User user = get(email);

        PasswordChangeRequest request = passwordChangeRequestRepository.findAllByUserIdAndUsed(user.getId(), false);
        if (request == null) {
            request = PasswordChangeRequest.builder()
                    .code(code)
                    .createdDate(LocalDateTime.now())
                    .used(Boolean.FALSE)
                    .userId(user.getId())
                    .build();
            passwordChangeRequestRepository.save(request);
        } else {
            LocalDateTime now = LocalDateTime.now();
            long minutes = DateTimeUtil.getDifferenceBetweenLocalDateTimes(request.getCreatedDate(), now);
            if (minutes > PASSWORD_REQUEST_EXPIRED_TIME) {
                request.setCode(code);
                request.setUsed(Boolean.FALSE);
                request.setCreatedDate(now);
                passwordChangeRequestRepository.save(request);
            }
        }
        emailService.sendPasswordResetEmail(user, request);
    }

    @Override
    @Transactional
    public void changePassword(String code, String newPassword, String email) {
        if (StringUtils.isBlank(code))
            throw new BadRequestException("Code is empty or null");

        if (StringUtils.isBlank(email))
            throw new BadRequestException("Email is empty");

        boolean matches = Pattern.matches(Patterns.VALID_PASSWORD_REGEX, newPassword.trim());
        if (!matches)
            throw new BadRequestException("Password is invalid");

        PasswordChangeRequest passwordRequest = passwordChangeRequestRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Password change request not found"));

        if (Boolean.TRUE.equals(passwordRequest.getUsed()))
            throw new BadRequestException("The password change request has been already used");

        LocalDateTime now = LocalDateTime.now();
        long minutes = DateTimeUtil.getDifferenceBetweenLocalDateTimes(passwordRequest.getCreatedDate(), now);
        if (minutes > PASSWORD_REQUEST_EXPIRED_TIME) {
            throw new BadRequestException("Password reset request has expired");
        }

        User user = get(passwordRequest.getUserId());
        if (passwordUtil.matches(newPassword, user.getHashedPassword()))
            throw new BadRequestException("You can't use old password as a new one");
        if (!user.getEmail().equals(email)) {
            throw new BadRequestException("Emails are not the same");
        }

        user.setHashedPassword(passwordUtil.hash(newPassword));
        userRepository.save(user);

        passwordRequest.setUsed(Boolean.TRUE);
        passwordChangeRequestRepository.save(passwordRequest);
    }

    @Override
    public User createForCompany(CompanyCreateForm form) {
        String email = form.getEmail().trim().toLowerCase();
        String password = form.getPassword();

        User user = User.builder()
                .email(email)
                .hashedPassword(passwordUtil.hash(password))
                .role(Role.COMPANY_ADMIN)
                .country(form.getCountry())
                .city(form.getCity())
                .name(form.getName())
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public TokenDto updateAccessToken(User user) {
        UserToken userToken = userTokenRepository.findByUserId(user.getId());

        if (userTokenExpired(userToken, true))
            throw new ForbiddenException("Refresh token has expired");

        userToken.setAccessTokenExpiresAt(LocalDateTime.now().plusHours(AppConstants.ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS));
        userTokenRepository.save(userToken);

        User updateAccessTokenUser = get(user.getId());
        updateAccessTokenUser.setAccessTokenParam(new RandomString().nextString());
        updateAccessTokenUser = userRepository.save(updateAccessTokenUser);

        String value = getTokenValue(updateAccessTokenUser, TokenType.ACCESS);

        return TokenDto.builder()
                .userId(user.getId())
                .role(updateAccessTokenUser.getRole().toString())
                .refreshTokenExpiresAt(localDateTimeToLong(userToken.getRefreshTokenExpiresAt()))
                .refreshToken(userToken.getRefreshToken())
                .accessToken(jwtConfig.getPrefix() + " " + value)
                .accessTokenExpiresAt(localDateTimeToLong(userToken.getAccessTokenExpiresAt()))
                .build();
    }

    @Override
    public UserToken getByUserId(Long userId) {
        return userTokenRepository.findByUserId(userId);
    }

    /**
     * checks if the token has expired
     *
     * @param token          to check
     * @param isRefreshToken type of token
     * @return true if token has expired
     */
    @Override
    public boolean userTokenExpired(@NotNull UserToken token, boolean isRefreshToken) {
        if (isRefreshToken)
            return LocalDateTime.now().isAfter(token.getRefreshTokenExpiresAt());
        else
            return LocalDateTime.now().isAfter(token.getAccessTokenExpiresAt());
    }

    @Override
    public TokenDto getTokenDtoFromString(String principalName) {
        principalName = getJsonOfTokenDtoFromPrincipalName(principalName);
        try {
            return objectMapper.readValue(principalName, TokenDto.class);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public User get(User jwtData) {
        return get(jwtData.getId());
    }

    @Override
    @Caching (evict = {
                @CacheEvict(key = "#user.id", value = "connections-suggestions"),
                @CacheEvict(key = "#userToAdd.id", value = "connections-suggestions")
        }
    )
    public void addConnection(User user, User userToAdd) {
        Set<User> usersToSave = new HashSet<>();
        Set<User> connections = user.getConnections();
        if (!connections.contains(userToAdd)) {
            connections.add(userToAdd);
            user.setConnections(connections);
            usersToSave.add(user);
        }

        Set<User> userToAddConnections = userToAdd.getConnections();
        if (!userToAddConnections.contains(user)) {
            userToAddConnections.add(user);
            userToAdd.setConnections(userToAddConnections);
            usersToSave.add(userToAdd);
        }
        userRepository.saveAll(usersToSave);
    }

    @Override
    @Transactional
    @Caching (evict = {
            @CacheEvict(key = "#user.id", value = "connections-suggestions"),
            @CacheEvict(key = "#userToDelete.id", value = "connections-suggestions")
        }
    )
    public void deleteConnection(User user, User userToDelete) {
        Set<User> usersToSave = new HashSet<>();
        Set<User> connections = user.getConnections();
        if (connections.contains(userToDelete)) {
            connections.remove(userToDelete);
            user.setConnections(connections);
            usersToSave.add(user);
        }

        Set<User> userToDeleteConnections = userToDelete.getConnections();
        if (userToDeleteConnections.contains(user)) {
            userToDeleteConnections.remove(user);
            userToDelete.setConnections(userToDeleteConnections);
            usersToSave.add(userToDelete);
        }
        connectionInvitationService.delete(user.getId(), userToDelete.getId());
        userRepository.saveAll(usersToSave);
    }

    @Override
    public void deleteConnection(User user, Long connectionId) {
        User connectionUser = get(connectionId);
        user = get(user.getId());
        deleteConnection(user, connectionUser);
    }

    @Override
    public Set<User> getConnections(Long userId) {
        User user = get(userId);
        return user.getConnections();
    }

    @Override
    @Cacheable(key = "#user.id", value = "connections-suggestions")
    public Set<User> getConnectionsSuggestions(User user) {
        String query = "select us.* from users us where us.id in (select cn.connection_id from connections cn where cn.user_id in (select c.connection_id from connections c where c.user_id = :id))";
        Query nativeQuery = entityManager
                .createNativeQuery(query, User.class)
                .setParameter("id", user.getId());
        List<User> resultList = nativeQuery.getResultList();
        return resultList.stream().filter(u -> !u.getId().equals(user.getId())).collect(Collectors.toSet());
    }

    private String getTokenValue(User user, TokenType type) {
        return Jwts.builder()
                .claim("role", user.getRole().toString())
                .claim("email", user.getEmail())
                .claim("tokenParam", TokenType.REFRESH.equals(type) ? user.getRefreshTokenParam() : user.getAccessTokenParam())
                .claim("token-type", type.toString())
                .setSubject(user.getId().toString())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();
    }

    private CriteriaQuery<User> getUsersListQuery(CriteriaBuilder criteriaBuilder, UserSearchForm form) {
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        if (form.getLookingForTeam() != null) {
            if (form.getLookingForTeam()) {
                predicates.add(criteriaBuilder.isTrue(root.get("lookingForTeam")));
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get("lookingForTeam")));
            }
        }
        if (!StringUtils.isBlank(form.getName())) {
            Expression<String> nameInLowerCase = criteriaBuilder.lower(root.get("name"));
            Expression<String> lastnameInLowerCase = criteriaBuilder.lower(root.get("lastname"));
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(nameInLowerCase, "%" + form.getName().toLowerCase().trim() + "%"),
                    criteriaBuilder.like(lastnameInLowerCase, "%" + form.getName().toLowerCase().trim() + "%")));
        }
        if (!StringUtils.isBlank(form.getCity())) {
            String city = form.getCity().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(root.get("city"), "%" + StringUtils.capitalize(city) + "%"));
        }
        if (!StringUtils.isBlank(form.getCountry())) {
            String country = form.getCountry().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(root.get("country"), "%" + StringUtils.capitalize(country) + "%"));
        }
        if (form.getSkills() != null && !form.getSkills().isEmpty()) {
            Join<User, Skill> join = root.join("skills", JoinType.INNER);
            join.on(join.get("id").in(form.getSkills()));
            predicates.add(join.getOn());
        }
        if (form.getParticipantOfHackathonId() != null) {
            Join<User, Hackathon> join = root.join("participantOfHackathons", JoinType.INNER);
            join.on(join.get("id").in(form.getParticipantOfHackathonId()));
            predicates.add(join.getOn());
        }
        query.distinct(true);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }

    private UserToken getOrCreateTokenIfNotExists(User user) {
        UserToken userToken = userTokenRepository.findByUserId(user.getId());
        if (userToken != null) return userToken;

        userToken = UserToken.builder()
                .user(user)
                .accessTokenExpiresAt(LocalDateTime.now().plusHours(AppConstants.ACCESS_TOKEN_EXPIRING_TIME_IN_HOURS))
                .refreshTokenExpiresAt(LocalDateTime.now().plusDays(AppConstants.REFRESH_TOKEN_EXPIRING_TIME_IN_DAYS))
                .refreshToken(getTokenValue(user, TokenType.REFRESH))
                .build();
        return userTokenRepository.save(userToken);
    }

    private TokenDto buildTokenDtoByUser(User user, UserToken userToken) {
        return TokenDto.builder()
                .userId(user.getId())
                .refreshToken(userToken.getRefreshToken())
                .accessToken(getTokenValue(user, TokenType.ACCESS))
                .refreshTokenExpiresAt(localDateTimeToLong(userToken.getRefreshTokenExpiresAt()))
                .accessTokenExpiresAt(localDateTimeToLong(userToken.getAccessTokenExpiresAt()))
                .role(user.getRole().toString())
                .build();
    }
}
