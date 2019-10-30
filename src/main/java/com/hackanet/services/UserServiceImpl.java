package com.hackanet.services;

import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.json.forms.UserSearchForm;
import com.hackanet.json.forms.UserUpdateForm;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Skill;
import com.hackanet.models.User;
import com.hackanet.repositories.UserRepository;
import com.hackanet.security.role.Role;
import com.hackanet.security.utils.PasswordUtil;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.utils.PhoneUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hackanet.utils.StringUtils.generateRandomString;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Service
public class UserServiceImpl implements UserService {

    // FIXME: 10/21/19 change the value
    private static final Integer DEFAULT_LIMIT = 10;

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
    private HackathonService hackathonService;
    @Autowired
    private FileInfoService fileInfoService;


    @Override
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
                .build();

        if (form.getSkills() != null && !form.getSkills().isEmpty())
            user.setSkills(skillService.getByIdsIn(form.getSkills()));
        user = userRepository.save(user);

        final String prefix = jwtConfig.getPrefix() + " ";
        return TokenDto.builder()
                .userId(user.getId())
                .role(user.getRole().toString())
                .token(prefix + Jwts.builder()
                        .claim("role", user.getRole().toString())
                        .claim("email", user.getEmail())
                        .setSubject(user.getId().toString())
                        .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact())
                .build();
    }

    @Override
    public TokenDto login(UserLoginForm form) {
        String email = form.getEmail().toLowerCase();
        String password = form.getPassword();

        User user = get(email);

        if (passwordUtil.matches(password, user.getHashedPassword())) {
            final String prefix = jwtConfig.getPrefix() + " ";
            String value = Jwts.builder()
                    .claim("role", user.getRole().toString())
                    .claim("email", user.getEmail())
                    .setSubject(user.getId().toString())
                    .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();
            return TokenDto.builder().token(prefix + value).userId(user.getId()).role(user.getRole().toString()).build();
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
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> NotFoundException.forUser(id));
    }

    @Override
    public List<User> getByIds(List<Long> ids) {
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

    @Override
    @Transactional
    public User saveFromGoogle(Map<String, Object> userDetails) {
        String email = (String) userDetails.get("email");
        boolean userExists = exists(email);
        if (userExists)
            return get(email);

//            map.get("email_verified"); in case we need to change user status
        FileInfo fileInfo = FileInfo.builder()
                .name(generateRandomString())
                .previewLink((String) userDetails.get("picture"))
                .build();

        fileInfoService.save(fileInfo);

        User user = User.builder()
                .email(email)
                .name((String) userDetails.get("given_name"))
                .lastname((String) userDetails.get("family_name"))
                .image(fileInfo)
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
        return user;
    }

    @Override
    public User update(@NotNull Long id, @NotNull User currentUser, @NotNull UserUpdateForm form) {
        User user = get(id);

        SecurityUtils.checkProfileAccess(currentUser, user);

        if (!StringUtils.isBlank(form.getName()))
            user.setName(form.getName());

        if (!StringUtils.isBlank(form.getLastname()))
            user.setLastname(form.getLastname());

        if (!StringUtils.isBlank(form.getAbout()))
            user.setAbout(form.getAbout());

        if (!StringUtils.isBlank(form.getCity()))
            user.setAbout(form.getCity());

        if (!StringUtils.isBlank(form.getCountry()))
            user.setCountry(form.getCountry());

        if (form.getImage() != null)
            user.setImage(fileInfoService.get(form.getImage()));

        if (form.getSkills() != null)
            user.setSkills(skillService.getByIdsIn(form.getSkills()));

        user = userRepository.save(user);
        return user;
    }

    private CriteriaQuery<User> getUsersListQuery(CriteriaBuilder criteriaBuilder, UserSearchForm form) {
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
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
}
