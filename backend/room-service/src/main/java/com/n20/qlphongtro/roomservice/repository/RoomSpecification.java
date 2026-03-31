package com.n20.qlphongtro.roomservice.repository;

import com.n20.qlphongtro.roomservice.entity.Room;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {
    public static String buildLikeStatement(String string) {
        return "%" + string.toLowerCase() + "%";
    }

    public static Specification<Room> hasManagerId(Long managerId) {
        if (managerId == null) return null;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("managerId"), managerId);
    }

    public static Specification<Room> hasRoomName(String roomName) {
        if (roomName == null) return null;

        return (root, query, criteriaBuilder) -> {
            var like = buildLikeStatement(roomName);
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("roomName")), like);
        };
    }

    public static Specification<Room> hasAreaRange(Float areaFrom, Float areaTo) {
        return (root, query, criteriaBuilder) -> {
            if (areaFrom != null && areaTo != null) {
                return criteriaBuilder.between(root.get("area"), areaFrom, areaTo);
            }
            else if (areaFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("area"), areaFrom);
            }
            else if (areaTo != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("area"), areaTo);
            }
            else {
                return null;
            }
        };
    }

    public static Specification<Room> hasPriceRange(Float priceFrom, Float priceTo) {
        return (root, query, criteriaBuilder) -> {
            if (priceFrom != null && priceTo != null) {
                return criteriaBuilder.between(root.get("price"), priceFrom, priceTo);
            }
            else if (priceFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceFrom);
            }
            else if (priceTo != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceTo);
            }
            else {
                return null;
            }
        };
    }

    public static Specification<Room> build (Long userId, String userRole, RoomPageRequest request){
        Specification<Room> spec = Specification.where(hasRoomName(request.getRoomName()))
                .and(hasAreaRange(request.getAreaFrom(), request.getAreaTo()))
                .and(hasPriceRange(request.getPriceFrom(), request.getPriceTo()));

        if (userRole.equals("MANAGER")) {
            return spec.and(hasManagerId(userId));
        }
        return spec;
    }
}
