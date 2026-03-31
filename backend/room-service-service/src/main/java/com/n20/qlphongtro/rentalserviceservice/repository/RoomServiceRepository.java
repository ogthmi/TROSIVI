package com.n20.qlphongtro.rentalserviceservice.repository;

import com.n20.qlphongtro.rentalserviceservice.entity.RoomService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomService, Long> {
}
