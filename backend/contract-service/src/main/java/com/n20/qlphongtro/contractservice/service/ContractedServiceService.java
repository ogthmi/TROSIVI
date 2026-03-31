package com.n20.qlphongtro.contractservice.service;

import com.n20.qlphongtro.contractservice.entity.Contract;
import com.n20.qlphongtro.contractservice.entity.ContractedService;
import com.n20.qlphongtro.contractservice.entity.RoomService;
import com.n20.qlphongtro.contractservice.exception.BusinessException;
import com.n20.qlphongtro.contractservice.repository.ContractedServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContractedServiceService {

    private final ContractedServiceRepository repository;
    private final WebClient.Builder webClientBuilder;

    private List<RoomService> getRoomServiceByIds(List<Long> roomServiceIds) {
        WebClient webClient = webClientBuilder.build();

        String ids = roomServiceIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        List<RoomService> roomServices = webClient.get()
                .uri("lb://room-service-service/api/service/batch?ids={ids}", ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoomService>>() {
                })
                .block();

        if (roomServices == null || roomServices.size() != roomServiceIds.size()) {
            throw new BusinessException("Some RoomService IDs are invalid");
        }

        return roomServices;
    }

    private void validateContractService(ContractedService cs) {
        if (cs.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be greater than 0");
        }
        if (cs.getServiceId() == null) {
            throw new BusinessException("Service ID is required");
        }
    }

    public List<ContractedService> createContractedServices(
            Contract contract, List<ContractedService> contractedServices
    ) {
        List<Long> roomServiceIds = contractedServices.stream()
                .map(ContractedService::getServiceId)
                .toList();

        List<RoomService> roomServices = getRoomServiceByIds(roomServiceIds);
        Map<Long, RoomService> roomServiceMap = roomServices.stream()
                .collect(Collectors.toMap(RoomService::getServiceId, rs -> rs));

        for (ContractedService cs : contractedServices) {
            validateContractService(cs);

            RoomService roomService = roomServiceMap.get(cs.getServiceId());
            if (roomService == null) {
                throw new BusinessException("RoomService ID " + cs.getServiceId() + " not found");
            }

            cs.setContract(contract);
            cs.setStartDate(contract.getStartDate());
            cs.setEndDate(contract.getEndDate());

            cs.setService(roomService);
            cs.setUnitPrice(roomService.getUnitPrice());
        }

        return repository.saveAll(contractedServices);
    }

    public List<ContractedService> getContractedServicesByContractId(Long contractId) {
        List<ContractedService> contractedServices = repository.findByContract_ContractId(contractId);

        List<Long> serviceIds = contractedServices.stream().map(ContractedService::getServiceId).toList();

        List<RoomService> roomServices = getRoomServiceByIds(serviceIds);
        Map<Long, RoomService> roomServiceMap = roomServices.stream()
                .collect(Collectors.toMap(RoomService::getServiceId, rs -> rs));

        for (ContractedService cs : contractedServices) {
            RoomService roomService = roomServiceMap.get(cs.getServiceId());
            if (roomService == null) {
                throw new BusinessException("RoomService ID " + cs.getServiceId() + " not found");
            }

            cs.setService(roomService);

        }
        return contractedServices;
    }

}