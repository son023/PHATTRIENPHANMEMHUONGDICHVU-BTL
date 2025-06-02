package com.auth_service.auth_service.controller;

import com.auth_service.auth_service.model.User;
import com.auth_service.auth_service.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        return authService.login(user);
    }

}
