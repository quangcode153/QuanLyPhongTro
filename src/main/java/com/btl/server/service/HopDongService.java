package com.btl.server.service;

import java.util.Arrays;
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
        Long idPhong = hopDong.getPhongTro().getId();
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

    public List<HopDong> layHopDongTheoChuTro(Long chuTroId) {
        return hopDongRepository.findByPhongTro_ChuTroId(chuTroId);
    }

    public List<HopDong> layHopDongTheoKhach(Long khachId) {
        return hopDongRepository.findByKhachHang_Id(khachId);
    }

     public List<HopDong> layHopDongTheoKhachVaTrangThai(Long khachId, String trangThai) {
        return hopDongRepository.findByKhachHang_IdAndTrangThai(khachId, trangThai);
    }

    @Transactional
    public HopDong capNhatTrangThaiHopDong(Long hopDongId, String trangThaiMoi, TaiKhoan currentUser) {
        HopDong hd = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hợp đồng!"));

        if (!currentUser.getRole().contains("ADMIN")) {
             if (!hd.getPhongTro().getChuTroId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền duyệt hợp đồng của khu trọ khác!");
            }
        }

        hd.setTrangThai(trangThaiMoi);

        if (hd.getPhongTro() != null) {
            PhongTro p = hd.getPhongTro();
            
            if ("ĐÃ_DUYỆT".equals(trangThaiMoi)) {
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
            else if (Arrays.asList("TỪ_CHỐI", "HỦY", "ĐÃ_KẾT_THÚC").contains(trangThaiMoi)) {
                boolean conNguoiKhacThue = hopDongRepository
                        .existsByPhongTro_IdAndTrangThaiAndIdNot(p.getId(), "ĐÃ_DUYỆT", hd.getId());
                
                if (!conNguoiKhacThue) {
                    p.setTrangThai("Trống");
                    phongTroRepository.save(p);
                }
            }
        }

        return hopDongRepository.save(hd);
    }
}