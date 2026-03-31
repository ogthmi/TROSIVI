package com.n20.qlphongtro.contractservice.entity;

import jakarta.persistence.Transient;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Room {
    private Long roomId;
    private String roomName;
    private float area;
    private float price;
    private String description;
    private User manager;
}
