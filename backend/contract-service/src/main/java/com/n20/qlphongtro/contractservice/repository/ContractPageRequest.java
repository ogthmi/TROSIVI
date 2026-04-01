package com.n20.qlphongtro.contractservice.repository;

import com.n20.qlphongtro.contractservice.entity.ContractStatus;
import com.n20.qlphongtro.contractservice.pagination.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractPageRequest extends BasePageRequest {
    private Date startDate, endDate;
    private Long customerId, managerId, roomId;
    private ContractStatus status;
    private float depositFrom, depositTo;
    private float rentalPriceFrom, rentalPriceTo;
}
