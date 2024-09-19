package com.BlogSystem.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BlogSystem.main.model.User;
import com.BlogSystem.main.service.UserService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
    private UserService userService;

    // User Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration successful. Please check your email for OTP.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | MessagingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // OTP Verification Endpoint
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = userService.verifyOtp(email, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or email.");
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String token = userService.login(username, password);
        if (token != null) {
            return ResponseEntity.ok("Login successful. JWT Token: " + token);
        } else {
            return ResponseEntity.badRequest().body("Invalid username or password.");
        }
    }
}
