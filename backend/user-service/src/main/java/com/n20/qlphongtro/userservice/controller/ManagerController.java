package com.n20.qlphongtro.userservice.controller;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.service.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@AllArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getManagerById(@PathVariable Long userId) {
        return ResponseEntity.ok(managerService.getManagerById(userId));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<User>> getManagersByIdList(@RequestParam("ids") String ids){
        return ResponseEntity.ok(managerService.getManagersByIdList(ids));
    }
}