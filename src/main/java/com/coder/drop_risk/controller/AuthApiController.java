package com.coder.drop_risk.controller;

import com.coder.drop_risk.model.AppUser;
import com.coder.drop_risk.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private static final Pattern GMAIL_PATTERN = Pattern.compile(
            "^([A-Za-z0-9](?:[A-Za-z0-9.]{4,28}[A-Za-z0-9]))(?:\\+[A-Za-z0-9][A-Za-z0-9.-]{0,31})?@gmail\\.com$",
            Pattern.CASE_INSENSITIVE);
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthApiController(AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignupRequest request) {
        if (blank(request.fullName) || request.fullName.length() > 120)
            return error("Full name is required (up to 120 characters).");
        if (request.username == null || !request.username.matches("[A-Za-z0-9_.-]{3,50}"))
            return error("Username must be 3 to 50 characters and use letters, numbers, dots, dashes, or underscores.");
        if (request.password == null || request.password.length() < 8)
            return error("Password must be at least 8 characters.");

        String email = request.email == null ? "" : request.email.trim();
        Matcher gmail = GMAIL_PATTERN.matcher(email);
        if (!gmail.matches() || gmail.group(1).contains(".."))
            return error("Enter a valid Gmail address, such as name@gmail.com.");
        if (users.existsByUsernameIgnoreCase(request.username.trim())) return error("Username is already taken.");
        if (users.existsByEmailIgnoreCase(email)) return error("Email is already registered.");

        AppUser user = new AppUser();
        user.setUsername(request.username.trim());
        user.setEmail(email);
        user.setFullName(request.fullName.trim());
        user.setPassword(passwordEncoder.encode(request.password));
        users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Account created successfully."));
    }

    private static boolean blank(String value) { return value == null || value.isBlank(); }
    private static ResponseEntity<Map<String, String>> error(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    public static class SignupRequest {
        public String username;
        public String password;
        public String email;
        public String fullName;
    }
}