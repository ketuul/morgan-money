package com.ketul.morganmoney.controller;

import com.ketul.morganmoney.model.User;
import com.ketul.morganmoney.repository.UserRepository;
import com.ketul.morganmoney.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserRepository userRepository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // POST /api/auth/signup — create a new user
    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");

        // Check if email is already taken
        if (userRepository.findByEmail(email).isPresent()) {
            return Map.of("error", "Email already in use");
        }

        // Hash the password before saving — never store plain text passwords
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword, name);
        userRepository.save(user);

        // Generate a token so they're logged in immediately after signup
        String token = jwtService.generateToken(email);
        return Map.of(
            "message", "Account created successfully!",
            "token", token,
            "userId", user.getId().toString()
        );
    }

    // POST /api/auth/login — log in with email and password
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> user = userRepository.findByEmail(email);

        // Check if user exists and password matches
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            return Map.of("error", "Invalid email or password");
        }

        // Generate and return a token
        String token = jwtService.generateToken(email);
        return Map.of(
            "message", "Login successful!",
            "token", token,
            "userId", user.get().getId().toString()
        );
    }
}
