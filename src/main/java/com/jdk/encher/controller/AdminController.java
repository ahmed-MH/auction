package com.jdk.encher.controller;

import com.jdk.encher.dto.StatsDTO;
import com.jdk.encher.dto.UtilisateurDTO;
import com.jdk.encher.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Administration", description = "Gestion administrative")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Obtenir les statistiques du site")
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatsDTO> getStats() {
        StatsDTO stats = adminService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtenir la liste de tous les utilisateurs")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        List<UtilisateurDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Bloquer ou débloquer un utilisateur")
    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        Boolean etatCompte = body.get("etatCompte");
        if (etatCompte == null) {
            return ResponseEntity.badRequest().build();
        }

        adminService.updateUserStatus(id, etatCompte);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Créer un nouvel administrateur")
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createAdmin(@RequestBody com.jdk.encher.dto.SignUpRequest request) {
        adminService.createAdmin(request);
        return ResponseEntity.ok().build();
    }
}