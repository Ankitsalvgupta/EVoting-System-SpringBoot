package com.codingninjas.EVotingSystem.controller;

import com.codingninjas.EVotingSystem.dto.AuthResponse;
import com.codingninjas.EVotingSystem.dto.LoginRequest;
import com.codingninjas.EVotingSystem.dto.RegisterRequest;
import com.codingninjas.EVotingSystem.entity.Role;
import com.codingninjas.EVotingSystem.entity.User;
import com.codingninjas.EVotingSystem.repository.UserRepository;
import com.codingninjas.EVotingSystem.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        if(userRepository.existsByName(req.name())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User user = new User();
        user.setName(req.name());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.VOTER);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registered");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.name(), req.password()));
        UserDetails user = userDetailsService.loadUserByUsername(req.name());
        return new AuthResponse(jwtService.generateToken(user));
    }
}
