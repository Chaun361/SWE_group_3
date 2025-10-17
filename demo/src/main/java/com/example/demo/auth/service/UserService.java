package com.example.demo.auth.service;

import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

    public Long getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getUserId)
                .orElse(null);
    }
    public User registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
    user.getPassword() == null || user.getPassword().trim().isEmpty() ||
    user.getFullName() == null || user.getFullName().trim().isEmpty() ||
    user.getPhone() == null || user.getPhone().trim().isEmpty() ||
    user.getUserAddress() == null || user.getUserAddress().trim().isEmpty()) {
    throw new IllegalArgumentException("All fields are required and cannot be empty.");
}
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        return userRepository.save(user);
    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
