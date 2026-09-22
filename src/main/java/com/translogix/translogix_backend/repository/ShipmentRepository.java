package com.translogix.translogix_backend.repository;

import com.translogix.translogix_backend.entity.Shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {

    // =====================================================
    // TRACKING NUMBER
    // =====================================================

    Optional<Shipment> findByTrackingNumber(
            String trackingNumber);

    // =====================================================
    // USER SHIPMENTS
    // =====================================================

    List<Shipment> findByUserUsernameIgnoreCase(
            String username);

    // =====================================================
    // USER ID SHIPMENTS
    // =====================================================

    List<Shipment> findByUserId(
            Long userId);

    // =====================================================
    // USER + TRACKING NUMBER
    // =====================================================

    Optional<Shipment>
    findByTrackingNumberAndUserUsernameIgnoreCase(
            String trackingNumber,
            String username);

    // =====================================================
    // STATUS
    // =====================================================

    List<Shipment> findByStatusIgnoreCase(
            String status);

    // =====================================================
    // DUPLICATE TRACKING NUMBER
    // =====================================================

    boolean existsByTrackingNumber(
            String trackingNumber);
}