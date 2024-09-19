package com.BlogSystem.main.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BlogSystem.main.model.User;
import com.BlogSystem.main.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtService jwtService;

    private final Random random = new Random();

    public User registerUser(User user) throws MessagingException {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username is already taken");
        }

        String otp = generateOtp();
        emailService.sendOtpEmail(user.getEmail(), otp);

        user.setPassword(passwordService.encodePassword(user.getPassword()));
        user.setVerified(false);
        return userRepository.save(user);
    }

    private String generateOtp() {
        return String.format("%04d", random.nextInt(10000));
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordService.matches(password, user.getPassword())) {
            return jwtService.generateToken(username);
        }
        return null;
    }
}
