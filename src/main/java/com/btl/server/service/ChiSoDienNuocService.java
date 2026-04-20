package com.btl.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.PhongTro;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.PhongTroRepository;
import com.btl.server.dto.PhieuTinhTienDTO;

@Service
public class ChiSoDienNuocService {

    @Autowired
    private ChiSoDienNuocRepository chiSoRepo;

    @Autowired
    private PhongTroRepository phongTroRepository;

    private final double GIA_DIEN = 3500.0;
    private final double GIA_NUOC = 20000.0;

    public PhieuTinhTienDTO chotSoVaTinhTien(ChiSoDienNuoc chiSo) {
        
        Integer idPhong = chiSo.getPhongTro().getId();

        PhongTro phong = phongTroRepository.findById(idPhong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng này!"));

        double giaPhong = phong.getGiaPhong();

        ChiSoDienNuoc daLuu = chiSoRepo.save(chiSo);

        int soDienDung = daLuu.getSoDienMoi() - daLuu.getSoDienCu();
        int soNuocDung = daLuu.getSoNuocMoi() - daLuu.getSoNuocCu();
        
        double tienDien = soDienDung * GIA_DIEN;
        double tienNuoc = soNuocDung * GIA_NUOC;
        
        double tongTien = giaPhong + tienDien + tienNuoc;

        PhieuTinhTienDTO phieu = new PhieuTinhTienDTO();
        phieu.setPhongId(phong.getId());
        phieu.setThang(daLuu.getThang());
        phieu.setNam(daLuu.getNam());
        phieu.setGiaPhong(giaPhong);
        phieu.setSoDienDung(soDienDung);
        phieu.setTienDien(tienDien);
        phieu.setSoNuocDung(soNuocDung);
        phieu.setTienNuoc(tienNuoc);
        phieu.setTongTien(tongTien);

        return phieu;
    }
}