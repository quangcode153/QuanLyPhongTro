package com.btl.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.btl.server.entity.PhongTro;
import com.btl.server.service.PhongTroService;

@RestController
@RequestMapping("/api/phong-tro")
public class PhongTroController {

    @Autowired
    private PhongTroService phongTroService;

    @GetMapping
    public List<PhongTro> layDanhSachPhong() {
        return phongTroService.getAllPhongs();
    }

    @PostMapping
    public PhongTro themPhongMoi(@Valid @RequestBody PhongTro phongTro) {
        return phongTroService.savePhong(phongTro);
    }

    @PutMapping("/{id}")
    public PhongTro capNhatPhong(@PathVariable Integer id, @Valid @RequestBody PhongTro phongTroMoi) {
        phongTroMoi.setId(id);
        return phongTroService.savePhong(phongTroMoi);
    }

    @DeleteMapping("/{id}")
    public String xoaPhong(@PathVariable Integer id) {
        phongTroService.deletePhong(id);
        return "Đã xóa thành công phòng có ID: " + id;
    }

    @GetMapping("/tim-kiem")
    public List<PhongTro> timPhong(@RequestParam String trangThai) {
        return phongTroService.timPhongTheoTrangThai(trangThai);
    }

    @GetMapping("/loc-phong")
    public List<PhongTro> locPhongTheoGia(
            @RequestParam String trangThai,
            @RequestParam Double giaToiDa) {
        return phongTroService.locPhongTheoGia(trangThai, giaToiDa);
    }
}