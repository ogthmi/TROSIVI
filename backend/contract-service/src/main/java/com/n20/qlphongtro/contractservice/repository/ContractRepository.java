package com.n20.qlphongtro.contractservice.repository;

import com.n20.qlphongtro.contractservice.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
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

    @Query(value =
            """
                SELECT DISTINCT c.roomId
                FROM Contract c
                WHERE c.roomId IN :roomIds
                AND c.status IN ('PENDING', 'ACTIVE')
                AND (
                    :startDate <= c.endDate AND :endDate >= c.startDate
                )
            """
    )
    List<Long> findRoomIdsHavingOverlappingContract(
            List<Long> roomIds,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<Contract> findAll(Specification<Contract> specification, Pageable pageable);
}
