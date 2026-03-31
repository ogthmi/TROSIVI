package com.n20.qlphongtro.userservice.repository;

import com.n20.qlphongtro.userservice.pagination.BasePageRequest;
import com.n20.qlphongtro.userservice.entity.UserRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserPageRequest extends BasePageRequest {
    private Long userId;
    private UserRole userRole;
    private String fullName;
    private String phone;
}
