
package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.service.ShipmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(
            ShipmentService shipmentService) {

        this.shipmentService = shipmentService;
    }

    // ==========================================================
    // GET ALL SHIPMENTS
    // ==========================================================

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {

        return ResponseEntity.ok(
                shipmentService.getAllShipments()
        );
    }

    // ==========================================================
    // GET SHIPMENT BY ID
    // ==========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Long id) {

        try {

            Shipment shipment =
                    shipmentService.getShipmentById(id);

            return ResponseEntity.ok(shipment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // GET SHIPMENT BY TRACKING NUMBER
    // ==========================================================

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<?> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {

        try {

            Shipment shipment =
                    shipmentService
                            .getShipmentByTrackingNumber(
                                    trackingNumber
                            );

            return ResponseEntity.ok(shipment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // CREATE SHIPMENT
    // ==========================================================

    @PostMapping
    public ResponseEntity<?> createShipment(
            @RequestBody Shipment shipment,
            Authentication authentication) {

        try {

            String username =
                    authentication.getName();

            Shipment created =
                    shipmentService.createShipment(
                            shipment,
                            username
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // ASSIGN EXISTING SHIPMENT TO USER
    //
    // Example:
    // PATCH /api/shipments/assign-user
    // ?trackingNumber=TRX10001&username=vijay
    // ==========================================================

    @PatchMapping("/assign-user")
    public ResponseEntity<?> assignShipmentToUser(
            @RequestParam String trackingNumber,
            @RequestParam String username) {

        try {

            Shipment updated =
                    shipmentService.assignShipmentToUser(
                            trackingNumber,
                            username
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // UPDATE SHIPMENT
    // ==========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShipment(
            @PathVariable Long id,
            @RequestBody Shipment shipment) {

        try {

            Shipment updated =
                    shipmentService.updateShipment(
                            id,
                            shipment
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // UPDATE SHIPMENT STATUS
    // ==========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            Shipment updated =
                    shipmentService.updateShipmentStatus(
                            id,
                            status
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // DELETE SHIPMENT
    // ==========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable Long id) {

        try {

            shipmentService.deleteShipment(id);

            return ResponseEntity
                    .ok("Shipment deleted successfully");

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ==========================================================
    // GET SHIPMENTS BY STATUS
    // ==========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getShipmentsByStatus(
            @PathVariable String status) {

        try {

            List<Shipment> shipments =
                    shipmentService
                            .getShipmentsByStatus(status);

            return ResponseEntity.ok(shipments);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}

