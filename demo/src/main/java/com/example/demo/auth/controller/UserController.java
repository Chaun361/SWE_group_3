package com.example.demo.auth.controller;

import com.example.demo.auth.model.User;
import com.example.demo.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        boolean isValid = userService.login(username, password);

        if (isValid) {
            Long userId = userService.getUserId(username);
            return ResponseEntity.ok(Map.of("message", "Login successful", "userId", userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }
         @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "userId", newUser.getUserId(),
                            "username", newUser.getUsername(),
                            "fullName", newUser.getFullName()
                    ));
        } catch (IllegalArgumentException e) {
            // ถ้าusernameซ้ำกับข้อมูลไม่ครบ
                if (e.getMessage().contains("exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
        } catch (Exception e) {
            // ถ้า body ผิดหรือข้อมูลไม่ครบ
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid user data", "details", e.getMessage()));
        }
    }
        @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "phone", user.getPhone(),
                "userAddress", user.getUserAddress()
        ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
