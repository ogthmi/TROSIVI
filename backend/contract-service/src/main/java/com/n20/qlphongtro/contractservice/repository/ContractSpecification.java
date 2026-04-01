package com.n20.qlphongtro.contractservice.repository;

import com.n20.qlphongtro.contractservice.entity.Contract;
import com.n20.qlphongtro.contractservice.entity.ContractStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ContractSpecification {
    public static Specification<Contract> hasManagerId(Long managerId){
        if (managerId == null) return null;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("managerId"), managerId);
    }

    public static Specification<Contract> hasCustomerId(Long customerId){
        if (customerId == null) return null;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customerId"), customerId);
    }

    public static Specification<Contract> hasRoomId(Long roomId){
        if (roomId == null) return null;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("roomId"), roomId);
    }

    public static Specification<Contract> hasStatus (ContractStatus contractStatus){
        if (contractStatus == null) return null;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), contractStatus);
    }

    public static Specification<Contract> hasDepositRange(Float depositFrom, Float depositTo) {
        return (root, query, criteriaBuilder) -> {
            if (depositFrom != null && depositTo != null) {
                return criteriaBuilder.between(root.get("deposit"), depositFrom, depositTo);
            }
            else if (depositFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("deposit"), depositFrom);
            }
            else if (depositTo != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("deposit"), depositTo);
            }
            else {
                return null;
            }
        };
    }

    public static Specification<Contract> hasRentalPriceFrom(Float rentlPriceFrom, Float rentalPriceTo) {
        return (root, query, criteriaBuilder) -> {
            if (rentlPriceFrom != null && rentalPriceTo != null) {
                return criteriaBuilder.between(root.get("rentalPrice"), rentlPriceFrom, rentalPriceTo);
            }
            else if (rentlPriceFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rentalPrice"), rentlPriceFrom);
            }
            else if (rentalPriceTo != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("rentalPrice"), rentalPriceTo);
            }
            else {
                return null;
            }
        };
    }

    public static Specification<Contract> hasDateRange(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate)
                );
            }
            else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
            }
            else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
            }
            else {
                return null;
            }
        };
    }

    public static Specification<Contract> build(ContractPageRequest request){
        return Specification.where(hasManagerId(request.getManagerId()))
                .and(hasCustomerId(request.getCustomerId()))
                .and(hasRoomId(request.getRoomId()))
                .and(hasDateRange(request.getStartDate(), request.getEndDate()))
                .and(hasDepositRange(request.getDepositFrom(), request.getDepositTo()))
                .and(hasRentalPriceFrom(request.getRentalPriceFrom(), request.getRentalPriceTo()))
                .and(hasStatus(request.getStatus()));
    }
}
