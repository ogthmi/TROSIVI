package com.n20.qlphongtro.rentalserviceservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_service")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RoomService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private float unitPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomServiceType serviceType;

    private String description;
}
