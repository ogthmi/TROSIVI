package com.n20.qlphongtro.contractservice.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RoomService {
    private Long serviceId;
    private String serviceName;
    private String unit;
    private float unitPrice;
    private String serviceType;
    private String description;
}
