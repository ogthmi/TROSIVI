package com.n20.qlphongtro.contractservice.repository;

import com.n20.qlphongtro.contractservice.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query(
            """
                SELECT COUNT(c) > 0
                FROM Contract c
                WHERE c.roomId = :roomId
                AND c.status IN ('PENDING', 'ACTIVE')
                AND (
                    :startDate <= c.endDate AND :endDate >= c.startDate
                )
            """
    )
    boolean existsOverlappingContract(Long roomId, LocalDate startDate, LocalDate endDate);
}
