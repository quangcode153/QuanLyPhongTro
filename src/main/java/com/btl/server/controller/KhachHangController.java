package com.btl.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.btl.server.dto.KhachHangDTO;
import com.btl.server.entity.KhachHang;
import com.btl.server.service.KhachHangService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public List<KhachHang> layDanhSachKhach() {
        return khachHangService.getAllKhachHang();
    }
    
    @PostMapping
    public KhachHang themKhachMoi(@Valid @RequestBody KhachHang khachHang) {
        return khachHangService.saveKhach(khachHang);
    }

    @GetMapping("/an-toan")
    public List<KhachHangDTO> layDanhSachKhachAnToan() {
        return khachHangService.getKhachHangAnToan();
    }
}