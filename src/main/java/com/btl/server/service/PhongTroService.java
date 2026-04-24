package com.btl.server.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btl.server.entity.PhongTro;
import com.btl.server.repository.PhongTroRepository;

@Service
public class PhongTroService {

    @Autowired
    private PhongTroRepository phongTroRepository;

    public List<PhongTro> getAllPhongs() {
        return phongTroRepository.findAll();
    }

    public PhongTro savePhong(PhongTro phongTro) {
        return phongTroRepository.save(phongTro);
    }

    public void deletePhong(Integer id) {
        phongTroRepository.deleteById(id);
    }

    public List<PhongTro> timPhongTheoTrangThai(String trangThai) {
        return phongTroRepository.findByTrangThai(trangThai);
    }

    public List<PhongTro> locPhongTheoGia(String trangThai, Double giaToiDa) {
        return phongTroRepository.findByTrangThaiAndGiaPhongLessThanEqual(trangThai, giaToiDa);
    }

    public PhongTro getPhongById(Integer id) {
        return phongTroRepository.findById(id).orElse(null);
    }

    public List<PhongTro> getPhongByChuTroId(Integer chuTroId) {
        return phongTroRepository.findByChuTroId(chuTroId);
    }
}