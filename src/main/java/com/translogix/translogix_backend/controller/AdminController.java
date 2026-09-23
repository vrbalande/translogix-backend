package com.translogix.translogix_backend.controller;

import com.translogix.translogix_backend.dto.AdminCreateShipmentRequest;
import com.translogix.translogix_backend.entity.Shipment;
import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.UserRepository;
import com.translogix.translogix_backend.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(
            AdminService adminService,
            UserRepository userRepository
    ) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL USERS / CUSTOMERS
    // =========================================================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getCustomers() {

        return ResponseEntity.ok(
                userRepository.findByRoleIgnoreCase("USER")
        );
    }

    // =========================================================
    // GET ALL SHIPMENTS
    // =========================================================

    @GetMapping("/shipments")
    public ResponseEntity<List<Shipment>> getAllShipments() {

        return ResponseEntity.ok(
                adminService.getAllShipments()
        );
    }

    // =========================================================
    // GET SHIPMENT BY ID
    // =========================================================

    @GetMapping("/shipments/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Long id
    ) {

        try {

            Shipment shipment =
                    adminService.getShipmentById(id);

            return ResponseEntity.ok(shipment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // CREATE SHIPMENT + ASSIGN CUSTOMER
    // =========================================================

    @PostMapping("/shipments")
    public ResponseEntity<?> createShipment(
            @RequestBody AdminCreateShipmentRequest request
    ) {

        try {

            Shipment savedShipment =
                    adminService.createShipment(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedShipment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // UPDATE SHIPMENT
    // =========================================================

    @PutMapping("/shipments/{id}")
    public ResponseEntity<?> updateShipment(
            @PathVariable Long id,
            @RequestBody Shipment shipment
    ) {

        try {

            Shipment updated =
                    adminService.updateShipment(
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

    // =========================================================
    // DELETE SHIPMENT
    // =========================================================

    @DeleteMapping("/shipments/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable Long id
    ) {

        try {

            adminService.deleteShipment(id);

            return ResponseEntity.ok(
                    "Shipment deleted successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PatchMapping("/shipments/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {

        try {

            Shipment updated =
                    adminService.updateShipmentStatus(
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

    // =========================================================
    // ASSIGN EXISTING SHIPMENT TO USER
    // =========================================================

    @PatchMapping("/shipments/assign-user")
    public ResponseEntity<?> assignShipmentToUser(
            @RequestParam String trackingNumber,
            @RequestParam String username
    ) {

        try {

            Shipment updated =
                    adminService.assignShipmentToUser(
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

    // =========================================================
    // GET SHIPMENTS BY STATUS
    // =========================================================

    @GetMapping("/shipments/status/{status}")
    public ResponseEntity<?> getShipmentsByStatus(
            @PathVariable String status
    ) {

        try {

            List<Shipment> shipments =
                    adminService.getShipmentsByStatus(status);

            return ResponseEntity.ok(shipments);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // DASHBOARD STATS
    // =========================================================

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {

        return ResponseEntity.ok(
                new DashboardStats(
                        adminService.getTotalShipments(),
                        adminService.getPendingShipments(),
                        adminService.getInTransitShipments(),
                        adminService.getOutForDeliveryShipments(),
                        adminService.getDeliveredShipments()
                )
        );
    }

    // =========================================================
    // DASHBOARD DTO
    // =========================================================

    public static class DashboardStats {

        private final long total;
        private final long pending;
        private final long inTransit;
        private final long outForDelivery;
        private final long delivered;

        public DashboardStats(
                long total,
                long pending,
                long inTransit,
                long outForDelivery,
                long delivered
        ) {

            this.total = total;
            this.pending = pending;
            this.inTransit = inTransit;
            this.outForDelivery = outForDelivery;
            this.delivered = delivered;
        }

        public long getTotal() {
            return total;
        }

        public long getPending() {
            return pending;
        }

        public long getInTransit() {
            return inTransit;
        }

        public long getOutForDelivery() {
            return outForDelivery;
        }

        public long getDelivered() {
            return delivered;
        }
    }
}