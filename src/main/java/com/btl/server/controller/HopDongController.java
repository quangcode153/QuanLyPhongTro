package com.btl.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.btl.server.entity.HopDong;
import com.btl.server.service.HopDongService;

@RestController
@RequestMapping("/api/hop-dong")
public class HopDongController {

    @Autowired
    private HopDongService hopDongService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<List<HopDong>> xemDanhSachHopDong() {
        return ResponseEntity.ok(hopDongService.layTatCaHopDong());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')") 
    public ResponseEntity<HopDong> kyHopDongMoi(@RequestBody HopDong hopDong) {
        hopDong.setTrangThai("CHỜ_DUYỆT");
        return ResponseEntity.ok(hopDongService.taoHopDong(hopDong));
    }

    @GetMapping("/chu-tro/{chuTroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')") 
    public ResponseEntity<List<HopDong>> layHopDongCuaChuTro(@PathVariable Integer chuTroId) {
        return ResponseEntity.ok(hopDongService.layHopDongTheoChuTro(chuTroId));
    }

   
    @GetMapping("/khach/{khachId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HopDong>> layHopDongCuaKhach(@PathVariable Integer khachId) {
        return ResponseEntity.ok(hopDongService.layHopDongTheoKhach(khachId));
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')") 
    public ResponseEntity<?> capNhatTrangThai(@PathVariable Integer id, @RequestParam String trangThai) {
        HopDong updated = hopDongService.capNhatTrangThaiHopDong(id, trangThai);
        return ResponseEntity.ok(updated);
    }
}