package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.request.AuthRq;
import com.fitnesscenter.app.dto.response.AdminRs;
import com.fitnesscenter.app.entity.Administrator;
import com.fitnesscenter.app.repository.AdministratorRepository;
import com.fitnesscenter.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AdministratorRepository administratorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AdminRs> authenticate(@RequestBody AuthRq request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin() {
        Administrator admin = new Administrator();
        admin.setLogin("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setFio("Администратор");
        admin.setEmail("admin@fitness.com");
        admin.setRole("ADMIN");
        admin.setDeleted(false);
        administratorRepository.save(admin);
        return ResponseEntity.ok("Admin created successfully with password 'admin'");
    }

}
