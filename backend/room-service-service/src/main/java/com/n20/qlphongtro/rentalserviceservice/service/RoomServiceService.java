package com.n20.qlphongtro.rentalserviceservice.service;

import com.n20.qlphongtro.rentalserviceservice.entity.RoomService;
import com.n20.qlphongtro.rentalserviceservice.repository.RoomServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomServiceService {
    private final RoomServiceRepository serviceRepository;

    public List<RoomService> getAllRoomService() {
        return serviceRepository.findAll();
    }

    public List<RoomService> getRoomServicesByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return serviceRepository.findAllById(idList).stream().distinct().toList();
    }
}
