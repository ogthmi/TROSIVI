package com.n20.qlphongtro.roomservice.controller;

import com.n20.qlphongtro.roomservice.entity.Room;
import com.n20.qlphongtro.roomservice.repository.RoomPageRequest;
import com.n20.qlphongtro.roomservice.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@AllArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Page<Room>> getRooms(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole,
            @ModelAttribute RoomPageRequest pageRequest
    ) {
        return ResponseEntity.ok(roomService.getRooms(Long.parseLong(userId), userRole, pageRequest));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }
}
