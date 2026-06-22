package com.mortytheplatypus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.username(),
                        loginDto.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        return new TokenResponse(jwtService.generateToken(user));
    }

    public void register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email '" + registerDto.email() + "' is already registered"
            );
        }

        if (userRepository.findByUsername(registerDto.username()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '" + registerDto.username() + "' is already taken"
            );
        }

        createUser(registerDto);
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
