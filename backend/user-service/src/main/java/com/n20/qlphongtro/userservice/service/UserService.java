package com.n20.qlphongtro.userservice.service;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(this::userNotFound);
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(this::userNotFound);
    }

    private EntityNotFoundException userNotFound() {
        return new EntityNotFoundException("User not found");
    }
}
