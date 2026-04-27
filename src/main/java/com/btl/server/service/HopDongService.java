package com.btl.server.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
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

        boolean daCoNguoiThu = hopDongRepository.existsByPhongTroAndTrangThai(phong, "ĐÃ_DUYỆT");
        if (daCoNguoiThu) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng này đã có người thuê chính thức!");
        }

        boolean daGuiYeuCau = hopDongRepository.existsByKhachHangAndPhongTroAndTrangThai(
                hopDong.getKhachHang(), phong, "CHỜ_DUYỆT");
        if (daGuiYeuCau) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã gửi yêu cầu thuê phòng này rồi, vui lòng chờ duyệt!");
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
    public HopDong capNhatTrangThaiHopDong(Integer hopDongId, String trangThaiMoi, TaiKhoan currentUser) {
        HopDong hd = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng!"));

        if (!currentUser.getRole().contains("ADMIN")) {
            if (!hd.getPhongTro().getChuTroId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền duyệt hợp đồng của khu trọ khác!");
            }
        }

        hd.setTrangThai(trangThaiMoi);

        if ("ĐÃ_DUYỆT".equals(trangThaiMoi) && hd.getPhongTro() != null) {
            PhongTro p = hd.getPhongTro();
            p.setTrangThai("Đã thuê");
            phongTroRepository.save(p);

            List<HopDong> danhSachCungPhong = hopDongRepository.findByPhongTro_Id(p.getId());
            for (HopDong hopDongKhac : danhSachCungPhong) {
                if (!hopDongKhac.getId().equals(hd.getId()) && "CHỜ_DUYỆT".equals(hopDongKhac.getTrangThai())) {
                    hopDongKhac.setTrangThai("TỪ_CHỐI");
                    hopDongRepository.save(hopDongKhac);
                }
            }
        }

        return hopDongRepository.save(hd);
    }
}