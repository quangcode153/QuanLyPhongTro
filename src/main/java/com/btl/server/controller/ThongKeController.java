package com.btl.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.btl.server.dto.ThongKeDTO;
import com.btl.server.service.ThongKeService;

/**
 * REST Controller điều phối các API báo cáo thống kê doanh thu và hoạt động (`/api/thong-ke`).
 * Hỗ trợ hiển thị dữ liệu trực quan trên Dashboard của Chủ trọ.
 */
@RestController
@RequestMapping("/api/thong-ke")
public class ThongKeController {

    private final ThongKeService thongKeService;

    /**
     * Khởi tạo ThongKeController với ThongKeService.
     */
    public ThongKeController(ThongKeService thongKeService) {
        this.thongKeService = thongKeService;
    }

    /**
     * API Chủ trọ lấy toàn bộ dữ liệu thống kê tổng hợp của khu trọ mình quản lý.
     * Trả về: Tổng số phòng, số phòng trống, số khách thuê đang ở, số hóa đơn chưa thanh toán,
     * doanh thu tháng hiện tại, doanh thu tháng trước, tỷ lệ tăng trưởng và dữ liệu biểu đồ 6 tháng.
     * 
     * @param id ID của chủ trọ cần xem thống kê
     */
    @GetMapping("/chu-tro/{id}")
    public ResponseEntity<ThongKeDTO> getThongKeChuTro(@PathVariable Long id) {
        ThongKeDTO thongKe = thongKeService.layThongKeChuTro(id);
        return ResponseEntity.ok(thongKe);
    }
}
