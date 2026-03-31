package com.n20.qlphongtro.userservice.service;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.entity.UserRole;
import com.n20.qlphongtro.userservice.exception.BusinessException;
import com.n20.qlphongtro.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ManagerService {
    private final UserRepository userRepository;

    public User getManagerById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (!user.getUserRole().equals(UserRole.MANAGER)) {
            throw new BusinessException("No permission to access this user");
        }

        return user;
    }

    public List<User> getManagersByIdList(String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return userRepository.findAllById(idList).stream().distinct().toList();
    }

}
