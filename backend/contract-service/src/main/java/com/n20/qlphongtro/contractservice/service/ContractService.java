package com.n20.qlphongtro.contractservice.service;

import com.n20.qlphongtro.contractservice.dto.ContractPayload;
import com.n20.qlphongtro.contractservice.entity.*;
import com.n20.qlphongtro.contractservice.exception.BusinessException;
import com.n20.qlphongtro.contractservice.repository.ContractRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final ContractedServiceService contractedServiceService;
    private final WebClient.Builder webClientBuilder;

    private User getCustomerById(Long userId) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("lb://user-service/api/customer/{userId}", userId)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    private Room getRoomById(Long roomId) {
        WebClient webClient = webClientBuilder.build();
        return webClient.get()
                .uri("lb://room-service/api/room/{roomId}", roomId)
                .retrieve()
                .bodyToMono(Room.class)
                .block();
    }

    private void validateContractRequest(Contract contract) {
        if (contract.getCustomerId() == null) {
            throw new BusinessException("Customer id is required");
        }

        if (contract.getRoomId() == null) {
            throw new BusinessException("Room id is required");
        }

        if (contract.getStartDate() == null || contract.getEndDate() == null) {
            throw new BusinessException("Start date and end date are required");
        }

        if (contract.getStartDate().isAfter(contract.getEndDate())) {
            throw new BusinessException("Start date must be before end date");
        }

        boolean isOverlap = contractRepository.existsOverlappingContract(
                contract.getRoomId(),
                contract.getStartDate(),
                contract.getEndDate()
        );

        if (isOverlap) {
            throw new RuntimeException("Room is already rented in this time");
        }

    }

    @Transactional
    public ContractPayload createContract(Contract contract, List<ContractedService> contractedServices) {
        validateContractRequest(contract);

        var customer = getCustomerById(contract.getCustomerId());
        var room = getRoomById(contract.getRoomId());

        var newContract = saveContract(contract, customer, room);

        var newContractedServices = contractedServiceService
                .createContractedServices(newContract, contractedServices);

        return ContractPayload.builder()
                .contract(newContract)
                .contractedServices(newContractedServices)
                .build();
    }
    protected Contract saveContract(Contract request, User customer, Room room) {
        request.setContractId(null);
        request.setStatus(ContractStatus.PENDING);
        request.setManagerId(room.getManager().getUserId());
        request.setRentalPrice(room.getPrice());

        var newContract = contractRepository.save(request);

        newContract.setCustomer(customer);
        newContract.setRoom(room);
        newContract.setManager(room.getManager());

        return newContract;
    }

    public ContractPayload getContractById(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));

        List<ContractedService> contractedServices = contractedServiceService
                .getContractedServicesByContractId(contractId);

        return ContractPayload.builder()
                .contract(contract)
                .contractedServices(contractedServices)
                .build();
    }
}
