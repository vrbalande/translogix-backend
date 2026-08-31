package com.translogix.translogix_backend.repository;

import com.translogix.translogix_backend.entity.Shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(
            String trackingNumber);

    List<Shipment> findByStatusIgnoreCase(
            String status);

    boolean existsByTrackingNumber(
            String trackingNumber);
}