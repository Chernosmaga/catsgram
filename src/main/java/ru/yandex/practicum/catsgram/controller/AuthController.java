package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.config.JwtTokenProvider;
import ru.yandex.practicum.catsgram.controller.dto.AuthRequestDto;
import ru.yandex.practicum.catsgram.controller.dto.RegRequestDto;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final JwtTokenProvider provider;
    private final PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequest) {
        String email = authRequest.getEmail();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, authRequest.getPassword()));
        User user;
        try {
            user = repository.findByEmail(email);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Пользователь не найден");
        }
        String token = provider.createToken(email, user.getRole().name());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    @SneakyThrows
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody RegRequestDto regRequest) {
        if (repository.existsByEmail(regRequest.getEmail())) {
            throw new ValidationException("Пользователь с такой электронной почтой уже существует");
        }
        User user = new User();
        user.setEmail(regRequest.getEmail());
        user.setUsername(regRequest.getUsername());
        user.setNickname(regRequest.getNickname());
        user.setPassword(encoder.encode(regRequest.getPassword()));
        user.setRole(USER);
        user.setStatus(ACTIVE);
        user.setCreationDate(LocalDateTime.now());
        user.setIsClosed(false);
        repository.save(user);

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(regRequest.getEmail(), regRequest.getPassword()));
        String token = provider.createToken(regRequest.getEmail(), user.getRole().name());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", regRequest.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}