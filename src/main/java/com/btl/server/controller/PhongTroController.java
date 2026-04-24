package com.btl.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.entity.HopDong;
import com.btl.server.service.PhongTroService;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.repository.HopDongRepository;

@RestController
@RequestMapping("/api/phong-tro")
public class PhongTroController {

    @Autowired
    private PhongTroService phongTroService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private HopDongRepository hopDongRepository;

    @GetMapping
    public ResponseEntity<List<PhongTro>> layDanhSachPhong(Principal principal) {
        if (principal == null) return ResponseEntity.ok(phongTroService.getAllPhongs());

        TaiKhoan taiKhoan = taiKhoanRepository.findByUsername(principal.getName()).orElse(null);
        if (taiKhoan != null && taiKhoan.getRole().contains("LANDLORD")) {
            return ResponseEntity.ok(phongTroService.getPhongByChuTroId(taiKhoan.getId()));
        }
        return ResponseEntity.ok(phongTroService.getAllPhongs());
    }

    @PostMapping
    public ResponseEntity<PhongTro> themPhongMoi(@Valid @RequestBody PhongTro phongTro, Principal principal) {
        TaiKhoan chuTro = taiKhoanRepository.findByUsername(principal.getName()).orElse(null);
        if (chuTro != null) phongTro.setChuTroId(chuTro.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(phongTroService.savePhong(phongTro));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> capNhatPhong(@PathVariable Integer id, @Valid @RequestBody PhongTro phongTroMoi) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        if (existingPhong == null) return ResponseEntity.notFound().build();

        phongTroMoi.setId(id);
        phongTroMoi.setChuTroId(existingPhong.getChuTroId());
        return ResponseEntity.ok(phongTroService.savePhong(phongTroMoi));
    }

    @PutMapping("/{id}/trang-thai")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> capNhatTrangThaiPhong(@PathVariable Integer id, @RequestParam String trangThai) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        if (existingPhong == null) return ResponseEntity.notFound().build();

        existingPhong.setTrangThai(trangThai);
        phongTroService.savePhong(existingPhong);

        if ("Trống".equals(trangThai)) {
            List<HopDong> hopDongs = hopDongRepository.findByPhongTro_Id(id);
            for (HopDong hd : hopDongs) {
                if ("ĐÃ_DUYỆT".equals(hd.getTrangThai())) {
                    hd.setTrangThai("ĐÃ_KẾT_THÚC");
                    hopDongRepository.save(hd);
                }
            }
        }
        return ResponseEntity.ok(existingPhong);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> xoaPhong(@PathVariable Integer id) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        if (existingPhong == null) return ResponseEntity.notFound().build();

        List<HopDong> hopDongs = hopDongRepository.findByPhongTro_Id(id);
        if (!hopDongs.isEmpty()) {
            hopDongRepository.deleteAll(hopDongs);
        }

        phongTroService.deletePhong(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa thành công phòng và các hợp đồng liên quan!"));
    }

    @GetMapping("/tim-kiem")
    public ResponseEntity<List<PhongTro>> timPhong(@RequestParam String trangThai) {
        return ResponseEntity.ok(phongTroService.timPhongTheoTrangThai(trangThai));
    }

    @GetMapping("/loc-phong")
    public ResponseEntity<List<PhongTro>> locPhongTheoGia(
            @RequestParam String trangThai, @RequestParam Double giaToiDa) {
        return ResponseEntity.ok(phongTroService.locPhongTheoGia(trangThai, giaToiDa));
    }

    @GetMapping("/chu-tro/{chuTroId}")
    public ResponseEntity<List<PhongTro>> layPhongTheoChuTro(@PathVariable Integer chuTroId) {
        return ResponseEntity.ok(phongTroService.getPhongByChuTroId(chuTroId));
    }
}