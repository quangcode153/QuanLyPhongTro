package com.btl.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.HoaDon;
import com.btl.server.entity.PhongTro;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.PhongTroRepository;
import com.btl.server.dto.PhieuTinhTienDTO;

@Service
public class ChiSoDienNuocService {

    @Autowired
    private ChiSoDienNuocRepository chiSoRepo;

    @Autowired
    private PhongTroRepository phongTroRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    private final double GIA_DIEN = 3500.0;
    private final double GIA_NUOC = 20000.0;

    public PhieuTinhTienDTO chotSoVaTinhTien(ChiSoDienNuoc chiSo) {
        
        Integer idPhong = chiSo.getPhongTro().getId();
        Integer thang = chiSo.getThang();
        Integer nam = chiSo.getNam();

        if (hoaDonRepository.existsByPhongTroIdAndThangAndNam(idPhong, thang, nam)) {
            throw new RuntimeException("Dữ liệu tháng " + thang + "/" + nam + " của phòng này đã tồn tại. Vui lòng kiểm tra lại hoặc xóa hóa đơn cũ trước khi chốt số mới!");
        }

        PhongTro phong = phongTroRepository.findById(idPhong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng này!"));

        double giaPhong = phong.getGiaPhong();
        ChiSoDienNuoc daLuu = chiSoRepo.save(chiSo);

        int soDienDung = daLuu.getSoDienMoi() - daLuu.getSoDienCu();
        int soNuocDung = daLuu.getSoNuocMoi() - daLuu.getSoNuocCu();
        
        double tienDien = soDienDung * GIA_DIEN;
        double tienNuoc = soNuocDung * GIA_NUOC;
        double tongTien = giaPhong + tienDien + tienNuoc;

        HoaDon hoaDonMoi = new HoaDon();
        hoaDonMoi.setPhongTro(phong);
        hoaDonMoi.setThang(daLuu.getThang());
        hoaDonMoi.setNam(daLuu.getNam());
        hoaDonMoi.setTongTien(tongTien);
        hoaDonMoi.setTrangThai("Chưa thanh toán");
        
        hoaDonRepository.save(hoaDonMoi);

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