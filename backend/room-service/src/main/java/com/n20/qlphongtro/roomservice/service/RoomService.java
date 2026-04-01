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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    private Map<Long, Boolean> checkRoomExistContract(LocalDate startDate, LocalDate endDate, List<Long> roomIds) {
        WebClient webClient = webClientBuilder
                .baseUrl("lb://contract-service")
                .build();

        String ids = roomIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/contract/check/exist")
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("ids", ids)
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, Boolean>>() {
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

    final List<String> ROOM_SORT_FIELDS = List.of("roomId", "roomName", "area", "price");

    public Page<Room> getRooms(Long userId, String userRole, RoomPageRequest request) {

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

    public Page<Room> getAvailableRooms(
            Long userId, String userRole, LocalDate startDate, LocalDate endDate, RoomPageRequest request
    ) {
        // 1. Build Sort & Pageable
        Sort sort = PageUtil.buildSort(request.getSortBy(), request.getDirection(), ROOM_SORT_FIELDS);
        Pageable pageable = PageUtil.buildPageable(request, sort);

        // 2. Build Specification cho các điều kiện cơ bản
        Specification<Room> specification = RoomSpecification.build(userId, userRole, request);

        // 3. Lấy tất cả Room thỏa spec (chỉ id nếu dataset lớn có thể optimize query chỉ lấy id)
        List<Room> allRooms = roomRepository.findAll(specification);

        // 4. Lấy danh sách roomId để kiểm tra hợp đồng trùng ngày
        List<Long> roomIds = allRooms.stream()
                .map(Room::getRoomId)
                .toList();

        // 5. Gọi service check room đã có hợp đồng hay chưa
        Map<Long, Boolean> occupiedMap = checkRoomExistContract(startDate, endDate, roomIds);

        // 6. Lọc ra các phòng còn trống
        List<Room> availableRooms = allRooms.stream()
                .filter(room -> !occupiedMap.getOrDefault(room.getRoomId(), false))
                .toList();

        // 7. Pagination thủ công
        int start = Math.min((int) pageable.getOffset(), availableRooms.size());
        int end = Math.min(start + pageable.getPageSize(), availableRooms.size());
        List<Room> pageContent = availableRooms.subList(start, end);

        // 8. Lấy danh sách managerId để batch query User
        List<Long> managerIds = pageContent.stream()
                .map(Room::getManagerId)
                .distinct()
                .toList();

        Map<Long, User> managerMap = getManagersByIdList(managerIds)
                .stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 9. Gắn manager vào Room
        pageContent.forEach(room -> room.setManager(managerMap.get(room.getManagerId())));

        // 10. Trả về PageImpl đúng pagination và totalElements
        return new PageImpl<>(pageContent, pageable, availableRooms.size());
    }
}
