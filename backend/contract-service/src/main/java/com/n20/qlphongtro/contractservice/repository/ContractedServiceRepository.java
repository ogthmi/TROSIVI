package com.n20.qlphongtro.contractservice.repository;

import com.n20.qlphongtro.contractservice.entity.ContractedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractedServiceRepository extends JpaRepository<ContractedService, Long> {
    List<ContractedService> findByContract_ContractId(Long contractId);
}