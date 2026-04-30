package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.btl.server.entity.TaiKhoan;
import com.btl.server.entity.KhachHang;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.security.JwtService;
import com.btl.server.dto.AuthRequestDTO;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
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

    // Chống Timing Attack - Rất chuẩn!
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

        KhachHang hoSoRong = new KhachHang();
        hoSoRong.setHoTen(request.getUsername());

        hoSoRong.setTaiKhoan(taiKhoanMoi);
        taiKhoanMoi.setKhachHang(hoSoRong);

        taiKhoanRepository.save(taiKhoanMoi);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng ký tài khoản thành công!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> layThongTinCaNhan(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        TaiKhoan user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chu-tro")
    public ResponseEntity<?> layDanhSachChuTro() {
        List<Map<String, Object>> danhSachChuTro = taiKhoanRepository.findAll().stream()
                .filter(tk -> "ROLE_LANDLORD".equals(tk.getRole()))
                .map(tk -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tk.getId());
                    map.put("username", tk.getUsername());
                    
                    if (tk.getKhachHang() != null) {
                        map.put("hoTen", tk.getKhachHang().getHoTen());
                    }
                    return map;
                }).toList();

        return ResponseEntity.ok(danhSachChuTro);
    }

    @GetMapping("/chu-tro/{id}/chi-tiet")
    public ResponseEntity<?> layChiTietChuTro(@PathVariable Long id) {
        Optional<TaiKhoan> tkOpt = taiKhoanRepository.findById(id);
        
        if (tkOpt.isEmpty() || !"ROLE_LANDLORD".equals(tkOpt.get().getRole())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dữ liệu chủ trọ");
        }
        
        TaiKhoan tk = tkOpt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("id", tk.getId());
        map.put("username", tk.getUsername());
        
        if (tk.getKhachHang() != null) {
            map.put("hoTen", tk.getKhachHang().getHoTen());
            map.put("soDienThoai", tk.getKhachHang().getSoDienThoai());
            map.put("email", tk.getKhachHang().getEmail());
            
            // Logic verify CCCD
            String cccd = tk.getKhachHang().getSoCccd();
            boolean isVerified = (cccd != null && !cccd.trim().isEmpty());
            map.put("isVerified", isVerified);
        } else {
            map.put("isVerified", false);
        }
        
        return ResponseEntity.ok(map);
    }

     @GetMapping("/admin/danh-sach-tai-khoan")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<?> layDanhSachNguoiDung() {
        List<Map<String, Object>> anToanList = taiKhoanRepository.findAll().stream()
                .map(tk -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tk.getId());
                    map.put("username", tk.getUsername());
                    map.put("role", tk.getRole());
                    if(tk.getKhachHang() != null && tk.getKhachHang().getHoTen() != null) {
                         map.put("hoTen", tk.getKhachHang().getHoTen());
                    } else {
                         map.put("hoTen", "");
                    }
                    return map;
                }).toList();

        return ResponseEntity.ok(anToanList);
    }
}