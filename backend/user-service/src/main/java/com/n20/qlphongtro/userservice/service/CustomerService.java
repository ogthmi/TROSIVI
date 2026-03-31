package com.n20.qlphongtro.userservice.service;

import com.n20.qlphongtro.userservice.pagination.PageUtil;
import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.entity.UserRole;
import com.n20.qlphongtro.userservice.exception.BusinessException;
import com.n20.qlphongtro.userservice.repository.UserPageRequest;
import com.n20.qlphongtro.userservice.repository.UserRepository;
import com.n20.qlphongtro.userservice.repository.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;

    public User createCustomer(User rawUser) {
        String hashedPassword = BCrypt.hashpw(rawUser.getPassword(), BCrypt.gensalt(10));
        rawUser.setPassword(hashedPassword);

        rawUser.setUserRole(UserRole.CUSTOMER);

        userRepository.save(rawUser);

        rawUser.setPassword(null);
        return rawUser;
    }

    public Page<User> getCustomers(UserPageRequest pageRequest) {
        final List<String> SORT_FIELD_LIST = List.of("userId", "fullName");

        Sort sort = PageUtil.buildSort(pageRequest.getSortBy(), pageRequest.getDirection(), SORT_FIELD_LIST);

        Pageable pageable = PageUtil.buildPageable(pageRequest, sort);

        Specification<User> customerSpecification = UserSpecification.getCustomers(pageRequest);
        customerSpecification.and(UserSpecification.hasRole(UserRole.CUSTOMER));

        Page<User> userPage = userRepository.findAll(customerSpecification, pageable);
        userPage.getContent().forEach(u -> u.setPassword(null));

        return userPage;
    }

    public User getCustomerById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (!user.getUserRole().equals(UserRole.CUSTOMER)) {
            throw new BusinessException("No permission to access this user");
        }

        user.setPassword(null);
        return user;
    }

    public User updateCustomerById(Long userId, User customerRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (!user.getUserRole().equals(UserRole.CUSTOMER)) {
            throw new BusinessException("No permission to access this user");
        }

        if (customerRequest.getFullName() != null) {
            user.setFullName(customerRequest.getFullName());
        }

        if (customerRequest.getPhone() != null) {
            user.setPhone(customerRequest.getPhone());
        }

        if (customerRequest.getEmail() != null) {
            user.setEmail(customerRequest.getEmail());
        }

        if (customerRequest.getBirthDate() != null) {
            user.setBirthDate(customerRequest.getBirthDate());
        }

        userRepository.save(user);

        user.setPassword(null);
        return user;
    }

    public void deleteCustomerById(Long userId) {
        var customer = getCustomerById(userId);

        if (customer != null) userRepository.deleteById(userId);
    }
}