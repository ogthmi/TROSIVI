package com.n20.qlphongtro.contractservice.controller;

import com.n20.qlphongtro.contractservice.dto.ContractPayload;
import com.n20.qlphongtro.contractservice.service.ContractService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractPayload> createContract(
            @RequestBody ContractPayload contractRequest
    ) {
        var contract = contractRequest.getContract();
        var contractedServices = contractRequest.getContractedServices();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contractService.createContract(contract, contractedServices));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractPayload> getContractId(@PathVariable Long contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }
}
