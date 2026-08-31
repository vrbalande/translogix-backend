package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.service.ShipmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(
            ShipmentService shipmentService) {

        this.shipmentService = shipmentService;
    }

    /*
     * =====================================================
     * GET ALL SHIPMENTS
     * =====================================================
     */

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {

        return ResponseEntity.ok(
                shipmentService.getAllShipments());
    }

    /*
     * =====================================================
     * GET SHIPMENT BY ID
     * =====================================================
     */

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(
            @PathVariable Long id) {

        return shipmentService
                .getShipmentById(id)
                .map(
                        ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity
                                .notFound()
                                .build());
    }

    /*
     * =====================================================
     * TRACKING
     * =====================================================
     */

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Shipment> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {

        Shipment shipment = shipmentService
                .getShipmentByTrackingNumber(
                        trackingNumber);

        if (shipment == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                shipment);
    }

    /*
     * =====================================================
     * GET BY STATUS
     * =====================================================
     */

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shipment>> getShipmentsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                shipmentService
                        .getShipmentsByStatus(
                                status));
    }

    /*
     * =====================================================
     * CREATE
     * =====================================================
     */

    @PostMapping
    public ResponseEntity<?> createShipment(
            @RequestBody Shipment shipment) {

        try {

            if (shipment
                    .getTrackingNumber() == null
                    ||
                    shipment
                            .getTrackingNumber()
                            .isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Tracking number is required");
            }

            if (shipment
                    .getStatus() == null
                    ||
                    shipment
                            .getStatus()
                            .isBlank()) {

                shipment.setStatus(
                        "Pending");
            }

            Shipment created = shipmentService
                    .createShipment(
                            shipment);

            return ResponseEntity
                    .status(
                            HttpStatus.CREATED)
                    .body(
                            created);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.BAD_REQUEST)
                    .body(
                            e.getMessage());
        }
    }

    /*
     * =====================================================
     * UPDATE
     * =====================================================
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShipment(
            @PathVariable Long id,
            @RequestBody Shipment shipment) {

        try {

            Shipment updated = shipmentService
                    .updateShipment(
                            id,
                            shipment);

            return ResponseEntity.ok(
                    updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.BAD_REQUEST)
                    .body(
                            e.getMessage());
        }
    }

    /*
     * =====================================================
     * STATUS UPDATE
     * =====================================================
     */

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            Shipment updated = shipmentService
                    .updateShipmentStatus(
                            id,
                            status);

            return ResponseEntity.ok(
                    updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.BAD_REQUEST)
                    .body(
                            e.getMessage());
        }
    }

    /*
     * =====================================================
     * DELETE
     * =====================================================
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable Long id) {

        try {

            shipmentService
                    .deleteShipment(id);

            return ResponseEntity.ok(
                    "Shipment deleted successfully");

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(
                            HttpStatus.NOT_FOUND)
                    .body(
                            e.getMessage());
        }
    }

}