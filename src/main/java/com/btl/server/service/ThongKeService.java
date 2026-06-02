package com.btl.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.btl.server.dto.ThongKeDTO;
import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.PhongTroRepository;

/**
 * Service tính toán dữ liệu báo cáo thống kê tài chính dành riêng cho Chủ trọ.
 * Hỗ trợ hiển thị trên biểu đồ và bảng điều khiển (Dashboard) của chủ trọ, bao gồm:
 * - Tổng số lượng phòng, phòng trống, phòng đã thuê.
 * - Thống kê số lượng hóa đơn chưa thanh toán và tổng dư nợ.
 * - Tính toán doanh thu tháng này so với tháng trước và tỷ lệ tăng trưởng.
 * - Xuất chuỗi dữ liệu 6 tháng gần nhất để vẽ biểu đồ cột doanh thu.
 */
@Service
public class ThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final PhongTroRepository phongTroRepository;

    /**
     * Khởi tạo ThongKeService với repository hóa đơn và phòng trọ.
     */
    public ThongKeService(HoaDonRepository hoaDonRepository, PhongTroRepository phongTroRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.phongTroRepository = phongTroRepository;
    }

    /**
     * Thu thập và tính toán toàn bộ số liệu thống kê của Chủ trọ.
     * 
     * @param chuTroId ID tài khoản của chủ trọ cần xem báo cáo
     * @return ThongKeDTO chứa trọn bộ dữ liệu thống kê
     */
    public ThongKeDTO layThongKeChuTro(Long chuTroId) {
        ThongKeDTO dto = new ThongKeDTO();

        // 1. Đếm tổng số lượng phòng và phân loại trạng thái (Trống / Đã thuê)
        List<PhongTro> cacPhong = phongTroRepository.findByChuTroId(chuTroId);
        int tongSoPhong = cacPhong.size();
        int soPhongDaThue = 0;
        int soPhongTrong = 0;

        for (PhongTro p : cacPhong) {
            if (p.getTrangThai() == TrangThaiPhong.DA_THUE) {
                soPhongDaThue++;
            } else if (p.getTrangThai() == TrangThaiPhong.TRONG) {
                soPhongTrong++;
            }
        }
        
        dto.setTongSoPhong(tongSoPhong);
        dto.setSoPhongDaThue(soPhongDaThue);
        dto.setSoPhongTrong(soPhongTrong);

        // 2. Thống kê nợ đọng: Hóa đơn chưa đóng tiền và tổng tiền nợ của cả khu trọ
        Integer countChuaThanhToan = hoaDonRepository.countHoaDonChuaThanhToan(chuTroId);
        BigDecimal tienChuaThanhToan = hoaDonRepository.sumTienChuaThanhToan(chuTroId);
        
        dto.setSoHoaDonChuaThanhToan(countChuaThanhToan != null ? countChuaThanhToan : 0);
        dto.setTongTienChuaThanhToan(tienChuaThanhToan != null ? tienChuaThanhToan : BigDecimal.ZERO);

        // 3. Tính doanh thu của Tháng Này và Tháng Trước phục vụ so sánh tăng trưởng
        LocalDate now = LocalDate.now();
        int thangHienTai = now.getMonthValue();
        int namHienTai = now.getYear();

        int thangTruoc = thangHienTai == 1 ? 12 : thangHienTai - 1;
        int namTruoc = thangHienTai == 1 ? namHienTai - 1 : namHienTai;

        BigDecimal doanhThuThangNay = hoaDonRepository.sumDoanhThuByChuTroAndThangNam(chuTroId, thangHienTai, namHienTai);
        if (doanhThuThangNay == null) doanhThuThangNay = BigDecimal.ZERO;

        BigDecimal doanhThuThangTruoc = hoaDonRepository.sumDoanhThuByChuTroAndThangNam(chuTroId, thangTruoc, namTruoc);
        if (doanhThuThangTruoc == null) doanhThuThangTruoc = BigDecimal.ZERO;

        dto.setTongDoanhThuThangNay(doanhThuThangNay);
        dto.setTongDoanhThuThangTruoc(doanhThuThangTruoc);

        // 4. Tính tỷ lệ tăng trưởng doanh thu giữa hai tháng liên tiếp (%)
        if (doanhThuThangTruoc.compareTo(BigDecimal.ZERO) == 0) {
            if (doanhThuThangNay.compareTo(BigDecimal.ZERO) > 0) {
                dto.setTyLeTangTruong(100.0); // Tăng trưởng tuyệt đối 100% nếu tháng trước bằng 0 và tháng này có doanh thu
            } else {
                dto.setTyLeTangTruong(0.0);
            }
        } else {
            BigDecimal chenhLech = doanhThuThangNay.subtract(doanhThuThangTruoc);
            // Phép chia làm tròn toán học lấy 4 chữ số thập phân trước khi nhân với 100
            BigDecimal phanTram = chenhLech.divide(doanhThuThangTruoc, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            dto.setTyLeTangTruong(phanTram.doubleValue());
        }

        // 5. Kết xuất chuỗi dữ liệu doanh thu của 6 tháng gần nhất để vẽ biểu đồ
        List<ThongKeDTO.BieuDoDoanhThu> bieuDo = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate t = now.minusMonths(i);
            BigDecimal dt = hoaDonRepository.sumDoanhThuByChuTroAndThangNam(chuTroId, t.getMonthValue(), t.getYear());
            if (dt == null) dt = BigDecimal.ZERO;
            bieuDo.add(new ThongKeDTO.BieuDoDoanhThu(t.getMonthValue(), t.getYear(), dt));
        }
        dto.setBieuDoDoanhThu(bieuDo);

        return dto;
    }
}
