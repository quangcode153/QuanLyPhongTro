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

import java.security.Principal;
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

        String requestedRole = request.getRole();

        if ("ROLE_LANDLORD".equals(requestedRole)) {
            taiKhoanMoi.setRole("ROLE_LANDLORD");
        } else {
            taiKhoanMoi.setRole("ROLE_USER");
        }

        taiKhoanRepository.save(taiKhoanMoi);

        Map<String, String> response = new HashMap<>();
        response.put("message",
                "Đăng ký thành công tài khoản: " + taiKhoanMoi.getUsername() + " với quyền " + taiKhoanMoi.getRole());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> layThongTinCaNhan(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TaiKhoan user = userOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chu-tro")
    public ResponseEntity<?> layDanhSachChuTro() {

        java.util.List<Map<String, Object>> danhSachChuTro = taiKhoanRepository.findAll().stream()
                .filter(tk -> tk.getRole() != null && tk.getRole().contains("LANDLORD"))
                .map(tk -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tk.getId());
                    map.put("username", tk.getUsername());
                    return map;
                }).toList();

        return ResponseEntity.ok(danhSachChuTro);
    }
}