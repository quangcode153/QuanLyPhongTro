package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.security.JwtService;
import com.btl.server.dto.AuthRequestDTO;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final String BCRYPT_DUMMY_HASH = "$2a$10$wTf2E/.n./l5.f.P./R7l.y0r.2X/n.O.m.r.y.Q.t.Q.O.m.X.Y.m.C";

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(request.getUsername());

        String hashToCheck = userOpt.isPresent() ? userOpt.get().getPassword() : BCRYPT_DUMMY_HASH;
        
        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), hashToCheck);

        if (userOpt.isPresent() && isPasswordMatch) {
            TaiKhoan user = userOpt.get();
            String token = jwtService.generateToken(user.getUsername(), user.getRole());
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("message", "Đăng nhập thành công!");
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("message", "Sai tên đăng nhập hoặc mật khẩu!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO request) {
        
        if (taiKhoanRepository.findByUsername(request.getUsername()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        TaiKhoan taiKhoanMoi = new TaiKhoan();
        taiKhoanMoi.setUsername(request.getUsername());
        taiKhoanMoi.setPassword(passwordEncoder.encode(request.getPassword()));
        taiKhoanMoi.setRole("ROLE_USER");
        
        taiKhoanRepository.save(taiKhoanMoi);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng ký thành công tài khoản: " + taiKhoanMoi.getUsername());
        return ResponseEntity.ok(response);
    }
}