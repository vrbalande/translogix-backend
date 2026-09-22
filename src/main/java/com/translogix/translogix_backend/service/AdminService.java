package com.translogix.translogix_backend.service;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.ShipmentRepository;
import com.translogix.translogix_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    public AdminService(
            ShipmentRepository shipmentRepository,
            UserRepository userRepository) {

        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
    }

    // ==========================================================
    // GET ALL SHIPMENTS
    // ==========================================================

    public List<Shipment> getAllShipments() {

        return shipmentRepository.findAll();
    }

    // ==========================================================
    // GET SHIPMENT BY ID
    // ==========================================================

    public Shipment getShipmentById(Long id) {

        return shipmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found: " + id
                        )
                );
    }

    // ==========================================================
    // CREATE SHIPMENT
    // ==========================================================

    public Shipment createShipment(Shipment shipment) {

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment data is required"
            );
        }

        if (shipment.getTrackingNumber() == null ||
                shipment.getTrackingNumber().isBlank()) {

            throw new RuntimeException(
                    "Tracking number is required"
            );
        }

        if (shipment.getSenderName() == null ||
                shipment.getSenderName().isBlank()) {

            throw new RuntimeException(
                    "Sender name is required"
            );
        }

        if (shipment.getReceiverName() == null ||
                shipment.getReceiverName().isBlank()) {

            throw new RuntimeException(
                    "Receiver name is required"
            );
        }

        if (shipment.getOrigin() == null ||
                shipment.getOrigin().isBlank()) {

            throw new RuntimeException(
                    "Origin is required"
            );
        }

        if (shipment.getDestination() == null ||
                shipment.getDestination().isBlank()) {

            throw new RuntimeException(
                    "Destination is required"
            );
        }

        if (shipmentRepository.existsByTrackingNumber(
                shipment.getTrackingNumber().trim())) {

            throw new RuntimeException(
                    "Tracking number already exists: "
                            + shipment.getTrackingNumber()
            );
        }

        shipment.setTrackingNumber(
                shipment.getTrackingNumber().trim()
        );

        shipment.setSenderName(
                shipment.getSenderName().trim()
        );

        shipment.setReceiverName(
                shipment.getReceiverName().trim()
        );

        shipment.setOrigin(
                shipment.getOrigin().trim()
        );

        shipment.setDestination(
                shipment.getDestination().trim()
        );

        if (shipment.getShipmentType() != null) {

            shipment.setShipmentType(
                    shipment.getShipmentType().trim()
            );
        }

        if (shipment.getStatus() == null ||
                shipment.getStatus().isBlank()) {

            shipment.setStatus("Pending");

        } else {

            shipment.setStatus(
                    shipment.getStatus().trim()
            );
        }

        if (shipment.getWeight() != null &&
                shipment.getWeight() < 0) {

            throw new RuntimeException(
                    "Weight cannot be negative"
            );
        }

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // UPDATE SHIPMENT
    // ==========================================================

    public Shipment updateShipment(
            Long id,
            Shipment updatedShipment) {

        Shipment shipment =
                getShipmentById(id);

        if (updatedShipment.getTrackingNumber() != null &&
                !updatedShipment.getTrackingNumber().isBlank()) {

            String trackingNumber =
                    updatedShipment
                            .getTrackingNumber()
                            .trim();

            if (!trackingNumber.equalsIgnoreCase(
                    shipment.getTrackingNumber())) {

                if (shipmentRepository.existsByTrackingNumber(
                        trackingNumber)) {

                    throw new RuntimeException(
                            "Tracking number already exists: "
                                    + trackingNumber
                    );
                }

                shipment.setTrackingNumber(
                        trackingNumber
                );
            }
        }

        if (updatedShipment.getSenderName() != null) {

            shipment.setSenderName(
                    updatedShipment
                            .getSenderName()
                            .trim()
            );
        }

        if (updatedShipment.getReceiverName() != null) {

            shipment.setReceiverName(
                    updatedShipment
                            .getReceiverName()
                            .trim()
            );
        }

        if (updatedShipment.getOrigin() != null) {

            shipment.setOrigin(
                    updatedShipment
                            .getOrigin()
                            .trim()
            );
        }

        if (updatedShipment.getDestination() != null) {

            shipment.setDestination(
                    updatedShipment
                            .getDestination()
                            .trim()
            );
        }

        if (updatedShipment.getShipmentType() != null) {

            shipment.setShipmentType(
                    updatedShipment
                            .getShipmentType()
                            .trim()
            );
        }

        if (updatedShipment.getStatus() != null &&
                !updatedShipment.getStatus().isBlank()) {

            shipment.setStatus(
                    updatedShipment
                            .getStatus()
                            .trim()
            );
        }

        if (updatedShipment.getWeight() != null) {

            if (updatedShipment.getWeight() < 0) {

                throw new RuntimeException(
                        "Weight cannot be negative"
                );
            }

            shipment.setWeight(
                    updatedShipment.getWeight()
            );
        }

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // DELETE SHIPMENT
    // ==========================================================

    public void deleteShipment(Long id) {

        if (!shipmentRepository.existsById(id)) {

            throw new RuntimeException(
                    "Shipment not found: " + id
            );
        }

        shipmentRepository.deleteById(id);
    }

    // ==========================================================
    // UPDATE STATUS
    // ==========================================================

    public Shipment updateShipmentStatus(
            Long id,
            String status) {

        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Status is required"
            );
        }

        Shipment shipment =
                getShipmentById(id);

        shipment.setStatus(
                status.trim()
        );

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // ASSIGN SHIPMENT TO USER
    // ==========================================================

    public Shipment assignShipmentToUser(
            String trackingNumber,
            String username) {

        if (trackingNumber == null ||
                trackingNumber.isBlank()) {

            throw new RuntimeException(
                    "Tracking number is required"
            );
        }

        if (username == null ||
                username.isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        Shipment shipment =
                shipmentRepository
                        .findByTrackingNumber(
                                trackingNumber.trim()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Shipment not found: "
                                                + trackingNumber
                                )
                        );

        User user =
                userRepository
                        .findByUsername(
                                username.trim()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found: "
                                                + username
                                )
                        );

        shipment.setUser(user);

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // GET SHIPMENTS BY STATUS
    // ==========================================================

    public List<Shipment> getShipmentsByStatus(
            String status) {

        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Status is required"
            );
        }

        return shipmentRepository.findByStatusIgnoreCase(
                status.trim()
        );
    }

    // ==========================================================
    // DASHBOARD STATISTICS
    // ==========================================================

    public long getTotalShipments() {

        return shipmentRepository.count();
    }

    public long getPendingShipments() {

        return shipmentRepository
                .findByStatusIgnoreCase("Pending")
                .size();
    }

    public long getInTransitShipments() {

        return shipmentRepository
                .findByStatusIgnoreCase("In Transit")
                .size();
    }

    public long getDeliveredShipments() {

        return shipmentRepository
                .findByStatusIgnoreCase("Delivered")
                .size();
    }

    public long getOutForDeliveryShipments() {

        return shipmentRepository
                .findByStatusIgnoreCase("Out for Delivery")
                .size();
    }
}