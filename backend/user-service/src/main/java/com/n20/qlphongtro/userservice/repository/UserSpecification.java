package com.n20.qlphongtro.userservice.repository;

import com.n20.qlphongtro.userservice.entity.User;
import com.n20.qlphongtro.userservice.entity.UserRole;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static String buildLikeStatement(String string) {
        return "%" + string.toLowerCase() + "%";
    }

    public static Specification<User> hasKeyword(String keyword) {
        final List<String> searchFields = List.of(
                "userId", "username", "email", "phone", "fullName"
        );

        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String like = buildLikeStatement(keyword);
            List<Predicate> predicates = new ArrayList<>();

            for (String field : searchFields) {
                predicates.add(cb.like(cb.lower(root.get(field)), like));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> hasFullName(String fullName) {
        if (fullName == null) return null;
        return (root, query, criteriaBuilder) -> {
            String like = buildLikeStatement(fullName);

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), like);
        };
    }

    public static Specification<User> hasRole(UserRole role) {
        if (role == null) return null;
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userRole"), role);
    }

    public static Specification<User> getCustomers(UserPageRequest request){
        return Specification.where(hasRole(UserRole.CUSTOMER))
                .and(hasKeyword(request.getKeyword()))
                .and(hasFullName(request.getFullName()));
    }
}