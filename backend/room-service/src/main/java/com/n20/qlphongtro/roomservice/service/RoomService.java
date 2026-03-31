package com.n20.qlphongtro.roomservice.service;

import com.n20.qlphongtro.roomservice.entity.Room;
import com.n20.qlphongtro.roomservice.entity.User;
import com.n20.qlphongtro.roomservice.pagination.PageUtil;
import com.n20.qlphongtro.roomservice.repository.RoomPageRequest;
import com.n20.qlphongtro.roomservice.repository.RoomRepository;
import com.n20.qlphongtro.roomservice.repository.RoomSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final WebClient.Builder webClientBuilder;

    private User getManagerById(Long userId) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("lb://user-service/api/manager/{userId}", userId)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    private List<User> getManagersByIdList(List<Long> userIdList) {
        WebClient webClient = webClientBuilder.build();

        String ids = userIdList.stream().map(String::valueOf).collect(Collectors.joining(","));

        return webClient.get()
                .uri("lb://user-service/api/manager/batch?ids={ids}", ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {
                })
                .block();
    }

    private EntityNotFoundException roomNotFound() {
        return new EntityNotFoundException("Room not found");
    }

    public Room getRoomById(Long roomId) {
        var room = roomRepository.findById(roomId).orElseThrow(this::roomNotFound);

        var manager = getManagerById(room.getManagerId());
        room.setManager(manager);

        return room;
    }

    public Page<Room> getRooms(Long userId, String userRole, RoomPageRequest request) {
        final List<String> ROOM_SORT_FIELDS = List.of("roomId", "roomName", "area", "price");

        Sort sort = PageUtil.buildSort(request.getSortBy(), request.getDirection(), ROOM_SORT_FIELDS);

        Pageable pageable = PageUtil.buildPageable(request, sort);

        Specification<Room> specification = RoomSpecification.build(userId, userRole, request);

        Page<Room> roomPage = roomRepository.findAll(specification, pageable);

        List<Long> managerIdList = roomPage.stream().map(Room::getManagerId).toList();

        Map<Long, User> managerMap = getManagersByIdList(managerIdList)
                .stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        return roomPage.map(room -> {
            room.setManager(managerMap.get(room.getManagerId()));
            return room;
        });
    }
}
