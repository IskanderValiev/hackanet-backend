package com.hackanet.services;

import com.hackanet.config.JwtConfig;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.dto.TokenDto;
import com.hackanet.json.forms.UserLoginForm;
import com.hackanet.json.forms.UserRegistrationForm;
import com.hackanet.models.User;
import com.hackanet.repositories.UserRepository;
import com.hackanet.security.role.Role;
import com.hackanet.security.utils.PasswordUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public TokenDto register(UserRegistrationForm form) {
        String email = form.getEmail().toLowerCase();
        String password = form.getPassword();
        if (exists(email))
            throw new BadRequestException("User with such email already exists");
        User user = User.builder()
                .email(email)
                .hashedPassword(passwordUtil.hash(password))
                .name(form.getName())
                .lastname(form.getLastname())
                .role(Role.USER)
                .build();
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
    public User get(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> NotFoundException.forUser(email));
    }
}
