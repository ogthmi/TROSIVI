package com.n20.qlphongtro.contractservice.dto;

import com.n20.qlphongtro.contractservice.entity.Contract;
import com.n20.qlphongtro.contractservice.entity.ContractedService;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ContractPayload {
    private Contract contract;
    private List<ContractedService> contractedServices;
}
