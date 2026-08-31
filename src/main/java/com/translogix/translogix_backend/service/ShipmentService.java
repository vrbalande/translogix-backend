package com.translogix.translogix_backend.service;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.repository.ShipmentRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    // =====================================================
    // GET ALL SHIPMENTS
    // =====================================================

    public List<Shipment> getAllShipments() {

        return shipmentRepository.findAll();
    }

    // =====================================================
    // GET SHIPMENT BY ID
    // =====================================================

    public Optional<Shipment> getShipmentById(
            Long id) {

        return shipmentRepository.findById(id);
    }

    // =====================================================
    // GET SHIPMENT BY TRACKING NUMBER
    // =====================================================

    public Shipment getShipmentByTrackingNumber(
            String trackingNumber) {

        if (trackingNumber == null ||
                trackingNumber.isBlank()) {

            return null;
        }

        return shipmentRepository
                .findByTrackingNumber(
                        trackingNumber.trim())
                .orElse(null);
    }

    // =====================================================
    // GET SHIPMENTS BY STATUS
    // =====================================================

    public List<Shipment> getShipmentsByStatus(
            String status) {

        if (status == null ||
                status.isBlank()) {

            return shipmentRepository.findAll();
        }

        return shipmentRepository
                .findByStatusIgnoreCase(
                        status.trim());
    }

    // =====================================================
    // CREATE SHIPMENT
    // =====================================================

    public Shipment createShipment(
            Shipment shipment) {

        if (shipment == null) {

            throw new RuntimeException(
                    "Shipment data is required");
        }

        if (shipment.getTrackingNumber() == null ||
                shipment.getTrackingNumber().isBlank()) {

            throw new RuntimeException(
                    "Tracking number is required");
        }

        if (shipment.getSenderName() == null ||
                shipment.getSenderName().isBlank()) {

            throw new RuntimeException(
                    "Sender name is required");
        }

        if (shipment.getReceiverName() == null ||
                shipment.getReceiverName().isBlank()) {

            throw new RuntimeException(
                    "Receiver name is required");
        }

        if (shipment.getOrigin() == null ||
                shipment.getOrigin().isBlank()) {

            throw new RuntimeException(
                    "Origin is required");
        }

        if (shipment.getDestination() == null ||
                shipment.getDestination().isBlank()) {

            throw new RuntimeException(
                    "Destination is required");
        }

        String trackingNumber = shipment
                .getTrackingNumber()
                .trim();

        // Prevent duplicate tracking number

        if (shipmentRepository
                .existsByTrackingNumber(
                        trackingNumber)) {

            throw new RuntimeException(
                    "Tracking number already exists: "
                            + trackingNumber);
        }

        shipment.setTrackingNumber(
                trackingNumber);

        // Default status

        if (shipment.getStatus() == null ||
                shipment.getStatus().isBlank()) {

            shipment.setStatus(
                    "Pending");
        }

        shipment.setStatus(
                shipment.getStatus().trim());

        // Normalize text

        if (shipment.getSenderName() != null) {

            shipment.setSenderName(
                    shipment.getSenderName().trim());
        }

        if (shipment.getReceiverName() != null) {

            shipment.setReceiverName(
                    shipment.getReceiverName().trim());
        }

        if (shipment.getOrigin() != null) {

            shipment.setOrigin(
                    shipment.getOrigin().trim());
        }

        if (shipment.getDestination() != null) {

            shipment.setDestination(
                    shipment.getDestination().trim());
        }

        if (shipment.getShipmentType() != null) {

            shipment.setShipmentType(
                    shipment.getShipmentType().trim());
        }

        return shipmentRepository.save(
                shipment);
    }

    // =====================================================
    // UPDATE SHIPMENT
    // =====================================================

    public Shipment updateShipment(
            Long id,
            Shipment request) {

        if (request == null) {

            throw new RuntimeException(
                    "Shipment update data is required");
        }

        Shipment existing = shipmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found: "
                                        + id));

        // Tracking number

        if (request.getTrackingNumber() != null &&
                !request.getTrackingNumber().isBlank()) {

            String newTracking = request
                    .getTrackingNumber()
                    .trim();

            boolean trackingChanged = !newTracking.equalsIgnoreCase(
                    existing
                            .getTrackingNumber());

            if (trackingChanged) {

                boolean alreadyExists = shipmentRepository
                        .existsByTrackingNumber(
                                newTracking);

                if (alreadyExists) {

                    throw new RuntimeException(
                            "Tracking number already exists: "
                                    + newTracking);
                }

                existing.setTrackingNumber(
                        newTracking);
            }
        }

        // Sender

        if (request.getSenderName() != null &&
                !request.getSenderName().isBlank()) {

            existing.setSenderName(
                    request
                            .getSenderName()
                            .trim());
        }

        // Receiver

        if (request.getReceiverName() != null &&
                !request.getReceiverName().isBlank()) {

            existing.setReceiverName(
                    request
                            .getReceiverName()
                            .trim());
        }

        // Origin

        if (request.getOrigin() != null &&
                !request.getOrigin().isBlank()) {

            existing.setOrigin(
                    request
                            .getOrigin()
                            .trim());
        }

        // Destination

        if (request.getDestination() != null &&
                !request.getDestination().isBlank()) {

            existing.setDestination(
                    request
                            .getDestination()
                            .trim());
        }

        // Shipment Type

        if (request.getShipmentType() != null) {

            existing.setShipmentType(
                    request
                            .getShipmentType()
                            .trim());
        }

        // Status

        if (request.getStatus() != null &&
                !request.getStatus().isBlank()) {

            existing.setStatus(
                    request
                            .getStatus()
                            .trim());
        }

        // Weight

        if (request.getWeight() != null) {

            if (request.getWeight() < 0) {

                throw new RuntimeException(
                        "Weight cannot be negative");
            }

            existing.setWeight(
                    request.getWeight());
        }

        return shipmentRepository.save(
                existing);
    }

    // =====================================================
    // UPDATE SHIPMENT STATUS
    // =====================================================

    public Shipment updateShipmentStatus(
            Long id,
            String status) {

        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Status is required");
        }

        Shipment shipment = shipmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found: "
                                        + id));

        String newStatus = status.trim();

        shipment.setStatus(
                newStatus);

        return shipmentRepository.save(
                shipment);
    }

    // =====================================================
    // DELETE SHIPMENT
    // =====================================================

    public void deleteShipment(
            Long id) {

        boolean exists = shipmentRepository
                .existsById(id);

        if (!exists) {

            throw new RuntimeException(
                    "Shipment not found: "
                            + id);
        }

        shipmentRepository.deleteById(
                id);
    }

}