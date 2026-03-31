package com.n20.qlphongtro.contractservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_contract_service")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContractedService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractServiceId;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private float unitPrice;

    @Column(nullable = false)
    private Long serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractId")
    @JsonIgnore
    private Contract contract;

    @Transient
    private RoomService service;
}
