package com.btl.server.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.PhongTroRepository;

@Service
public class PhongTroService {

    private static final Logger log = LoggerFactory.getLogger(PhongTroService.class);

    private final PhongTroRepository phongTroRepository;
    private final HopDongRepository hopDongRepository;

    public PhongTroService(PhongTroRepository phongTroRepository, HopDongRepository hopDongRepository) {
        this.phongTroRepository = phongTroRepository;
        this.hopDongRepository = hopDongRepository;
    }

    public List<PhongTro> getAllPhongs() {
        return phongTroRepository.findAll();
    }

    public PhongTro savePhong(PhongTro phongTro) {
        return phongTroRepository.save(phongTro);
    }

    public PhongTro getPhongById(Long id) {
        return phongTroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng trọ có ID: " + id));
    }

    public List<PhongTro> getPhongByChuTroId(Long chuTroId) {
        return phongTroRepository.findByChuTroId(chuTroId);
    }

   public List<PhongTro> timPhongTheoTrangThai(TrangThaiPhong trangThai) {
        return phongTroRepository.findByTrangThai(trangThai);
    }

    public List<PhongTro> locPhongTheoGia(TrangThaiPhong trangThai,BigDecimal  giaToiDa) {
        return phongTroRepository.findByTrangThaiAndGiaPhongLessThanEqual(trangThai, giaToiDa);
    }

    @Transactional
    public PhongTro capNhatTrangThaiPhong(Long id, TrangThaiPhong trangThaiMoi) {
        PhongTro existingPhong = getPhongById(id);
        existingPhong.setTrangThai(trangThaiMoi);
        phongTroRepository.save(existingPhong);

        if (trangThaiMoi == TrangThaiPhong.TRONG) {
            int count = hopDongRepository.ketThucHopDongTheoPhong(id, TrangThaiHopDong.DA_DUYET, TrangThaiHopDong.DA_KET_THUC);
            if (count > 0) {
                log.info("Phòng {} chuyển trạng thái TRỐNG. Đã thanh lý tự động {} hợp đồng.", id, count);
            }
        }
        return existingPhong;
    }

    @Transactional
    public void deletePhong(Long id) {
        PhongTro existingPhong = getPhongById(id);
        
        int count = hopDongRepository.ketThucHopDongTheoPhong(id, TrangThaiHopDong.DA_DUYET, TrangThaiHopDong.DA_KET_THUC);
        hopDongRepository.ketThucHopDongTheoPhong(id, TrangThaiHopDong.CHO_DUYET, TrangThaiHopDong.HUY);
        
        log.warn("Đã soft-delete (HỦY/KẾT THÚC) {} hợp đồng trước khi xóa phòng ID: {}", count, id);
        phongTroRepository.delete(existingPhong);
    }
}