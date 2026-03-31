package com.n20.qlphongtro.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String fullName;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String userRole;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean deleted = false;
}
