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
import com.btl.server.repository.PhongTroRepository;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.KhachHangRepository;
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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import com.btl.server.service.MailService;

@RestController
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {

    private static final Logger log = LoggerFactory.getLogger(TaiKhoanController.class);
    private final String BCRYPT_DUMMY_HASH = "$2a$10$wTf2E/.n./l5.f.P./R7l.y0r.2X/n.O.m.r.y.Q.t.Q.O.m.X.Y.m.C";

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PhongTroRepository phongTroRepository;
    private final HopDongRepository hopDongRepository;
    private final KhachHangRepository khachHangRepository;
    private final MailService mailService;

    private static final ConcurrentHashMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    private static class OtpData {
        String otp;
        long expiryTime;
        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    public TaiKhoanController(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder, JwtService jwtService, 
                            PhongTroRepository phongTroRepository, HopDongRepository hopDongRepository,
                            KhachHangRepository khachHangRepository, MailService mailService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.phongTroRepository = phongTroRepository;
        this.hopDongRepository = hopDongRepository;
        this.khachHangRepository = khachHangRepository;
        this.mailService = mailService;
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

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email đăng ký không được để trống!"));
        }

        String cleanEmail = request.getEmail().trim().toLowerCase();
        if (khachHangRepository.findByEmail(cleanEmail).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email này đã được sử dụng! Vui lòng chọn email khác."));
        }

        String hoTen = request.getHoTen();
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Họ tên không được để trống!"));
        }
        hoTen = hoTen.trim();
        if (hoTen.length() < 2 || hoTen.length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Họ tên phải từ 2 đến 50 ký tự!"));
        }
        if (!hoTen.matches("^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂÂÊÔƠưăâêôơ\\s]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Họ tên không được chứa số hoặc ký tự đặc biệt!"));
        }

        String registerOtpKey = "register_otp_" + cleanEmail;
        OtpData registerOtpData = otpStorage.get(registerOtpKey);
        if (registerOtpData == null || registerOtpData.expiryTime < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mã xác thực OTP đã hết hạn hoặc không hợp lệ. Vui lòng lấy mã mới!"));
        }
        if (!registerOtpData.otp.equals(request.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mã xác thực OTP không chính xác!"));
        }
        otpStorage.remove(registerOtpKey);

        TaiKhoan taiKhoanMoi = new TaiKhoan();
        taiKhoanMoi.setUsername(cleanUsername);
        taiKhoanMoi.setPassword(passwordEncoder.encode(request.getPassword()));
        
        String reqRole = request.getRole(); 
        if (reqRole == null || reqRole.trim().isEmpty()) {
            reqRole = "ROLE_USER";
        }
        taiKhoanMoi.setRole(reqRole);

        KhachHang hoSoRong = new KhachHang();
        hoSoRong.setHoTen(hoTen);
        hoSoRong.setEmail(cleanEmail);
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

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        TaiKhoan tk = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
        
        if ("ROLE_ADMIN".equals(tk.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Không thể xóa tài khoản Admin!"));
        }

        // Kiểm tra dữ liệu liên quan để tránh lỗi FK
        if ("ROLE_LANDLORD".equals(tk.getRole())) {
            if (!phongTroRepository.findByChuTroId(id).isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Không thể xóa: Chủ trọ này đang có khu trọ và phòng đang hoạt động!"));
            }
        } else if ("ROLE_USER".equals(tk.getRole())) {
            if (!hopDongRepository.findByKhachHang_Id(id).isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Không thể xóa: Khách thuê này đang có hợp đồng trên hệ thống!"));
            }
        }

        taiKhoanRepository.delete(tk);
        log.info("Admin deleted user ID: {}. IP: {}", id, request.getRemoteAddr());

        return ResponseEntity.ok(Map.of("message", "Đã xóa tài khoản thành công!"));
    }

    @PostMapping("/send-register-otp")
    public ResponseEntity<?> sendRegisterOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email không được để trống!"));
        }
        email = email.trim().toLowerCase();
        if (!email.endsWith("@gmail.com")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email phải là địa chỉ Gmail hợp lệ (@gmail.com)"));
        }
        if (khachHangRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email này đã được sử dụng! Vui lòng chọn email khác."));
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put("register_otp_" + email, new OtpData(otp, System.currentTimeMillis() + 5 * 60 * 1000));

        try {
            mailService.sendOtpMessage(email, otp, true);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể gửi email xác thực. Vui lòng kiểm tra lại cấu hình SMTP!"));
        }

        return ResponseEntity.ok(Map.of("message", "Mã xác thực OTP đã được gửi đến Gmail của bạn. Vui lòng kiểm tra hòm thư!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email không được để trống!"));
        }
        email = email.trim().toLowerCase();
        Optional<KhachHang> khachOpt = khachHangRepository.findByEmail(email);
        if (khachOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email này không tồn tại trên hệ thống!"));
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put("forgot_otp_" + email, new OtpData(otp, System.currentTimeMillis() + 5 * 60 * 1000));

        try {
            mailService.sendOtpMessage(email, otp, false);
        } catch (Exception e) {
            log.error("Failed to send forgot password OTP to {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không thể gửi email xác thực. Vui lòng kiểm tra lại cấu hình SMTP!"));
        }

        return ResponseEntity.ok(Map.of("message", "Mã xác thực OTP khôi phục mật khẩu đã được gửi đến Gmail của bạn."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");

        if (email == null || email.trim().isEmpty() ||
            otp == null || otp.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Vui lòng nhập đầy đủ thông tin Email, mã OTP và mật khẩu mới!"));
        }

        email = email.trim().toLowerCase();
        otp = otp.trim();
        newPassword = newPassword.trim();

        if (newPassword.length() < 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mật khẩu phải chứa ít nhất 3 ký tự!"));
        }

        String forgotOtpKey = "forgot_otp_" + email;
        OtpData forgotOtpData = otpStorage.get(forgotOtpKey);
        if (forgotOtpData == null || forgotOtpData.expiryTime < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mã xác thực OTP đã hết hạn hoặc không hợp lệ. Vui lòng lấy mã mới!"));
        }
        if (!forgotOtpData.otp.equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mã xác thực OTP không chính xác!"));
        }

        KhachHang kh = khachHangRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin khách hàng!"));
        TaiKhoan tk = kh.getTaiKhoan();
        if (tk == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy tài khoản tương ứng với email này!"));
        }

        tk.setPassword(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(tk);

        otpStorage.remove(forgotOtpKey);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới."));
    }
}