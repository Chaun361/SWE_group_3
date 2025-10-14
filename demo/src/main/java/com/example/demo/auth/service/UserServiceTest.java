package com.example.demo.auth.service;

import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        User mockUser = new User("John Doe", "johndoe", "123", "0900000000", "Bangkok");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

        boolean result = userService.login("johndoe", "123");

        assertTrue(result);
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLogin_Fail_WrongPassword() {
        User mockUser = new User("John Doe", "johndoe", "123", "0900000000", "Bangkok");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));

        boolean result = userService.login("johndoe", "wrongpass");

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void testLogin_Fail_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = userService.login("unknown", "123");

        assertFalse(result);
        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
