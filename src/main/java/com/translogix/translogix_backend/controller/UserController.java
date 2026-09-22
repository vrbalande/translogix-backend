
package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.repository.ShipmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final ShipmentRepository shipmentRepository;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public UserController(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    // ==========================================
    // TRACK SHIPMENT BY TRACKING NUMBER
    // ==========================================

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<?> trackShipment(
            @PathVariable String trackingNumber) {

        Optional<Shipment> shipment =
                shipmentRepository.findByTrackingNumber(trackingNumber);

        if (shipment.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Shipment not found.");
        }

        return ResponseEntity.ok(shipment.get());
    }

    // ==========================================
    // GET USER SHIPMENT BY ID
    // ==========================================

    @GetMapping("/shipments/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        Optional<Shipment> shipment =
                shipmentRepository.findById(id);

        if (shipment.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Shipment not found.");
        }

        Shipment foundShipment = shipment.get();

        // Make sure shipment belongs to logged-in user
        if (foundShipment.getUser() == null ||
                foundShipment.getUser().getUsername() == null ||
                !foundShipment.getUser()
                        .getUsername()
                        .equalsIgnoreCase(username)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to access this shipment.");
        }

        return ResponseEntity.ok(foundShipment);
    }

    // ==========================================
    // VIEW LOGGED-IN USER SHIPMENTS
    // ==========================================

    @GetMapping("/shipments")
    public ResponseEntity<List<Shipment>> getMyShipments(
            Authentication authentication) {

        String username = authentication.getName();

        List<Shipment> shipments =
                shipmentRepository
                        .findByUserUsernameIgnoreCase(username);

        return ResponseEntity.ok(shipments);
    }

    // ==========================================
    // SEARCH USER SHIPMENTS BY STATUS
    // ==========================================

    @GetMapping("/shipments/status/{status}")
    public ResponseEntity<List<Shipment>> getShipmentsByStatus(
            @PathVariable String status,
            Authentication authentication) {

        String username = authentication.getName();

        List<Shipment> shipments =
                shipmentRepository
                        .findByUserUsernameIgnoreCase(username)
                        .stream()
                        .filter(shipment ->
                                shipment.getStatus() != null &&
                                shipment.getStatus()
                                        .equalsIgnoreCase(status))
                        .toList();

        return ResponseEntity.ok(shipments);
    }

    // ==========================================
    // USER PROFILE
    // ==========================================

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                Map.of(
                        "username", username,
                        "message",
                        "User profile retrieved successfully"
                )
        );
    }

    // ==========================================
    // DEBUG SHIPMENT MAPPING
    // TEMPORARY - REMOVE AFTER TESTING
    // ==========================================

    @GetMapping("/debug/shipment-mapping")
    public ResponseEntity<?> debugShipmentMapping(
            Authentication authentication) {

        String username = authentication.getName();

        List<Shipment> shipments =
                shipmentRepository.findAll();

        List<Map<String, Object>> result =
                shipments.stream()
                        .map(shipment -> {

                            Map<String, Object> data =
                                    new HashMap<>();

                            data.put(
                                    "id",
                                    shipment.getId()
                            );

                            data.put(
                                    "trackingNumber",
                                    shipment.getTrackingNumber()
                            );

                            data.put(
                                    "status",
                                    shipment.getStatus()
                            );

                            if (shipment.getUser() != null) {

                                data.put(
                                        "userId",
                                        shipment.getUser().getId()
                                );

                                data.put(
                                        "username",
                                        shipment.getUser().getUsername()
                                );

                            } else {

                                data.put(
                                        "userId",
                                        null
                                );

                                data.put(
                                        "username",
                                        null
                                );
                            }

                            return data;
                        })
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "loggedInUser",
                        username,

                        "shipments",
                        result
                )
        );
    }

    // ==========================================
    // USER DASHBOARD SUMMARY
    // ==========================================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            Authentication authentication) {

        String username = authentication.getName();

        List<Shipment> shipments =
                shipmentRepository
                        .findByUserUsernameIgnoreCase(username);

        // ==========================================
        // TOTAL SHIPMENTS
        // ==========================================

        long totalShipments = shipments.size();

        // ==========================================
        // DELIVERED
        // ==========================================

        long delivered = shipments.stream()
                .filter(shipment ->
                        shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Delivered"))
                .count();

        // ==========================================
        // IN TRANSIT
        // ==========================================

        long inTransit = shipments.stream()
                .filter(shipment ->
                        shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("In Transit"))
                .count();

        // ==========================================
        // PENDING
        // ==========================================

        long pending = shipments.stream()
                .filter(shipment ->
                        shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Pending"))
                .count();

        // ==========================================
        // OUT FOR DELIVERY
        // ==========================================

        long outForDelivery = shipments.stream()
                .filter(shipment ->
                        shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Out for Delivery"))
                .count();

        // ==========================================
        // RESPONSE
        // ==========================================

        return ResponseEntity.ok(
                Map.of(
                        "totalShipments",
                        totalShipments,

                        "delivered",
                        delivered,

                        "inTransit",
                        inTransit,

                        "pending",
                        pending,

                        "outForDelivery",
                        outForDelivery
                )
        );
    }
}

