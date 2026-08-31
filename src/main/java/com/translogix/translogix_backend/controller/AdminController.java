
package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.repository.ShipmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

        private final ShipmentRepository shipmentRepository;

        public AdminController(ShipmentRepository shipmentRepository) {
                this.shipmentRepository = shipmentRepository;
        }

        // ==========================================
        // GET ALL SHIPMENTS
        // ==========================================

        @GetMapping("/shipments")
        public ResponseEntity<List<Shipment>> getAllShipments(
                        Authentication authentication) {

                return ResponseEntity.ok(
                                shipmentRepository.findAll());
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
        // ADD SHIPMENT
        // ==========================================

        @PostMapping("/shipments")
        public ResponseEntity<?> createShipment(
                        @RequestBody Shipment shipment) {

                Shipment savedShipment = shipmentRepository.save(shipment);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(savedShipment);
        }

        // ==========================================
        // UPDATE SHIPMENT
        // ==========================================

        @PutMapping("/shipments/{id}")
        public ResponseEntity<?> updateShipment(
                        @PathVariable Long id,
                        @RequestBody Shipment updatedShipment) {

                Optional<Shipment> existing = shipmentRepository.findById(id);

                if (existing.isEmpty()) {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("Shipment not found.");
                }

                Shipment shipment = existing.get();

                shipment.setTrackingNumber(
                                updatedShipment.getTrackingNumber());

                shipment.setSenderName(
                                updatedShipment.getSenderName());

                shipment.setReceiverName(
                                updatedShipment.getReceiverName());

                shipment.setOrigin(
                                updatedShipment.getOrigin());

                shipment.setDestination(
                                updatedShipment.getDestination());

                shipment.setShipmentType(
                                updatedShipment.getShipmentType());

                shipment.setStatus(
                                updatedShipment.getStatus());

                shipment.setWeight(
                                updatedShipment.getWeight());

                Shipment savedShipment = shipmentRepository.save(shipment);

                return ResponseEntity.ok(savedShipment);
        }

        // ==========================================
        // DELETE SHIPMENT
        // ==========================================

        @DeleteMapping("/shipments/{id}")
        public ResponseEntity<?> deleteShipment(
                        @PathVariable Long id) {

                if (!shipmentRepository.existsById(id)) {

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("Shipment not found.");
                }

                shipmentRepository.deleteById(id);

                return ResponseEntity.ok(
                                "Shipment deleted successfully.");
        }

        // ==========================================
        // UPDATE SHIPMENT STATUS
        // ==========================================

        @PatchMapping("/shipments/{id}/status")
        public ResponseEntity<?> updateStatus(
                        @PathVariable Long id,
                        @RequestParam String status) {

                Optional<Shipment> existing = shipmentRepository.findById(id);

                if (existing.isEmpty()) {

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("Shipment not found.");
                }

                Shipment shipment = existing.get();

                shipment.setStatus(status);

                Shipment savedShipment = shipmentRepository.save(shipment);

                return ResponseEntity.ok(savedShipment);
        }
}
