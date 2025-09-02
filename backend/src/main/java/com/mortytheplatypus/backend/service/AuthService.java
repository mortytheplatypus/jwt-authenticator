package com.mortytheplatypus.backend.service;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mortytheplatypus.backend.model.Role;
import com.mortytheplatypus.backend.model.User;
import com.mortytheplatypus.backend.model.dto.LoginDto;
import com.mortytheplatypus.backend.model.dto.RegisterDto;
import com.mortytheplatypus.backend.model.dto.TokenResponse;
import com.mortytheplatypus.backend.repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenResponse login(LoginDto loginDto) {
        try {
            String username = loginDto.username();
            String password = loginDto.password();

            User user = userRepository.findByUsername(loginDto.username())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + username + "' not found"));

            if (Boolean.FALSE.equals(passwordEncoder.matches(password, user.getPassword()))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
            } else {
                String token = jwtService.generateToken(user);
                return new TokenResponse(token);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User login unavailable at the moment");
        }
    }

    public void register(RegisterDto registerDto) {
        try {            
            if (userRepository.findByEmail(registerDto.email()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email '" + registerDto.email() + "' is already registered");
            }

            if (userRepository.findByUsername(registerDto.username()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '" + registerDto.username() + "' is already taken");
            }

            if (registerDto.name().length() < 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be at least 2 characters long");
            }

            if (registerDto.password().length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters long");
            }

            createUser(registerDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration unavailable at the moment");
        }
    }

    private void createUser(RegisterDto registerDto) {
        User user = new User();

        user.setName(registerDto.name());
        user.setEmail(registerDto.email());
        user.setUsername(registerDto.username());
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }
}
