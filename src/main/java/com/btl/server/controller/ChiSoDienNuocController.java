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

// REST Controller điều phối các API ghi nhận chỉ số Điện và Nước hàng tháng (`/api/dien-nuoc`). Hỗ trợ chốt số tháng mới, cập nhật chỉ số khi có sai sót và tra cứu chỉ số chu kỳ.
@RestController
@RequestMapping("/api/dien-nuoc")
public class ChiSoDienNuocController {
    @Autowired
    private ChiSoDienNuocService chiSoService;

    // API Chủ trọ thực hiện chốt số điện nước đầu/cuối tháng của phòng trọ. Hệ thống tự động tính toán tổng tiêu thụ Delta và xuất hóa đơn dịch vụ tương ứng.
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

    // API Chủ trọ cập nhật sửa đổi lại chỉ số điện nước chu kỳ do ghi nhận sai sót. Hệ thống tự động tính toán lại dư nợ và sửa trực tiếp trên Hóa đơn tương ứng chu kỳ đó.
    @PutMapping("/cap-nhat/{hoaDonId}")
    public ResponseEntity<?> capNhatSo(@PathVariable Long hoaDonId, @RequestBody ChiSoDienNuoc chiSoMoi) {
        try {
            PhieuTinhTienDTO ketQua = chiSoService.capNhatChiSoVaTinhTien(hoaDonId, chiSoMoi);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("trangThai", "LỖI CẬP NHẬT");
            errorResponse.put("thongBao", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // API tra cứu chi tiết thông tin chỉ số điện nước của một phòng trong chu kỳ tháng/năm xác định.
    @GetMapping("/chi-so")
    public ResponseEntity<ChiSoDienNuoc> layChiSo(@RequestParam Long phongId, @RequestParam Integer thang, @RequestParam Integer nam) {
        return chiSoService.layChiSoDienNuoc(phongId, thang, nam)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}