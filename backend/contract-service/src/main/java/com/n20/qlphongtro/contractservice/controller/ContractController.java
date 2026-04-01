package com.n20.qlphongtro.contractservice.controller;

import com.n20.qlphongtro.contractservice.dto.ContractPayload;
import com.n20.qlphongtro.contractservice.entity.Contract;
import com.n20.qlphongtro.contractservice.repository.ContractPageRequest;
import com.n20.qlphongtro.contractservice.service.ContractService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<Page<Contract>> getContracts(@ModelAttribute ContractPageRequest pageRequest) {
        return ResponseEntity.ok(contractService.getContracts(pageRequest));
    }

    @GetMapping("/check/exist")
    public ResponseEntity<Map<Long, Boolean>> checkRoomExistContract(
            @RequestParam(value = "startDate") LocalDate startDate,
            @RequestParam(value = "endDate") LocalDate endDate,
            @RequestParam(value = "ids") String roomIds
    ) {
        return ResponseEntity.ok(contractService.checkRoomExistContract(startDate, endDate, roomIds));
    }
}
