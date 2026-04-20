package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public PhieuTinhTienDTO chotSoThangNay(@RequestBody ChiSoDienNuoc chiSo) {
        return chiSoService.chotSoVaTinhTien(chiSo);
    }
}