package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.btl.server.service.HoaDonService;

@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;

    
    @DeleteMapping("/xoa/{id}")
    public String xoaHoaDon(@PathVariable Integer id) {
        hoaDonService.xoaHoaDonBịSai(id);
        return "Đã xóa hóa đơn thành công! Lớp khiên đã mở, bạn có thể chốt lại số điện nước.";
    }
}