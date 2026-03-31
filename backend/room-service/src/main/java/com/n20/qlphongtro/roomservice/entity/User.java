package com.n20.qlphongtro.roomservice.entity;

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
