package com.btl.server.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.PhongTroRepository;

@Service
public class HopDongService {

    @Autowired
    private HopDongRepository hopDongRepository;

    @Autowired
    private PhongTroRepository phongTroRepository; 

    public HopDong taoHopDong(HopDong hopDong) {
        
        Integer idPhong = hopDong.getPhongTro().getId();
        
        PhongTro phong = phongTroRepository.findById(idPhong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lỗi: Không tìm thấy phòng trọ này!"));

        if ("Đã thuê".equals(phong.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CẢNH BÁO: Phòng này đã có người thuê, không thể ký hợp đồng!");
        }

        HopDong hopDongMoi = hopDongRepository.save(hopDong);

        phong.setTrangThai("Đã thuê");
        phongTroRepository.save(phong);

        return hopDongMoi;
    }

    public List<HopDong> layTatCaHopDong() {
        return hopDongRepository.findAll();
    }
}