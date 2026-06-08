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
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.ChiSoDienNuocRepository;

// Service phòng trọ
@Service
public class PhongTroService {
    private static final Logger log = LoggerFactory.getLogger(PhongTroService.class);

    private final PhongTroRepository phongTroRepository;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ChiSoDienNuocRepository chiSoDienNuocRepository;

    public PhongTroService(PhongTroRepository phongTroRepository, 
                           HopDongRepository hopDongRepository,
                           HoaDonRepository hoaDonRepository,
                           ChiSoDienNuocRepository chiSoDienNuocRepository) {
        this.phongTroRepository = phongTroRepository;
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.chiSoDienNuocRepository = chiSoDienNuocRepository;
    }

    // Lấy tất cả phòng trọ
    public List<PhongTro> getAllPhongs() {
        return phongTroRepository.findAll();
    }

    // Lưu/Cập nhật phòng trọ.
    public PhongTro savePhong(PhongTro phongTro) {
        return phongTroRepository.save(phongTro);
    }

    // Tìm phòng trọ theo ID.
    public PhongTro getPhongById(Long id) {
        return phongTroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng trọ có ID: " + id));
    }

    // Tìm danh sách các phòng trọ do một chủ trọ quản lý.
    public List<PhongTro> getPhongByChuTroId(Long chuTroId) {
        return phongTroRepository.findByChuTroId(chuTroId);
    }

    // Tìm kiếm các phòng trọ theo trạng thái (ví dụ: TRỐNG, ĐÃ THUÊ, ĐANG SỬA).
    public List<PhongTro> timPhongTheoTrangThai(TrangThaiPhong trangThai) {
        return phongTroRepository.findByTrangThai(trangThai);
    }

    // Lọc danh sách các phòng trọ theo trạng thái và có giá thuê nhỏ hơn hoặc bằng một mức giá tối đa.
    public List<PhongTro> locPhongTheoGia(TrangThaiPhong trangThai, BigDecimal giaToiDa) {
        return phongTroRepository.findByTrangThaiAndGiaPhongLessThanEqual(trangThai, giaToiDa);
    }

    // Tìm kiếm phòng trọ
    public List<PhongTro> searchPhongTro(String tenPhong, String diaChi, BigDecimal giaToiThieu, BigDecimal giaToiDa, TrangThaiPhong trangThai) {
        return phongTroRepository.searchPhongTro(tenPhong, diaChi, giaToiThieu, giaToiDa, trangThai);
    }

    // Cập nhật trạng thái phòng. Nếu phòng trọ được chuyển sang trạng thái TRỐNG (nghĩa là khách dọn đi hoặc thanh lý xong), hệ thống sẽ tự động cập nhật kết thúc (chuyển sang trạng thái DA_KET_THUC) cho toàn bộ các hợp đồng đang có hiệu lực (DA_DUYET) của phòng đó.
    @Transactional
    public PhongTro capNhatTrangThaiPhong(Long id, TrangThaiPhong trangThaiMoi) {
        PhongTro existingPhong = getPhongById(id);
        existingPhong.setTrangThai(trangThaiMoi);
        phongTroRepository.save(existingPhong);

        // Logic nghiệp vụ tự động: Nếu chủ trọ chủ động trả phòng về trạng thái TRỐNG
        if (trangThaiMoi == TrangThaiPhong.TRONG) {
            // Tự động kết thúc tất cả hợp đồng đang hoạt động của phòng này
            int count = hopDongRepository.ketThucHopDongTheoPhong(id, TrangThaiHopDong.DA_DUYET, TrangThaiHopDong.DA_KET_THUC);
            if (count > 0) {
                log.info("Phòng {} chuyển trạng thái TRỐNG. Đã thanh lý tự động {} hợp đồng.", id, count);
            }
        }
        return existingPhong;
    }

    // Xóa phòng trọ
    @Transactional
    public void deletePhong(Long id) {
        PhongTro existingPhong = getPhongById(id);
        
        // 1. Kiểm tra điều kiện nghiệp vụ: Không xóa phòng đang có người ở
        if (existingPhong.getTrangThai() == TrangThaiPhong.DA_THUE) {
            throw new com.btl.server.exception.BadRequestException("Không thể xóa phòng đang có khách thuê! Vui lòng thanh lý hợp đồng trước.");
        }
        
        // 2. Dọn sạch dữ liệu liên quan ở các bảng con để tránh lỗi ràng buộc CSDL (Referential Integrity)
        chiSoDienNuocRepository.deleteByPhongTro_Id(id); // Xóa lịch sử điện nước
        hoaDonRepository.deleteByPhongTro_Id(id);        // Xóa tất cả các hóa đơn dịch vụ
        hopDongRepository.deleteByPhongTro_Id(id);       // Xóa tất cả hợp đồng (kể cả nháp hoặc đã duyệt)
        
        // 3. Xóa bản ghi phòng trọ chính
        phongTroRepository.delete(existingPhong);
        log.info("Đã xóa sạch dữ liệu liên quan và phòng ID: {}", id);
    }
}