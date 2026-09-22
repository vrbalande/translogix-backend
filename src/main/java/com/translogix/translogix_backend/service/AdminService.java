package com.translogix.translogix_backend.service;

import com.translogix.translogix_backend.dto.AdminCreateShipmentRequest;
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
                .orElseThrow(() ->
                        new RuntimeException(
                                "Shipment not found: " + id
                        )
                );
    }

    // ==========================================================
    // CREATE SHIPMENT - ADMIN
    // IMPORTANT: ASSIGN SHIPMENT TO CUSTOMER
    // ==========================================================

    public Shipment createShipment(
            AdminCreateShipmentRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Shipment data is required"
            );
        }

        // ------------------------------------------------------
        // VALIDATION
        // ------------------------------------------------------

        if (request.getTrackingNumber() == null ||
                request.getTrackingNumber().isBlank()) {

            throw new RuntimeException(
                    "Tracking number is required"
            );
        }

        if (request.getSenderName() == null ||
                request.getSenderName().isBlank()) {

            throw new RuntimeException(
                    "Sender name is required"
            );
        }

        if (request.getReceiverName() == null ||
                request.getReceiverName().isBlank()) {

            throw new RuntimeException(
                    "Receiver name is required"
            );
        }

        if (request.getOrigin() == null ||
                request.getOrigin().isBlank()) {

            throw new RuntimeException(
                    "Origin is required"
            );
        }

        if (request.getDestination() == null ||
                request.getDestination().isBlank()) {

            throw new RuntimeException(
                    "Destination is required"
            );
        }

        if (request.getShipmentType() == null ||
                request.getShipmentType().isBlank()) {

            throw new RuntimeException(
                    "Shipment type is required"
            );
        }

        // ------------------------------------------------------
        // CUSTOMER USERNAME IS REQUIRED
        // ------------------------------------------------------

        if (request.getUsername() == null ||
                request.getUsername().isBlank()) {

            throw new RuntimeException(
                    "Customer username is required"
            );
        }

        // ------------------------------------------------------
        // NORMALIZE VALUES
        // ------------------------------------------------------

        String trackingNumber =
                request.getTrackingNumber().trim();

        String username =
                request.getUsername().trim();

        // ------------------------------------------------------
        // CHECK DUPLICATE TRACKING NUMBER
        // ------------------------------------------------------

        if (shipmentRepository.existsByTrackingNumber(
                trackingNumber)) {

            throw new RuntimeException(
                    "Tracking number already exists: "
                            + trackingNumber
            );
        }

        // ------------------------------------------------------
        // FIND CUSTOMER
        // ------------------------------------------------------

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + username
                                )
                        );

        // ------------------------------------------------------
        // WEIGHT VALIDATION
        // ------------------------------------------------------

        if (request.getWeight() != null &&
                request.getWeight() < 0) {

            throw new RuntimeException(
                    "Weight cannot be negative"
            );
        }

        // ------------------------------------------------------
        // CREATE SHIPMENT ENTITY
        // ------------------------------------------------------

        Shipment shipment = new Shipment();

        shipment.setTrackingNumber(
                trackingNumber
        );

        shipment.setSenderName(
                request.getSenderName().trim()
        );

        shipment.setReceiverName(
                request.getReceiverName().trim()
        );

        shipment.setOrigin(
                request.getOrigin().trim()
        );

        shipment.setDestination(
                request.getDestination().trim()
        );

        shipment.setShipmentType(
                request.getShipmentType().trim()
        );

        // ------------------------------------------------------
        // STATUS
        // ------------------------------------------------------

        if (request.getStatus() == null ||
                request.getStatus().isBlank()) {

            shipment.setStatus("Pending");

        } else {

            shipment.setStatus(
                    request.getStatus().trim()
            );
        }

        // ------------------------------------------------------
        // WEIGHT
        // ------------------------------------------------------

        shipment.setWeight(
                request.getWeight()
        );

        // ======================================================
        // ⭐ VERY IMPORTANT
        // ASSIGN SHIPMENT TO SELECTED CUSTOMER
        // ======================================================

        shipment.setUser(user);

        // ------------------------------------------------------
        // SAVE
        // ------------------------------------------------------

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

        if (updatedShipment == null) {
            throw new RuntimeException(
                    "Shipment data is required"
            );
        }

        // ------------------------------------------------------
        // TRACKING NUMBER
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // SENDER
        // ------------------------------------------------------

        if (updatedShipment.getSenderName() != null &&
                !updatedShipment.getSenderName().isBlank()) {

            shipment.setSenderName(
                    updatedShipment
                            .getSenderName()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // RECEIVER
        // ------------------------------------------------------

        if (updatedShipment.getReceiverName() != null &&
                !updatedShipment.getReceiverName().isBlank()) {

            shipment.setReceiverName(
                    updatedShipment
                            .getReceiverName()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // ORIGIN
        // ------------------------------------------------------

        if (updatedShipment.getOrigin() != null &&
                !updatedShipment.getOrigin().isBlank()) {

            shipment.setOrigin(
                    updatedShipment
                            .getOrigin()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // DESTINATION
        // ------------------------------------------------------

        if (updatedShipment.getDestination() != null &&
                !updatedShipment.getDestination().isBlank()) {

            shipment.setDestination(
                    updatedShipment
                            .getDestination()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // SHIPMENT TYPE
        // ------------------------------------------------------

        if (updatedShipment.getShipmentType() != null &&
                !updatedShipment.getShipmentType().isBlank()) {

            shipment.setShipmentType(
                    updatedShipment
                            .getShipmentType()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // STATUS
        // ------------------------------------------------------

        if (updatedShipment.getStatus() != null &&
                !updatedShipment.getStatus().isBlank()) {

            shipment.setStatus(
                    updatedShipment
                            .getStatus()
                            .trim()
            );
        }

        // ------------------------------------------------------
        // WEIGHT
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // SAVE
        // ------------------------------------------------------

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
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipment not found: "
                                                + trackingNumber
                                )
                        );

        User user =
                userRepository
                        .findByUsername(
                                username.trim()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
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

        return shipmentRepository
                .findByStatusIgnoreCase(
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
                .findByStatusIgnoreCase(
                        "Out for Delivery"
                )
                .size();
    }
}