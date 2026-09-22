package com.translogix.translogix_backend.service;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.ShipmentRepository;
import com.translogix.translogix_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    public ShipmentService(
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
                                "Shipment not found with id: " + id
                        )
                );
    }

    // ==========================================================
    // GET SHIPMENT BY TRACKING NUMBER
    // ==========================================================

    public Shipment getShipmentByTrackingNumber(
            String trackingNumber) {

        if (trackingNumber == null ||
                trackingNumber.isBlank()) {

            throw new RuntimeException(
                    "Tracking number is required"
            );
        }

        return shipmentRepository
                .findByTrackingNumber(
                        trackingNumber.trim()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found: "
                                        + trackingNumber
                        )
                );
    }

    // ==========================================================
    // GET SHIPMENTS BY LOGGED-IN USER
    // ==========================================================

    public List<Shipment> getShipmentsByUsername(
            String username) {

        if (username == null ||
                username.isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        // ------------------------------------------------------
        // FIND USER
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // GET SHIPMENTS USING USER ID
        // ------------------------------------------------------

        return shipmentRepository
                .findByUserId(
                        user.getId()
                );
    }

    // ==========================================================
    // GET SHIPMENT BY TRACKING NUMBER FOR LOGGED-IN USER
    // ==========================================================

    public Shipment getShipmentByTrackingNumberForUser(
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

        return shipmentRepository
                .findByTrackingNumberAndUserUsernameIgnoreCase(
                        trackingNumber.trim(),
                        username.trim()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found for user: "
                                        + trackingNumber
                        )
                );
    }

    // ==========================================================
    // CREATE SHIPMENT
    // ==========================================================

    public Shipment createShipment(
            Shipment shipment,
            String username) {

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment data is required"
            );
        }

        if (username == null ||
                username.isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        // ------------------------------------------------------
        // REQUIRED FIELD VALIDATION
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // DUPLICATE TRACKING NUMBER CHECK
        // ------------------------------------------------------

        String trackingNumber =
                shipment.getTrackingNumber().trim();

        if (shipmentRepository
                .existsByTrackingNumber(trackingNumber)) {

            throw new RuntimeException(
                    "Tracking number already exists: "
                            + trackingNumber
            );
        }

        // ------------------------------------------------------
        // FIND USER
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // LINK SHIPMENT TO USER
        // ------------------------------------------------------

        shipment.setUser(user);

        // ------------------------------------------------------
        // NORMALIZE DATA
        // ------------------------------------------------------

        shipment.setTrackingNumber(
                trackingNumber
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

        // ------------------------------------------------------
        // DEFAULT STATUS
        // ------------------------------------------------------

        if (shipment.getStatus() == null ||
                shipment.getStatus().isBlank()) {

            shipment.setStatus("Pending");

        } else {

            shipment.setStatus(
                    shipment.getStatus().trim()
            );
        }

        // ------------------------------------------------------
        // WEIGHT VALIDATION
        // ------------------------------------------------------

        if (shipment.getWeight() != null &&
                shipment.getWeight() < 0) {

            throw new RuntimeException(
                    "Weight cannot be negative"
            );
        }

        // ------------------------------------------------------
        // SAVE SHIPMENT
        // ------------------------------------------------------

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // ASSIGN EXISTING SHIPMENT TO USER
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

        // ------------------------------------------------------
        // FIND EXISTING SHIPMENT
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // FIND USER
        // ------------------------------------------------------

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

        // ------------------------------------------------------
        // LINK SHIPMENT TO USER
        // ------------------------------------------------------

        shipment.setUser(user);

        // ------------------------------------------------------
        // SAVE CHANGES
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

        Shipment existingShipment =
                shipmentRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Shipment not found with id: "
                                                + id
                                )
                        );

        if (updatedShipment.getTrackingNumber() != null) {
            existingShipment.setTrackingNumber(
                    updatedShipment.getTrackingNumber()
            );
        }

        if (updatedShipment.getSenderName() != null) {
            existingShipment.setSenderName(
                    updatedShipment.getSenderName()
            );
        }

        if (updatedShipment.getReceiverName() != null) {
            existingShipment.setReceiverName(
                    updatedShipment.getReceiverName()
            );
        }

        if (updatedShipment.getOrigin() != null) {
            existingShipment.setOrigin(
                    updatedShipment.getOrigin()
            );
        }

        if (updatedShipment.getDestination() != null) {
            existingShipment.setDestination(
                    updatedShipment.getDestination()
            );
        }

        if (updatedShipment.getShipmentType() != null) {
            existingShipment.setShipmentType(
                    updatedShipment.getShipmentType()
            );
        }

        if (updatedShipment.getStatus() != null) {
            existingShipment.setStatus(
                    updatedShipment.getStatus()
            );
        }

        if (updatedShipment.getWeight() != null) {
            existingShipment.setWeight(
                    updatedShipment.getWeight()
            );
        }

        return shipmentRepository.saveAndFlush(
                existingShipment
        );
    }

    // ==========================================================
    // UPDATE SHIPMENT STATUS
    // ==========================================================

    public Shipment updateShipmentStatus(
            Long id,
            String status) {

        Shipment shipment =
                shipmentRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Shipment not found with id: "
                                                + id
                                )
                        );

        if (status == null ||
                status.isBlank()) {

            throw new RuntimeException(
                    "Status is required"
            );
        }

        shipment.setStatus(
                status.trim()
        );

        return shipmentRepository.saveAndFlush(
                shipment
        );
    }

    // ==========================================================
    // DELETE SHIPMENT
    // ==========================================================

    public void deleteShipment(Long id) {

        Shipment shipment =
                shipmentRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Shipment not found with id: "
                                                + id
                                )
                        );

        shipmentRepository.delete(
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
}