package com.main_project.user_service.service;

import com.main_project.user_service.entity.User;
import com.main_project.user_service.exceptions.AppException;
import com.main_project.user_service.exceptions.enums.ErrorCode;
import com.main_project.user_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public User create(User user) {
        boolean isExisted = userRepository.existsByUsername(user.getUsername());
        if(isExisted) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public User valid(User user) {
        var existedUser = userRepository.findByUsername(user.getUsername());
        if(!existedUser.isPresent()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean isValid = passwordEncoder
                .matches(user.getPassword(), existedUser.get().getPassword());
        if(!isValid) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return existedUser.get();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
