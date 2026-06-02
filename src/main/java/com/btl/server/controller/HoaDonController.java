package com.btl.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;

import com.btl.server.service.HoaDonService;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.entity.HoaDon;
import com.btl.server.entity.TaiKhoan;

/**
 * REST Controller điều phối các API quản lý Hóa Đơn (`/api/hoa-don`).
 * Hỗ trợ: Xem danh sách hóa đơn theo chủ trọ hoặc khách thuê cá nhân,
 * thực hiện thanh toán, và xóa hóa đơn nhập sai.
 */
@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Khởi tạo HoaDonController với HoaDonService và TaiKhoanRepository.
     */
    public HoaDonController(HoaDonService hoaDonService, TaiKhoanRepository taiKhoanRepository) {
        this.hoaDonService = hoaDonService;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    /**
     * API Chủ trọ lấy toàn bộ danh sách hóa đơn của tất cả các phòng thuộc quyền quản lý của mình.
     * @param chuTroId ID của chủ trọ
     */
    @GetMapping("/chu-tro/{chuTroId}")
    public ResponseEntity<List<HoaDon>> layHoaDonTheoChuTro(@PathVariable Long chuTroId) {
        return ResponseEntity.ok(hoaDonService.layHoaDonCuaChuTro(chuTroId));
    }

    /**
     * API Khách thuê lấy toàn bộ danh sách hóa đơn tiền phòng và dịch vụ của chính mình.
     */
    @GetMapping("/me")
    public ResponseEntity<List<HoaDon>> layHoaDonCuaToi(Principal principal) {
        TaiKhoan tk = taiKhoanRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user!"));
            
        return ResponseEntity.ok(hoaDonService.layDanhSachHoaDonCuaKhach(tk.getId()));
    }

    /**
     * API Khách thuê hoặc Chủ trọ xác nhận thực hiện thanh toán hóa đơn dịch vụ hàng tháng.
     * @param id ID của hóa đơn cần đóng tiền
     */
    @PostMapping("/{id}/thanh-toan")
    public ResponseEntity<Void> thanhToan(@PathVariable Long id, Principal principal) {
        TaiKhoan tk = taiKhoanRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user!"));
            
        hoaDonService.thanhToanHoaDon(id, tk.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * API Chủ trọ xóa bỏ hóa đơn bị nhập nhầm lẫn chỉ số cũ/mới của tháng.
     * Logic nghiệp vụ xóa an toàn được ủy thác toàn bộ cho `HoaDonService.xoaHoaDonBiSai(id)`.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaHoaDon(@PathVariable Long id) {
        hoaDonService.xoaHoaDonBiSai(id);
        return ResponseEntity.noContent().build();
    }
}