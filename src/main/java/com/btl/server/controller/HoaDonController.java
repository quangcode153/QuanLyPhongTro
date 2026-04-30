package com.btl.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.service.HoaDonService;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    public HoaDonController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<Void> xoaHoaDon(@PathVariable Integer id) {
        hoaDonService.xoaHoaDonBiSai(id);
        return ResponseEntity.noContent().build();
    }
}