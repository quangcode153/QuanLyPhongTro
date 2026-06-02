package com.btl.server.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.HoaDon;
import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiHoaDon;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.PhongTroRepository;
import com.btl.server.dto.PhieuTinhTienDTO;

/**
 * Service quản lý nghiệp vụ ghi chỉ số Điện và Nước hàng tháng cho các phòng trọ.
 * Bao gồm các nghiệp vụ cốt lõi: 
 * 1. Chốt số cũ, số mới và tự động tính toán sinh ra Hóa đơn chưa thanh toán.
 * 2. Cập nhật chỉ số điện nước khi có sai sót và tự động tính toán lại tổng tiền của Hóa đơn tương ứng.
 */
@Service
public class ChiSoDienNuocService {

    private static final Logger log = LoggerFactory.getLogger(ChiSoDienNuocService.class);

    private final ChiSoDienNuocRepository chiSoRepo;
    private final PhongTroRepository phongTroRepository;
    private final HoaDonRepository hoaDonRepository;

    // Đơn giá dịch vụ định mức
    private static final BigDecimal GIA_DIEN = new BigDecimal("3500.0");  // 3500đ / kWh
    private static final BigDecimal GIA_NUOC = new BigDecimal("20000.0"); // 20000đ / m3

    public ChiSoDienNuocService(ChiSoDienNuocRepository chiSoRepo, 
                                PhongTroRepository phongTroRepository, 
                                HoaDonRepository hoaDonRepository) {
        this.chiSoRepo = chiSoRepo;
        this.phongTroRepository = phongTroRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    /**
     * Chốt số điện nước hàng tháng và tự động lập hóa đơn dịch vụ mới.
     * Quy tắc kiểm tra nghiệp vụ:
     * 1. Hóa đơn cho chu kỳ tháng/năm của phòng trọ này phải chưa tồn tại (tránh chốt trùng 2 lần).
     * 2. Chỉ số mới nhập vào (điện/nước) không được nhỏ hơn chỉ số cũ ghi nhận tháng trước.
     * 
     * @param chiSo Thực thể chứa thông tin chỉ số điện nước cũ, mới cần chốt
     * @return PhieuTinhTienDTO Chứa thông tin chi tiết tính tiền trả về cho Client hiển thị đối soát lập tức
     */
    @Transactional
    public PhieuTinhTienDTO chotSoVaTinhTien(ChiSoDienNuoc chiSo) {
        
        Long idPhong = chiSo.getPhongTro().getId();
        Integer thang = chiSo.getThang();
        Integer nam = chiSo.getNam();

        // 1. Kiểm tra xem chu kỳ này đã chốt và xuất hóa đơn chưa
        if (hoaDonRepository.existsByPhongTroIdAndThangAndNam(idPhong, thang, nam)) {
            throw new BadRequestException("Dữ liệu tháng " + thang + "/" + nam + " của phòng này đã tồn tại. Vui lòng kiểm tra lại hoặc xóa hóa đơn cũ trước khi chốt số mới!");
        }

        // 2. Kiểm tra tính hợp lệ của số điện nước tiêu thụ
        if (chiSo.getSoDienMoi() < chiSo.getSoDienCu() || chiSo.getSoNuocMoi() < chiSo.getSoNuocCu()) {
            throw new BadRequestException("Chỉ số mới không được nhỏ hơn chỉ số cũ!");
        }

        PhongTro phong = phongTroRepository.findById(idPhong)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng này!"));

        BigDecimal giaPhong = phong.getGiaPhong();
        ChiSoDienNuoc daLuu = chiSoRepo.save(chiSo);

        // 3. Tính toán lượng tiêu thụ thực tế (Delta)
        int soDienDung = daLuu.getSoDienMoi() - daLuu.getSoDienCu();
        int soNuocDung = daLuu.getSoNuocMoi() - daLuu.getSoNuocCu();
        
        // 4. Nhân với đơn giá định mức
        BigDecimal tienDien = GIA_DIEN.multiply(BigDecimal.valueOf(soDienDung));
        BigDecimal tienNuoc = GIA_NUOC.multiply(BigDecimal.valueOf(soNuocDung));
        BigDecimal tongTien = giaPhong.add(tienDien).add(tienNuoc);

        // 5. Tự động sinh ra hóa đơn tương ứng ở trạng thái CHƯA THANH TOÁN
        HoaDon hoaDonMoi = new HoaDon();
        hoaDonMoi.setPhongTro(phong);
        hoaDonMoi.setThang(daLuu.getThang());
        hoaDonMoi.setNam(daLuu.getNam());
        hoaDonMoi.setTienPhong(giaPhong);
        hoaDonMoi.setTienDien(tienDien);
        hoaDonMoi.setTienNuoc(tienNuoc);
        hoaDonMoi.setTongTien(tongTien);
        hoaDonMoi.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN); 
        
        hoaDonRepository.save(hoaDonMoi);

        // 6. Đóng gói dữ liệu trả về cho giao diện (DTO)
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

        log.info("Chốt số điện nước thành công cho phòng ID: {}, Tháng: {}/{}", idPhong, thang, nam);
        return phieu;
    }

