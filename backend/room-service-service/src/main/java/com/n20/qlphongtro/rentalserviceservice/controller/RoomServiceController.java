package com.n20.qlphongtro.rentalserviceservice.controller;

import com.n20.qlphongtro.rentalserviceservice.entity.RoomService;
import com.n20.qlphongtro.rentalserviceservice.service.RoomServiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@AllArgsConstructor
public class RoomServiceController {
    private final RoomServiceService roomServiceService;

    @GetMapping
    public ResponseEntity<List<RoomService>> getAllRoomServices(){
        return ResponseEntity.ok(roomServiceService.getAllRoomService());
    }

    @GetMapping("/batch")
    public ResponseEntity<List<RoomService>> getRoomServicesByIds(
            @RequestParam(value = "ids") String ids
    ){
        return ResponseEntity.ok(roomServiceService.getRoomServicesByIds(ids));
    }
}
