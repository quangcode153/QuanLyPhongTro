package com.btl.server.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.service.ChiSoDienNuocService;
import com.btl.server.dto.PhieuTinhTienDTO;

@RestController
@RequestMapping("/api/dien-nuoc")
public class ChiSoDienNuocController {

    @Autowired
    private ChiSoDienNuocService chiSoService;

    @PostMapping("/chot-so")
    public ResponseEntity<?> chotSoThangNay(@RequestBody ChiSoDienNuoc chiSo) {
        try {
            PhieuTinhTienDTO ketQua = chiSoService.chotSoVaTinhTien(chiSo);
            return ResponseEntity.ok(ketQua);
            
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("trangThai", "LỖI TRÙNG LẶP");
            errorResponse.put("thongBao", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}