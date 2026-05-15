package com.btl.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.btl.server.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {

    private static final Logger log = LoggerFactory.getLogger(TaiKhoanController.class);
    private final String BCRYPT_DUMMY_HASH = "$2a$10$wTf2E/.n./l5.f.P./R7l.y0r.2X/n.O.m.r.y.Q.t.Q.O.m.X.Y.m.C";

        private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TaiKhoanController(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request, HttpServletRequest httpRequest) {

                String cleanUsername = request.getUsername().trim().toLowerCase();
        String clientIp = httpRequest.getRemoteAddr();

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(cleanUsername);
        
        TaiKhoan userToVerify = userOpt.orElseGet(() -> {
            TaiKhoan fake = new TaiKhoan();
            fake.setPassword(BCRYPT_DUMMY_HASH);
            return fake;
        });

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), userToVerify.getPassword());

        if (isPasswordMatch && userOpt.isPresent()) {
            TaiKhoan user = userOpt.get();

            if (user.isLocked()) {
                                log.warn("Login blocked (Account Locked). IP: {}", clientIp);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Tài khoản của bạn đã bị khóa bởi Quản trị viên!"));
            }

            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                log.error("Login failed (Invalid Role Data) for user ID: {}", user.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Dữ liệu phân quyền bị lỗi. Vui lòng liên hệ Admin!"));
            }

            String token = jwtService.generateToken(user.getUsername(), user.getRole());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("message", "Đăng nhập thành công!");

            log.info("Login success: user_id={}, ip={}", user.getId(), clientIp);
            return ResponseEntity.ok(response);
        }

                log.warn("Login failed (Wrong credentials). Hash: {}, IP: {}", cleanUsername.hashCode(), clientIp);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Sai tên đăng nhập hoặc mật khẩu!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO request, HttpServletRequest httpRequest) {
        String cleanUsername = request.getUsername().trim().toLowerCase();
        
        if (taiKhoanRepository.findByUsername(cleanUsername).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác."));
        }

        TaiKhoan taiKhoanMoi = new TaiKhoan();
        taiKhoanMoi.setUsername(cleanUsername);
        taiKhoanMoi.setPassword(passwordEncoder.encode(request.getPassword()));
        
                        String reqRole = request.getRole(); 
        if (reqRole == null || reqRole.trim().isEmpty()) {
            reqRole = "ROLE_USER";         }
        taiKhoanMoi.setRole(reqRole);

        KhachHang hoSoRong = new KhachHang();
        hoSoRong.setHoTen(cleanUsername);
        hoSoRong.setTaiKhoan(taiKhoanMoi);
        taiKhoanMoi.setKhachHang(hoSoRong);

        taiKhoanRepository.save(taiKhoanMoi);

        log.info("New user registered successfully: Hash={}, IP={}", cleanUsername.hashCode(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Đăng ký tài khoản thành công!"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> layThongTinCaNhan(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByUsername(principal.getName().toLowerCase());
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
                List<TaiKhoanRepository.ChuTroProjection> danhSachChuTro = taiKhoanRepository.findChuTroProjections();
        return ResponseEntity.ok(danhSachChuTro);
    }

    @GetMapping("/chu-tro/{id}/chi-tiet")
    public ResponseEntity<?> layChiTietChuTro(@PathVariable Long id) {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .filter(t -> "ROLE_LANDLORD".equals(t.getRole()))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy dữ liệu chủ trọ"));
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", tk.getId());
        map.put("username", tk.getUsername());
        
        if (tk.getKhachHang() != null) {
            map.put("hoTen", tk.getKhachHang().getHoTen());
            map.put("soDienThoai", tk.getKhachHang().getSoDienThoai());
            map.put("email", tk.getKhachHang().getEmail());
            map.put("tenNganHang", tk.getKhachHang().getTenNganHang());
            map.put("soTaiKhoan", tk.getKhachHang().getSoTaiKhoan());
            map.put("chuTaiKhoan", tk.getKhachHang().getChuTaiKhoan());
            
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
    public ResponseEntity<?> layDanhSachNguoiDung(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<TaiKhoan> taiKhoanPage = taiKhoanRepository.findAll(PageRequest.of(page, size));
        
        List<Map<String, Object>> content = taiKhoanPage.stream().map(tk -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tk.getId());
            map.put("username", tk.getUsername());
            map.put("role", tk.getRole());
            map.put("locked", tk.isLocked()); 
            map.put("hoTen", tk.getKhachHang() != null && tk.getKhachHang().getHoTen() != null ? tk.getKhachHang().getHoTen() : "");
            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("data", content);
        response.put("currentPage", taiKhoanPage.getNumber());
        response.put("totalItems", taiKhoanPage.getTotalElements());
        response.put("totalPages", taiKhoanPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdmin() {
        return taiKhoanRepository.findFirstByRole("ROLE_ADMIN")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
     
    @PutMapping("/admin/{id}/toggle-lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleLockUser(@PathVariable Long id, HttpServletRequest request) {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
         
        tk.setLocked(!tk.isLocked());
        taiKhoanRepository.save(tk);
         
        log.info("Admin toggled lock status for user ID: {}. New Status: {}, IP: {}", id, tk.isLocked(), request.getRemoteAddr());

        Map<String, Object> response = new HashMap<>();
        response.put("id", tk.getId());
        response.put("locked", tk.isLocked());
        response.put("message", tk.isLocked() ? "Đã khóa tài khoản!" : "Đã mở khóa tài khoản!");

        return ResponseEntity.ok(response);
    }
}