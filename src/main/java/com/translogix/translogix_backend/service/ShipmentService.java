
package com.translogix.translogix_backend.service;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.ShipmentRepository;
import com.translogix.translogix_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getShipmentById(Long id) {
        return shipmentRepository.findById(id);
    }

    public Shipment getShipmentByTrackingNumber(
            String trackingNumber) {

        if (trackingNumber == null ||
                trackingNumber.isBlank()) {
            return null;
        }

        return shipmentRepository
                .findByTrackingNumber(trackingNumber.trim())
                .orElse(null);
    }

    public List<Shipment> getShipmentsByUsername(
            String username) {

        if (username == null || username.isBlank()) {
            return List.of();
        }

        return shipmentRepository
                .findByUserUsernameIgnoreCase(username.trim());
    }

    public Shipment getShipmentByTrackingNumberForUser(
            String trackingNumber,
            String username) {

        if (trackingNumber == null ||
                trackingNumber.isBlank() ||
                username == null ||
                username.isBlank()) {
            return null;
        }

        return shipmentRepository
                .findByTrackingNumberAndUserUsernameIgnoreCase(
                        trackingNumber.trim(),
                        username.trim())
                .orElse(null);
    }

    public List<Shipment> getShipmentsByStatus(
            String status) {

        if (status == null || status.isBlank()) {
            return shipmentRepository.findAll();
        }

        return shipmentRepository
                .findByStatusIgnoreCase(status.trim());
    }

    /*
     * =====================================================
     * CREATE SHIPMENT
     * =====================================================
     */

    public Shipment createShipment(
            Shipment shipment,
            String username) {

        if (shipment == null) {
            throw new RuntimeException(
                    "Shipment data is required");
        }

        if (username == null || username.isBlank()) {
            throw new RuntimeException(
                    "Authenticated username is required");
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

        String trackingNumber =
                shipment.getTrackingNumber().trim();

        if (shipmentRepository
                .existsByTrackingNumber(trackingNumber)) {

            throw new RuntimeException(
                    "Tracking number already exists: "
                            + trackingNumber);
        }

        /*
         * =================================================
         * FIND LOGGED-IN USER
         * =================================================
         */

        User user = userRepository
                .findByUsername(username.trim())
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found: "
                                        + username));

        /*
         * =================================================
         * IMPORTANT: LINK SHIPMENT TO USER
         * =================================================
         */

        shipment.setUser(user);

        shipment.setTrackingNumber(
                trackingNumber);

        if (shipment.getStatus() == null ||
                shipment.getStatus().isBlank()) {

            shipment.setStatus("Pending");
        }

        shipment.setStatus(
                shipment.getStatus().trim());

        shipment.setSenderName(
                shipment.getSenderName().trim());

        shipment.setReceiverName(
                shipment.getReceiverName().trim());

        shipment.setOrigin(
                shipment.getOrigin().trim());

        shipment.setDestination(
                shipment.getDestination().trim());

        if (shipment.getShipmentType() != null) {
            shipment.setShipmentType(
                    shipment.getShipmentType().trim());
        }

        if (shipment.getWeight() != null &&
                shipment.getWeight() < 0) {

            throw new RuntimeException(
                    "Weight cannot be negative");
        }

        /*
         * saveAndFlush forces the INSERT immediately.
         */
        return shipmentRepository.saveAndFlush(
                shipment);
    }

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
                                "Shipment not found: " + id));

        if (request.getTrackingNumber() != null &&
                !request.getTrackingNumber().isBlank()) {

            String newTracking =
                    request.getTrackingNumber().trim();

            boolean trackingChanged =
                    !newTracking.equalsIgnoreCase(
                            existing.getTrackingNumber());

            if (trackingChanged &&
                    shipmentRepository
                            .existsByTrackingNumber(
                                    newTracking)) {

                throw new RuntimeException(
                        "Tracking number already exists: "
                                + newTracking);
            }

            if (trackingChanged) {
                existing.setTrackingNumber(
                        newTracking);
            }
        }

        if (request.getSenderName() != null &&
                !request.getSenderName().isBlank()) {
            existing.setSenderName(
                    request.getSenderName().trim());
        }

        if (request.getReceiverName() != null &&
                !request.getReceiverName().isBlank()) {
            existing.setReceiverName(
                    request.getReceiverName().trim());
        }

        if (request.getOrigin() != null &&
                !request.getOrigin().isBlank()) {
            existing.setOrigin(
                    request.getOrigin().trim());
        }

        if (request.getDestination() != null &&
                !request.getDestination().isBlank()) {
            existing.setDestination(
                    request.getDestination().trim());
        }

        if (request.getShipmentType() != null) {
            existing.setShipmentType(
                    request.getShipmentType().trim());
        }

        if (request.getStatus() != null &&
                !request.getStatus().isBlank()) {
            existing.setStatus(
                    request.getStatus().trim());
        }

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

    public Shipment updateShipmentStatus(
            Long id,
            String status) {

        if (status == null || status.isBlank()) {
            throw new RuntimeException(
                    "Status is required");
        }

        Shipment shipment = shipmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found: " + id));

        shipment.setStatus(status.trim());

        return shipmentRepository.save(shipment);
    }

    public void deleteShipment(Long id) {

        if (!shipmentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Shipment not found: " + id);
        }

        shipmentRepository.deleteById(id);
    }
}

