
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

        Optional<Shipment> shipment = shipmentRepository.findByTrackingNumber(trackingNumber);

        if (shipment.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Shipment not found.");
        }

        return ResponseEntity.ok(shipment.get());
    }

    // ==========================================
    // GET SHIPMENT BY ID
    // ==========================================

    @GetMapping("/shipments/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Long id) {

        Optional<Shipment> shipment = shipmentRepository.findById(id);

        if (shipment.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Shipment not found.");
        }

        return ResponseEntity.ok(shipment.get());
    }

    // ==========================================
    // VIEW ALL SHIPMENTS
    // ==========================================

    @GetMapping("/shipments")
    public ResponseEntity<List<Shipment>> getAllShipments() {

        List<Shipment> shipments = shipmentRepository.findAll();

        return ResponseEntity.ok(shipments);
    }

    // ==========================================
    // SEARCH SHIPMENTS BY STATUS
    // ==========================================

    @GetMapping("/shipments/status/{status}")
    public ResponseEntity<List<Shipment>> getShipmentsByStatus(
            @PathVariable String status) {

        List<Shipment> shipments = shipmentRepository.findByStatusIgnoreCase(status);

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
                        "message", "User profile retrieved successfully"));
    }

    // ==========================================
    // USER DASHBOARD SUMMARY
    // ==========================================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {

        List<Shipment> shipments = shipmentRepository.findAll();

        // Total shipments
        long totalShipments = shipments.size();

        // Delivered shipments
        long delivered = shipments.stream()
                .filter(shipment -> shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Delivered"))
                .count();

        // In Transit shipments
        long inTransit = shipments.stream()
                .filter(shipment -> shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("In Transit"))
                .count();

        // Pending shipments
        long pending = shipments.stream()
                .filter(shipment -> shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Pending"))
                .count();

        // Out for Delivery shipments
        long outForDelivery = shipments.stream()
                .filter(shipment -> shipment.getStatus() != null &&
                        shipment.getStatus()
                                .equalsIgnoreCase("Out for Delivery"))
                .count();

        return ResponseEntity.ok(
                Map.of(
                        "totalShipments", totalShipments,
                        "delivered", delivered,
                        "inTransit", inTransit,
                        "pending", pending,
                        "outForDelivery", outForDelivery));
    }
}
