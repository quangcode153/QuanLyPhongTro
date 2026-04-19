package com.btl.server.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btl.server.entity.KhachHang;
import com.btl.server.dto.KhachHangDTO;
import com.btl.server.repository.KhachHangRepository;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public KhachHang saveKhach(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public List<KhachHangDTO> getKhachHangAnToan() {
        List<KhachHang> danhSachGoc = khachHangRepository.findAll();
        List<KhachHangDTO> danhSachDTO = new ArrayList<>();

        for (KhachHang kh : danhSachGoc) {
            KhachHangDTO dto = new KhachHangDTO();
            dto.setTenKhach(kh.getTenKhach());
            dto.setSoDienThoai(kh.getSoDienThoai());
            
            if (kh.getPhongTro() != null) {
                dto.setTenPhongDangThue(kh.getPhongTro().getTenPhong());
            } else {
                dto.setTenPhongDangThue("Chưa thuê phòng");
            }
            
            danhSachDTO.add(dto);
        }
        
        return danhSachDTO;
    }
}