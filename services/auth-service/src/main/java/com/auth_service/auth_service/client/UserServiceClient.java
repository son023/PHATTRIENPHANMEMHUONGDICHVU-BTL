package com.auth_service.auth_service.client;

import com.auth_service.auth_service.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users/valid")
    ResponseEntity<User> valid(@RequestBody User user);

    @GetMapping("/users/{username}")
    ResponseEntity<User> getByUsername(@PathVariable String username);
}

