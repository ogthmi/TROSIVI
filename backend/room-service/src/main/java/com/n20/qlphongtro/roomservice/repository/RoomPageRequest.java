package com.n20.qlphongtro.roomservice.repository;

import com.n20.qlphongtro.roomservice.pagination.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomPageRequest extends BasePageRequest {
    private String roomName;
    private Float areaFrom, areaTo;
    private Float priceFrom, priceTo;
    private Long managerId;
}
