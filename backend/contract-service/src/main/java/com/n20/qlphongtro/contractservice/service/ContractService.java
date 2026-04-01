package com.n20.qlphongtro.contractservice.service;

import com.n20.qlphongtro.contractservice.dto.ContractPayload;
import com.n20.qlphongtro.contractservice.entity.*;
import com.n20.qlphongtro.contractservice.exception.BusinessException;
import com.n20.qlphongtro.contractservice.pagination.PageUtil;
import com.n20.qlphongtro.contractservice.repository.ContractPageRequest;
import com.n20.qlphongtro.contractservice.repository.ContractRepository;
import com.n20.qlphongtro.contractservice.repository.ContractSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    private User getManagerById(Long userId) {
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("lb://user-service/api/manager/{userId}", userId)
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
            throw new BusinessException("Room is already rented in this time");
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

    public Page<Contract> getContracts(ContractPageRequest pageRequest) {
        final List<String> CONTRACT_SORT_FIELDS = List.of(
                "contractId", "managerId", "customerId", "roomId",
                "rentalPrice", "deposit", "startDate", "endDate"
        );

        Sort sort = PageUtil.buildSort(pageRequest.getSortBy(), pageRequest.getDirection(), CONTRACT_SORT_FIELDS);

        Pageable pageable = PageUtil.buildPageable(pageRequest, sort);

        Specification<Contract> specification = ContractSpecification.build(pageRequest);

        Page<Contract> contractPage = contractRepository.findAll(specification, pageable);

        contractPage.getContent().forEach(c -> {
            var customer = getCustomerById(c.getCustomerId());
            var manager = getManagerById(c.getManagerId());
            var room = getRoomById(c.getRoomId());

            c.setCustomer(customer);
            c.setManager(manager);
            c.setRoom(room);
        });

        return contractPage;
    }

    public Map<Long, Boolean> checkRoomExistContract(LocalDate startDate, LocalDate endDate, String roomIdString) {
        List<Long> roomIds = Arrays.stream(roomIdString.split(","))
                .map(Long::parseLong).toList();

        List<Long> overlappedRoomIds = contractRepository
                .findRoomIdsHavingOverlappingContract(roomIds, startDate, endDate)
                .stream().distinct().toList();

        return roomIds.stream()
                .collect(Collectors.toMap(
                        roomId -> roomId,
                        roomId -> overlappedRoomIds.contains(roomId)
                ));
    }
}
