package com.btl.server.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng!"));

        if ("Đã thuê".equals(phong.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng đã có người thuê!");
        }
        return hopDongRepository.save(hopDong);
    }

    public List<HopDong> layTatCaHopDong() {
        return hopDongRepository.findAll();
    }

    public List<HopDong> layHopDongTheoChuTro(Integer chuTroId) {
        return hopDongRepository.findByPhongTro_ChuTroId(chuTroId);
    }

    
    public List<HopDong> layHopDongTheoKhach(Integer khachId) {
        return hopDongRepository.findByKhachHang_Id(khachId);
    }

    @Transactional
    public HopDong capNhatTrangThaiHopDong(Integer hopDongId, String trangThaiMoi) {
        HopDong hd = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng!"));
        
        hd.setTrangThai(trangThaiMoi);
        
        if ("ĐÃ_DUYỆT".equals(trangThaiMoi) && hd.getPhongTro() != null) {
            PhongTro p = hd.getPhongTro();
            p.setTrangThai("Đã thuê");
            phongTroRepository.save(p);
        }
        
        return hopDongRepository.save(hd);
    }
}