    /**
     * Cập nhật chỉ số điện nước khi chủ trọ phát hiện nhập sai sót.
     * Quy tắc kiểm tra nghiệp vụ nghiêm ngặt:
     * 1. Hóa đơn tương ứng phải ở trạng thái CHƯA THANH TOÁN (Nếu khách đã đóng tiền thì cấm sửa chỉ số).
     * 2. Chỉ số điện nước mới nhập vẫn phải đảm bảo số mới >= số cũ.
     * 
     * @param hoaDonId ID của hóa đơn muốn sửa chỉ số điện nước
     * @param chiSoMoi Chứa các thông tin số điện/nước cũ và mới đã được chỉnh sửa
     */
    @Transactional
    public PhieuTinhTienDTO capNhatChiSoVaTinhTien(Long hoaDonId, ChiSoDienNuoc chiSoMoi) {
        HoaDon hd = hoaDonRepository.findById(hoaDonId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn!"));

        // 1. Kiểm tra trạng thái hóa đơn: Cấm sửa khi đã hoàn tất thanh toán
        if (hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new BadRequestException("Hóa đơn đã được thanh toán, không thể thay đổi chỉ số điện nước!");
        }

        // 2. Đảm bảo tính hợp lý của số liệu
        if (chiSoMoi.getSoDienMoi() < chiSoMoi.getSoDienCu() || chiSoMoi.getSoNuocMoi() < chiSoMoi.getSoNuocCu()) {
            throw new BadRequestException("Chỉ số mới không được nhỏ hơn chỉ số cũ!");
        }

        ChiSoDienNuoc chiSoHienTai = chiSoRepo.findByPhongTroIdAndThangAndNam(hd.getPhongTro().getId(), hd.getThang(), hd.getNam())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy dữ liệu điện nước của tháng này!"));

        // 3. Tiến hành cập nhật lại các chỉ số điện/nước trong CSDL
        chiSoHienTai.setSoDienCu(chiSoMoi.getSoDienCu());
        chiSoHienTai.setSoDienMoi(chiSoMoi.getSoDienMoi());
        chiSoHienTai.setSoNuocCu(chiSoMoi.getSoNuocCu());
        chiSoHienTai.setSoNuocMoi(chiSoMoi.getSoNuocMoi());

        ChiSoDienNuoc daLuu = chiSoRepo.save(chiSoHienTai);

        // 4. Tính toán lại tiền điện nước và cập nhật trực tiếp vào Hóa đơn
        int soDienDung = daLuu.getSoDienMoi() - daLuu.getSoDienCu();
        int soNuocDung = daLuu.getSoNuocMoi() - daLuu.getSoNuocCu();
        
        BigDecimal tienDien = GIA_DIEN.multiply(BigDecimal.valueOf(soDienDung));
        BigDecimal tienNuoc = GIA_NUOC.multiply(BigDecimal.valueOf(soNuocDung));
        BigDecimal giaPhong = hd.getPhongTro().getGiaPhong();
        BigDecimal tongTien = giaPhong.add(tienDien).add(tienNuoc);

        hd.setTienDien(tienDien);
        hd.setTienNuoc(tienNuoc);
        hd.setTongTien(tongTien);
        
        hoaDonRepository.save(hd);

        // 5. Trả về phiếu tính tiền mới cho Client hiển thị đồng bộ
        PhieuTinhTienDTO phieu = new PhieuTinhTienDTO();
        phieu.setPhongId(hd.getPhongTro().getId());
        phieu.setThang(daLuu.getThang());
        phieu.setNam(daLuu.getNam());
        phieu.setGiaPhong(giaPhong);
        phieu.setSoDienDung(soDienDung);
        phieu.setTienDien(tienDien);
        phieu.setSoNuocDung(soNuocDung);
        phieu.setTienNuoc(tienNuoc);
        phieu.setTongTien(tongTien);

        log.info("Cập nhật số điện nước thành công cho hóa đơn ID: {}", hoaDonId);
        return phieu;
    }

    /**
     * Tra cứu nhanh bản ghi chỉ số điện nước của một phòng trong chu kỳ cụ thể.
     */
    public java.util.Optional<ChiSoDienNuoc> layChiSoDienNuoc(Long phongId, Integer thang, Integer nam) {
        return chiSoRepo.findByPhongTroIdAndThangAndNam(phongId, thang, nam);
    }
}