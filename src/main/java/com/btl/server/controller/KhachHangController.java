package com.btl.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.btl.server.dto.KhachHangDTO;
import com.btl.server.entity.KhachHang;
import com.btl.server.service.KhachHangService;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public ResponseEntity<List<KhachHang>> layDanhSachKhach() {
        return ResponseEntity.ok(khachHangService.getAllKhachHang());
    }
    
    @GetMapping("/an-toan")
    public ResponseEntity<List<KhachHangDTO>> layDanhSachKhachAnToan() {
        return ResponseEntity.ok(khachHangService.getKhachHangAnToan());
    }
}