package com.btl.server.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.btl.server.dto.CapNhatHoSoDTO;
import com.btl.server.dto.HoSoResponseDTO;
import com.btl.server.dto.KhachHangDTO;
import com.btl.server.service.KhachHangService;

import jakarta.validation.Valid;

/**
 * REST Controller điều phối các API quản lý hồ sơ thông tin cá nhân của người dùng (`/api/khach-hang`).
 * Cung cấp: Xem danh sách thành viên (chỉ Admin), cập nhật hồ sơ cá nhân hiện tại,
 * và tra cứu thông tin chi tiết một khách hàng theo ID kèm phân quyền bảo mật.
 */
@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    /**
     * API Admin lấy danh sách toàn bộ khách hàng trong hệ thống (đã được lọc các trường dữ liệu nhạy cảm).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KhachHangDTO>> layDanhSachKhachHang() {
        return ResponseEntity.ok(khachHangService.getKhachHangAnToan());
    }

    /**
     * API người dùng đăng nhập tự tra cứu hồ sơ cá nhân của chính mình.
     */
    @GetMapping("/ho-so/me")
    public ResponseEntity<HoSoResponseDTO> layHoSoCaNhan(Principal principal) {
        return ResponseEntity.ok(khachHangService.layHoSoCaNhan(principal.getName()));
    }

    /**
     * API người dùng đăng nhập chủ động cập nhật chỉnh sửa hồ sơ cá nhân (CCCD, Họ tên, SĐT, tài khoản ngân hàng).
     */
    @PutMapping("/ho-so/me")
    public ResponseEntity<HoSoResponseDTO> capNhatHoSoCaNhan(
            Principal principal,
            @Valid @RequestBody CapNhatHoSoDTO dto) {
        return ResponseEntity.ok(khachHangService.capNhatHoSoCaNhan(principal.getName(), dto));
    }

    /**
     * API lấy thông tin chi tiết hồ sơ của một tài khoản cụ thể theo ID.
     * Chứa kiểm duyệt bảo mật:
     * - Admin xem được tất cả.
     * - Chủ trọ xem được nếu khách hàng này đang thuê phòng thuộc quyền quản lý của chủ trọ đó.
     * - Khách thuê chỉ xem được của chính bản thân.
     */
    @GetMapping("/chi-tiet/{id}")
    public ResponseEntity<HoSoResponseDTO> layChiTietKhachHang(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(khachHangService.layHoSoTheoId(id, principal.getName()));
    }
}