package com.btl.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.repository.HoaDonRepository;

import com.btl.server.service.NhatKyService; 

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    // Gọi Thư ký ra
    @Autowired
    private NhatKyService nhatKyService;

    public void xoaHoaDonBịSai(Integer id) {
        if (!hoaDonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn này để xóa!");
        }
        
        // Tiến hành chém (Xóa)
        hoaDonRepository.deleteById(id);

        // ==========================================
        // CAMERA HOẠT ĐỘNG: Ghi lại ngay lập tức!
        // ==========================================
        nhatKyService.ghiLog("XÓA HÓA ĐƠN", "Đã xóa vĩnh viễn hóa đơn có ID = " + id);
    }
}