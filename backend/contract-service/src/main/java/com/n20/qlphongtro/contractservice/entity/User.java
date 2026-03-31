package com.n20.qlphongtro.contractservice.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private Long userId;

    private String fullName;

    private String phone;

    private String email;
}
