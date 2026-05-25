package com.fitnesscenter.app.service;


import com.fitnesscenter.app.dto.request.AuthRq;
import com.fitnesscenter.app.dto.response.AdminRs;
import com.fitnesscenter.app.entity.Administrator;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.AdministratorRepository;
import com.fitnesscenter.app.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AdministratorRepository administratorRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AdminRs authenticate(AuthRq request) {
        Administrator admin = administratorRepository.findByLoginAndDeletedFalse(request.getLogin())
                .orElseThrow(() -> new EntityNotFoundException("Administrator", request.getLogin()));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenUtil.generateToken(admin.getLogin(), admin.getId());

        return AdminRs.builder()
                .id(admin.getId())
                .login(admin.getLogin())
                .fio(admin.getFio())
                .email(admin.getEmail())
                .build();
    }

    public void logout(String token) {
        // логика выхода
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
