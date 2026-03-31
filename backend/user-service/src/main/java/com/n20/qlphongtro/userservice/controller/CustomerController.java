package com.n20.qlphongtro.userservice.controller;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.repository.UserPageRequest;
import com.n20.qlphongtro.userservice.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<User> createCustomer(@RequestBody User user) {
        var newCustomer = customerService.createCustomer(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @GetMapping
    public ResponseEntity<Page<User>> getCustomers(@ModelAttribute UserPageRequest pageRequest) {
        return ResponseEntity.ok(customerService.getCustomers(pageRequest));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getCustomerById(@PathVariable Long userId) {
        var customer = customerService.getCustomerById(userId);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateCustomerById(@PathVariable Long userId, @RequestBody User customer) {
        var updatedCustomer = customerService.updateCustomerById(userId, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Long userId) {
        customerService.deleteCustomerById(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
