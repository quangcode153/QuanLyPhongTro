package com.btl.server.service;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.HoaDon;
import com.btl.server.enums.TrangThaiHoaDon;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service quản lý toàn bộ nghiệp vụ liên quan đến Hóa Đơn hàng tháng.
 * Thực hiện: Lấy danh sách hóa đơn của khách/chủ trọ, bổ sung chi tiết hóa đơn
 * dựa trên chỉ số điện nước,
 * Thanh toán hóa đơn và xóa các hóa đơn bị sai sót kèm theo dọn dẹp chỉ số điện
 * nước tương ứng.
 */
@Service
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiSoDienNuocRepository chiSoRepo;
    private final NhatKyService nhatKyService;

    // Đơn giá định mức mặc định nếu phòng không cấu hình giá riêng
    private static final BigDecimal GIA_DIEN = new BigDecimal("3500.0"); // 3500đ / kWh
    private static final BigDecimal GIA_NUOC = new BigDecimal("20000.0"); // 20000đ / m3

    public HoaDonService(HoaDonRepository hoaDonRepository,
            ChiSoDienNuocRepository chiSoRepo,
            NhatKyService nhatKyService) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiSoRepo = chiSoRepo;
        this.nhatKyService = nhatKyService;
    }

    /**
     * Xóa một hóa đơn bị nhập sai dữ liệu.
     * Quy trình nghiệp vụ:
     * 1. Tìm thực thể hóa đơn theo ID.
     * 2. Tìm và tự động xóa luôn bản ghi chỉ số điện nước tương ứng của phòng đó
     * trong tháng/năm đó để tránh lệch số liệu.
     * 3. Thực hiện xóa hóa đơn.
     * 4. Ghi nhận log lịch sử vào Nhật ký hệ thống.
     * 
     * @param id ID của hóa đơn cần xóa
     */
    @Transactional
    public void xoaHoaDonBiSai(Long id) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn này để xóa!"));

        // Tìm và xóa bản ghi chỉ số điện nước cùng chu kỳ
        chiSoRepo.findByPhongTroIdAndThangAndNam(hd.getPhongTro().getId(), hd.getThang(), hd.getNam())
                .ifPresent(chiSo -> chiSoRepo.delete(chiSo));

        // Xóa hóa đơn chính
        hoaDonRepository.delete(hd);

        // Ghi nhận nhật ký hệ thống
        nhatKyService.ghiLog("XÓA HÓA ĐƠN",
                "Đã xóa hóa đơn và chỉ số điện nước tháng " + hd.getThang() + "/" + hd.getNam() + " (ID = " + id + ")");
    }

    /**
     * Lấy danh sách toàn bộ hóa đơn của tất cả các phòng do Chủ trọ quản lý.
     * 
     * @param chuTroId ID tài khoản của chủ trọ
     */
    public List<HoaDon> layHoaDonCuaChuTro(Long chuTroId) {
        return hoaDonRepository.findByPhongTroChuTroId(chuTroId);
    }

    /**
     * Lấy danh sách hóa đơn của Khách thuê đang ở.
     * Đồng thời tự động "Lazy Load" bổ sung thêm các chi tiết dịch vụ (tiền phòng,
     * tiền điện, tiền nước)
     * nếu các thông tin này bị trống trong cơ sở dữ liệu để hiển thị chi tiết trực
     * quan nhất ở Frontend.
     * 
     * @param khachHangId ID tài khoản của khách thuê
     */
    public List<HoaDon> layDanhSachHoaDonCuaKhach(Long khachHangId) {
        List<HoaDon> dsHoaDon = hoaDonRepository.findHoaDonByKhachHangId(khachHangId);

        // Duyệt qua từng hóa đơn và bổ sung chi tiết nếu bị thiếu
        for (HoaDon hd : dsHoaDon) {
            if (hd.getTienPhong() == null) {
                boSungChiTietHoaDon(hd);
            }
        }
        return dsHoaDon;
    }

    /**
     * Bổ sung chi tiết các khoản chi phí cho hóa đơn (Tính toán lại dựa trên chênh
     * lệch số điện/nước cũ và mới).
     */
    private void boSungChiTietHoaDon(HoaDon hd) {
        try {
            BigDecimal giaPhong = hd.getPhongTro().getGiaPhong();
            hd.setTienPhong(giaPhong);

            // Tìm bản ghi chỉ số điện nước để tính lượng tiêu thụ thực tế
            chiSoRepo.findByPhongTroIdAndThangAndNam(
                    hd.getPhongTro().getId(), hd.getThang(), hd.getNam()).ifPresent(chiSo -> {
                        int soDien = chiSo.getSoDienMoi() - chiSo.getSoDienCu();
                        int soNuoc = chiSo.getSoNuocMoi() - chiSo.getSoNuocCu();
                        hd.setTienDien(GIA_DIEN.multiply(BigDecimal.valueOf(soDien)));
                        hd.setTienNuoc(GIA_NUOC.multiply(BigDecimal.valueOf(soNuoc)));
                    });

            // Nếu không tìm thấy chỉ số điện nước nhưng đã có tổng tiền trước đó
            if (hd.getTienDien() == null && hd.getTongTien() != null) {
                BigDecimal conLai = hd.getTongTien().subtract(giaPhong);
                hd.setTienDien(BigDecimal.ZERO);
                hd.setTienNuoc(conLai.compareTo(BigDecimal.ZERO) > 0 ? conLai : BigDecimal.ZERO);
            }

            // Lưu cập nhật chi tiết hóa đơn
            hoaDonRepository.save(hd);
        } catch (Exception e) {
            // Phòng ngừa lỗi tính toán văng ra, đặt các giá trị mặc định để giao diện không
            // bị lỗi Crash
            if (hd.getTienPhong() == null)
                hd.setTienPhong(BigDecimal.ZERO);
            if (hd.getTienDien() == null)
                hd.setTienDien(BigDecimal.ZERO);
            if (hd.getTienNuoc() == null)
                hd.setTienNuoc(BigDecimal.ZERO);
        }
    }

    /**
     * Đánh dấu hóa đơn đã được thanh toán thành công (Khách chuyển khoản hoặc trả
     * tiền mặt).
     * 
     * @param id          ID của hóa đơn
     * @param khachHangId ID của khách thanh toán
     */
    @Transactional
    public void thanhToanHoaDon(Long id, Long khachHangId) {
        HoaDon hd = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn!"));

        hd.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonRepository.save(hd);

        // Ghi lịch sử hoạt động để chủ trọ đối soát sau này
        nhatKyService.ghiLog("THANH TOÁN", "Hóa đơn ID " + id + " đã được thanh toán thành công.");
    }
}