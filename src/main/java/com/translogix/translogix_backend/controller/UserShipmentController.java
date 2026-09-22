package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.service.ShipmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserShipmentController {

    private final ShipmentService shipmentService;

    public UserShipmentController(
            ShipmentService shipmentService) {

        this.shipmentService = shipmentService;
    }

    // =====================================================
    // GET LOGGED-IN USER SHIPMENTS
    // =====================================================

    @GetMapping("/shipments")
    public ResponseEntity<List<Shipment>> getMyShipments(
            Authentication authentication) {

        String username =
                authentication.getName();

        return ResponseEntity.ok(
                shipmentService
                        .getShipmentsByUsername(username));
    }

    // =====================================================
    // GET LOGGED-IN USER SHIPMENT BY TRACKING NUMBER
    // =====================================================

    @GetMapping("/shipments/tracking/{trackingNumber}")
    public ResponseEntity<Shipment> getMyShipmentByTrackingNumber(
            @PathVariable String trackingNumber,
            Authentication authentication) {

        String username =
                authentication.getName();

        Shipment shipment =
                shipmentService
                        .getShipmentByTrackingNumberForUser(
                                trackingNumber,
                                username);

        if (shipment == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                shipment);
    }
}