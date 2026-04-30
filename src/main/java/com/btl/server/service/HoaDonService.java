package com.btl.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.repository.HoaDonRepository;

@Service
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final NhatKyService nhatKyService;

    public HoaDonService(HoaDonRepository hoaDonRepository, NhatKyService nhatKyService) {
        this.hoaDonRepository = hoaDonRepository;
        this.nhatKyService = nhatKyService;
    }

    public void xoaHoaDonBiSai(Integer id) {
        if (!hoaDonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn này để xóa!");
        }
        
        hoaDonRepository.deleteById(id);
        nhatKyService.ghiLog("XÓA HÓA ĐƠN", "Đã xóa vĩnh viễn hóa đơn có ID = " + id);
    }
}