package com.btl.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.btl.server.entity.HopDong;
import com.btl.server.service.HopDongService;

@RestController
@RequestMapping("/api/hop-dong")
public class HopDongController {

    @Autowired
    private HopDongService hopDongService;

    @GetMapping
    public List<HopDong> xemDanhSachHopDong() {
        return hopDongService.layTatCaHopDong();
    }

    @PostMapping
    public HopDong kyHopDongMoi(@Valid @RequestBody HopDong hopDong) {
        return hopDongService.taoHopDong(hopDong);
    }
}