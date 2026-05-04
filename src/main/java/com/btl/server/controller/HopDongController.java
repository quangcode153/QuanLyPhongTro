package com.btl.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.btl.server.dto.HopDongRequestDTO;
import com.btl.server.entity.HopDong;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.HopDongService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hop-dong")
public class HopDongController {

    private static final Logger log = LoggerFactory.getLogger(HopDongController.class);

    private final HopDongService hopDongService;
    private final TaiKhoanRepository taiKhoanRepository;

    public HopDongController(HopDongService hopDongService, TaiKhoanRepository taiKhoanRepository) {
        this.hopDongService = hopDongService;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HopDong>> xemDanhSachHopDong() {
        return ResponseEntity.ok(hopDongService.layTatCaHopDong());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    // 🔥 FIX 5: Bắt client gửi DTO thay vì Entity
    public ResponseEntity<?> kyHopDongMoi(@Valid @RequestBody HopDongRequestDTO request, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Xác thực thất bại, user không tồn tại!"));

        hopDongService.taoHopDong(request, user);

        return ResponseEntity.ok(Map.of("message", "Đã tạo yêu cầu thuê phòng thành công!"));
    }

    @GetMapping("/chu-tro/{chuTroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<List<HopDong>> layHopDongCuaChuTro(@PathVariable Long chuTroId, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        // 🔥 FIX 7: Kiểm tra Role chặt chẽ, phải là Admin hoặc đúng Chủ Trọ đó
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            if (!"ROLE_LANDLORD".equals(user.getRole()) || !user.getId().equals(chuTroId)) {
                throw new ForbiddenException("Không được phép xem dữ liệu của chủ trọ khác!");
            }
        }

        return ResponseEntity.ok(hopDongService.layHopDongTheoChuTro(chuTroId));
    }

    @GetMapping("/khach/{khachId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HopDong>> layHopDongCuaKhach(
            @PathVariable Long khachId,
            @RequestParam(required = false, defaultValue = "ALL") String trangThai,
            Principal principal) {

        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        if (!user.getId().equals(khachId)) {
            throw new ForbiddenException("Không được phép xem dữ liệu của người khác!");
        }

        if ("ALL".equalsIgnoreCase(trangThai)) {
            return ResponseEntity.ok(hopDongService.layHopDongTheoKhach(khachId));
        }

        try {
            TrangThaiHopDong enumTrangThai = TrangThaiHopDong.valueOf(trangThai.toUpperCase());
            return ResponseEntity.ok(hopDongService.layHopDongTheoKhachVaTrangThai(khachId, enumTrangThai));
        } catch (IllegalArgumentException e) {
            throw new com.btl.server.exception.BadRequestException("Trạng thái hợp đồng không hợp lệ!");
        }
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> capNhatTrangThai(@PathVariable Long id, @RequestParam String trangThai, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        try {
            TrangThaiHopDong trangThaiMoi = TrangThaiHopDong.valueOf(trangThai.toUpperCase());
            hopDongService.capNhatTrangThaiHopDong(id, trangThaiMoi, user);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
        } catch (IllegalArgumentException e) {
            throw new com.btl.server.exception.BadRequestException("Trạng thái chuyển đổi không hợp lệ!");
        }
    }
}