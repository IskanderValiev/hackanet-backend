package com.hackanet.services.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hackanet.application.AppConstants;
import com.hackanet.exceptions.AlreadyExistsException;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.*;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserPhoneToken;
import com.hackanet.models.user.UserToken;
import com.hackanet.push.enums.ClientType;
import com.hackanet.repositories.user.UserPhoneTokenRepository;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.repositories.user.UserTokenRepository;
import com.hackanet.security.enums.Role;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.EmailService;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.PortfolioService;
import com.hackanet.services.skill.SkillService;
import com.hackanet.utils.RandomString;
import com.hackanet.utils.validators.UserUpdateFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

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
    private PortfolioService portfolioService;

    @Autowired
    private UserNotificationSettingsService userNotificationSettingsService;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private UserUpdateFormValidator userUpdateFormValidator;

    @Override
    @Transactional
    public TokenDto register(UserRegistrationForm form) {
        String email = form.getEmail().toLowerCase();
        checkIfExistsByEmail(email);
        User user = userRepository.save(buildUser(form));
        userNotificationSettingsService.getOrCreateDefaultsSettingsForUser(user);
        portfolioService.getByUserId(user.getId());
        emailService.sendWelcomeEmail(user);
        emailService.sendEmailConfirmation(user);
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
    public User get(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> NotFoundException.forUser(email));
    }

    @Override
    public User getUserInfo(Long id, User currentUser) {
        User user = get(id);
        if (currentUser == null) {
            return user;
        }
        SecurityUtils.checkUserProfileForViewing(user, currentUser);
        return user;
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.forUser(id));
    }

    @Override
    public Set<User> getByIds(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    @Override
    public List<User> userList(UserSearchForm form) {
        if (form.getLimit() == null) {
            form.setLimit(DEFAULT_LIMIT);
        }
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
        if (Boolean.TRUE.equals(add)) {
            user.getAttendedHackathons().add(hackathon);
        } else {
            user.getAttendedHackathons().remove(hackathon);
        }
        userRepository.save(user);
    }

    @Override
    public User update(Long id, User currentUser, UserUpdateForm form) {
        userUpdateFormValidator.validateUpdateForm(form);
        User user = get(id);
        SecurityUtils.checkProfileAccess(currentUser, user);
        if (form.getNickname() != null) {
            checkIfExistsByNickname(currentUser, form.getNickname().toLowerCase());
            user.setNickname(form.getNickname().toLowerCase());
        } else {
            user.setNickname(form.getNickname());
        }
        user.setName(com.hackanet.utils.StringUtils.formatProper(form.getName(), false, "Name"));
        user.setLastname(com.hackanet.utils.StringUtils.formatProper(form.getLastname(), false, "Lastname"));
        user.setAbout(form.getAbout());
        user.setSkills(skillService.getByIds(form.getSkills()));
        user.setLookingForTeam(form.getLookingForTeam());
        user.setPosition(positionService.get(form.getPositionId()));
        user.setUniversity(form.getUniversity());
        user.setPicture(fileInfoService.get(form.getPicture()));
        user.setCity(com.hackanet.utils.StringUtils.formatProper(form.getCity(), true, "City"));
        user.setCountry(com.hackanet.utils.StringUtils.formatProper(form.getCountry(), true, "Country"));
        return userRepository.save(user);
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
                .emailConfirmed(false)
                .lastRequestTime(LocalDateTime.now())
                .emailConfirmationCode(new RandomString().nextString())
                .accessTokenParam(new RandomString().nextString())
                .refreshTokenParam(new RandomString().nextString())
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

    private void checkIfExistsByEmail(String email) {
        if (exists(email))
            throw new BadRequestException("User with such email already exists");
    }

    @Override
    public void updateLastRequestTime(User user) {
        user = get(user.getId());
        user.setLastRequestTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void confirmEmail(String code) {
        final User user = getByEmailConfirmationCode(code);
        user.setEmailConfirmed(Boolean.TRUE);
        userRepository.save(user);
    }

    private User getByEmailConfirmationCode(String code) {
        return userRepository.findByEmailConfirmationCode(code)
                .orElseThrow(() -> NotFoundException.throwNFE(User.class, "email confirmation code", code));
    }

    private void checkIfExistsByNickname(User currentUser, String nickname) {
        userRepository.findByNickname(nickname)
                .ifPresent((user) -> {
                    if (!currentUser.equals(user)) {
                        AlreadyExistsException.throwException(user.getClass(), "nickname", nickname);
                    }
                });
    }

    private CriteriaQuery<User> getUsersListQuery(CriteriaBuilder criteriaBuilder, UserSearchForm form) {
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("role"), Role.USER));
        if (form.getLookingForTeam() != null) {
            if (form.getLookingForTeam()) {
                predicates.add(criteriaBuilder.isTrue(root.get("lookingForTeam")));
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get("lookingForTeam")));
            }
        }
        if (!StringUtils.isBlank(form.getNickname())) {
            predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + form.getNickname() + "%"));
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
            predicates.add(criteriaBuilder.like(root.get("city"), "%" + StringUtils.capitalize(city.toLowerCase()) + "%"));
        }
        if (!StringUtils.isBlank(form.getCountry())) {
            String country = form.getCountry().trim().toLowerCase();
            predicates.add(criteriaBuilder.like(root.get("country"), "%" + StringUtils.capitalize(country.toLowerCase()) + "%"));
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

    private User buildUser(UserRegistrationForm form) {
        return User.builder()
                .email(form.getEmail().toLowerCase())
                .hashedPassword(passwordUtil.hash(form.getPassword()))
                .role(Role.USER)
                .lookingForTeam(Boolean.FALSE)
                .accessTokenParam(new RandomString().nextString())
                .refreshTokenParam(new RandomString().nextString())
                .emailConfirmationCode(new RandomString().nextString())
                .lastRequestTime(LocalDateTime.now())
                .emailConfirmed(Boolean.FALSE)
                .picture(fileInfoService.get(AppConstants.DEFAULT_PROFILE_IMAGE_ID))
                .build();
    }
}
