package com.btl.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.dto.KhachHangDTO;
import com.btl.server.dto.CapNhatHoSoDTO;
import com.btl.server.dto.HoSoResponseDTO;
import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.KhachHangRepository;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.KhachHangService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HopDongRepository hopDongRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KhachHang>> layDanhSachKhach() {
        return ResponseEntity.ok(khachHangService.getAllKhachHang());
    }

    @GetMapping("/an-toan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KhachHangDTO>> layDanhSachKhachAnToan() {
        return ResponseEntity.ok(khachHangService.getKhachHangAnToan());
    }

    @GetMapping("/ho-so/me")
    public ResponseEntity<?> layHoSoCaNhan(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));

        Optional<KhachHang> hoSoOpt = khachHangRepository.findByTaiKhoan(user);
        if (hoSoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Chưa có hồ sơ"));
        }

        return ResponseEntity.ok(new HoSoResponseDTO(hoSoOpt.get()));
    }

    @GetMapping("/chi-tiet/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> layHoSoTheoId(@PathVariable Integer id, Principal principal) {
        TaiKhoan currentUser = taiKhoanRepository.findByUsername(principal.getName()).orElseThrow();

        if (!currentUser.getRole().contains("ADMIN")) {
            boolean isMyTenant = hopDongRepository.existsByKhachHang_IdAndPhongTro_ChuTroId(id, currentUser.getId());
            if (!isMyTenant) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Cảnh báo bảo mật: Bạn không có quyền xem hồ sơ của khách hàng này!"));
            }
        }

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy tài khoản!"));
        }

        Optional<KhachHang> hoSoOpt = khachHangRepository.findByTaiKhoan(userOpt.get());
        if (hoSoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Khách hàng này chưa cập nhật hồ sơ!"));
        }

        return ResponseEntity.ok(new HoSoResponseDTO(hoSoOpt.get()));
    }

    @PutMapping("/ho-so/me")
    @Transactional
    public ResponseEntity<?> capNhatHoSoCaNhan(Principal principal, @Valid @RequestBody CapNhatHoSoDTO dto) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập lại!"));
        }

        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));

        Optional<KhachHang> khachWithCccd = khachHangRepository.findBySoCccd(dto.getSoCccd());
        if (khachWithCccd.isPresent()) {
            KhachHang kh = khachWithCccd.get();
            if (kh.getTaiKhoan() != null && !kh.getTaiKhoan().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Số CCCD đã được đăng ký cho một tài khoản khác!"));
            }
        }

        KhachHang hoSo = khachHangRepository.findByTaiKhoan(user)
                .orElseGet(() -> {
                    KhachHang newHoSo = new KhachHang();
                    newHoSo.setTaiKhoan(user);
                    return newHoSo;
                });

        hoSo.setHoTen(dto.getHoTen());
        hoSo.setNgaySinh(dto.getNgaySinh());
        hoSo.setGioiTinh(dto.getGioiTinh());
        hoSo.setSoCccd(dto.getSoCccd());
        hoSo.setSoDienThoai(dto.getSoDienThoai());
        hoSo.setEmail(dto.getEmail());
        hoSo.setDiaChiThuongTru(dto.getDiaChiThuongTru());

        KhachHang savedHoSo = khachHangRepository.save(hoSo);
        return ResponseEntity.ok(new HoSoResponseDTO(savedHoSo));
    }
}