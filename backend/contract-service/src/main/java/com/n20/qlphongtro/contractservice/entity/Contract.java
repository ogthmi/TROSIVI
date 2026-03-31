package com.n20.qlphongtro.contractservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_contract")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private float rentalPrice;

    @Column(nullable = false)
    private float deposit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private Long customerId, managerId;

    private Long roomId;

    @Transient
    private User customer, manager;

    @Transient
    private Room room;
}
