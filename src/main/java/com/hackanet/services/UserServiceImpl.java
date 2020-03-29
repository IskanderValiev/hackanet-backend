package com.hackanet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hackanet.application.AppConstants;
import com.hackanet.application.Patterns;
import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.*;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.push.enums.ClientType;
import com.hackanet.repositories.PasswordChangeRequestRepository;
import com.hackanet.repositories.UserPhoneTokenRepository;
import com.hackanet.repositories.UserRepository;
import com.hackanet.repositories.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.utils.PhoneUtil;
import com.hackanet.utils.RandomString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class UserServiceImpl implements UserService, SocialNetworkAuthService {

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
    private UserTokenService userTokenService;

    @Override
    @Transactional
    public TokenDto register(UserRegistrationForm form) {
        String email = form.getEmail().toLowerCase();
        throwIfExistsByEmail(email);
        String phone = PhoneUtil.formatPhone(form.getPhone());
        throwIfExistsByPhone(phone);
        User user = getUser(form);

        if (form.getSkills() != null && !form.getSkills().isEmpty())
            user.setSkills(skillService.getByIds(form.getSkills()));
        user = userRepository.save(user);

        userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        portfolioService.getByUserId(user.getId());
        emailService.sendWelcomeEmail(user);
        return userTokenService.getTokenByUser(user);
    }

    @Override
    @Transactional
    public TokenDto login(UserLoginForm form) {
        String email = form.getEmail().toLowerCase();
        String password = form.getPassword();
        User user = get(email);

        if (passwordUtil.matches(password, user.getHashedPassword())) {
            UserToken token = userTokenRepository.findByUserId(user.getId());
            if (token == null) {
                token = userTokenService.getOrCreateTokenIfNotExists(user);
            }
            if (userTokenService.userTokenExpired(token, true)) {
                user.setRefreshTokenParam(new RandomString().nextString());
                userRepository.save(user);
            }
            return userTokenService.buildTokenDtoByUser(user, token);
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
            UserToken userToken = userTokenService.getOrCreateTokenIfNotExists(user);
            return userTokenService.buildTokenDtoByUser(user, userToken);
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
        UserToken userToken = userTokenService.getOrCreateTokenIfNotExists(user);
        emailService.sendWelcomeEmail(user);
        return userTokenService.buildTokenDtoByUser(user, userToken);
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
                return userTokenService.getTokenByUser(user);
            }
            userBuilder.email(email);
        } else if (!StringUtils.isBlank(login)) {
            boolean exists = exists(login.toLowerCase());
            if (exists) {
                User user = get(login.toLowerCase());
                return userTokenService.getTokenByUser(user);
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
        UserToken token = userTokenService.getOrCreateTokenIfNotExists(user);

        String avatarUrl = (String) userDetails.get("avatar_url");
        fileInfoService.createAndSave(user, avatarUrl);
        return userTokenService.buildTokenDtoByUser(user, token);

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
        user = userRepository.save(user);
        return userTokenService.getTokenByUser(user);
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
        user.setPicture(fileInfoService.get(form.getImage()));
        user.setSkills(skillService.getByIds(form.getSkills()));
        user.setLookingForTeam(form.getLookingForTeam());
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

    @Deprecated
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

    private void throwIfExistsByEmail(String email) {
        if (exists(email))
            throw new BadRequestException("User with such email already exists");
    }

    @Override
    public void updateLastRequestTime(User user) {
        user.setLastRequestTime(LocalDateTime.now());
        userRepository.save(user);
    }

    private void throwIfExistsByPhone(String phone) {
        if (existsByPhone(phone))
            throw new BadRequestException("User with such phone already exists");
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

    private User getUser(UserRegistrationForm form) {
        return User.builder()
                .email(form.getEmail().toLowerCase())
                .hashedPassword(passwordUtil.hash(form.getPassword()))
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
                .build();
    }
}
