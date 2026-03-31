package com.n20.qlphongtro.userservice.controller;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/internal/username/{username}")
    public ResponseEntity<User> getInternalUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/internal/id/{userId}")
    public ResponseEntity<User> getInternalUserById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById (@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
