package com.btl.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.HopDongService;

@RestController
@RequestMapping("/api/hop-dong")
public class HopDongController {

    @Autowired
    private HopDongService hopDongService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HopDong>> xemDanhSachHopDong() {
        return ResponseEntity.ok(hopDongService.layTatCaHopDong());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> kyHopDongMoi(@RequestBody HopDong request, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Xác thực thất bại!"));

        HopDong hopDong = new HopDong();
        hopDong.setKhachHang(user);
        hopDong.setPhongTro(request.getPhongTro());
        hopDong.setNgayBatDau(request.getNgayBatDau());
        hopDong.setTienCoc(request.getTienCoc() != null ? request.getTienCoc() : 0.0);
        hopDong.setTrangThai("CHỜ_DUYỆT");

        hopDongService.taoHopDong(hopDong);

        return ResponseEntity.ok(Map.of("message", "Đã tạo yêu cầu thuê phòng thành công!"));
    }

    @GetMapping("/chu-tro/{chuTroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<List<HopDong>> layHopDongCuaChuTro(@PathVariable Integer chuTroId, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName()).orElseThrow();

        if (!user.getRole().contains("ADMIN") && !user.getId().equals(chuTroId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không được phép xem dữ liệu của chủ trọ khác!");
        }

        return ResponseEntity.ok(hopDongService.layHopDongTheoChuTro(chuTroId));
    }

    @GetMapping("/khach/{khachId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HopDong>> layHopDongCuaKhach(@PathVariable Integer khachId, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName()).orElseThrow();

        if (!user.getId().equals(khachId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không được phép xem dữ liệu của người khác!");
        }

        return ResponseEntity.ok(hopDongService.layHopDongTheoKhach(khachId));
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> capNhatTrangThai(@PathVariable Integer id, @RequestParam String trangThai, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName()).orElseThrow();

        hopDongService.capNhatTrangThaiHopDong(id, trangThai, user);

        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
    }
}