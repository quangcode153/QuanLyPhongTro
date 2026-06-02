package com.btl.server.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btl.server.dto.HopDongRequestDTO;
import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.PhongTroRepository;

/**
 * Service quản lý toàn bộ nghiệp vụ liên quan đến Hợp Đồng Thuê Phòng.
 * Bao gồm các tính năng: Khách gửi yêu cầu thuê, Chủ trọ duyệt hợp đồng (tự động bác bỏ yêu cầu khác),
 * Gia hạn hợp đồng, Khách gửi yêu cầu hủy hợp đồng và Chủ trọ phê duyệt hủy hợp đồng.
 */
@Service
public class HopDongService {

    private static final Logger log = LoggerFactory.getLogger(HopDongService.class);

    private final HopDongRepository hopDongRepository;
    private final PhongTroRepository phongTroRepository;

    /**
     * Khởi tạo HopDongService với các repository cần thiết.
     */
    public HopDongService(HopDongRepository hopDongRepository, PhongTroRepository phongTroRepository) {
        this.hopDongRepository = hopDongRepository;
        this.phongTroRepository = phongTroRepository;
    }

    /**
     * Tạo yêu cầu thuê phòng mới (do Khách thuê khởi tạo).
     * Các quy tắc kiểm tra nghiêm ngặt:
     * 1. Ngày kết thúc phải sau ngày bắt đầu.
     * 2. Phòng trọ mục tiêu phải tồn tại trong hệ thống.
     * 3. Phòng trọ phải ở trạng thái TRỐNG.
     * 4. Phòng trọ chưa có bất kỳ hợp đồng nào đã duyệt (Đang có hiệu lực).
     * 5. Khách thuê chưa từng gửi yêu cầu thuê cho phòng này mà đang trong trạng thái chờ duyệt (tránh gửi trùng).
     * 
     * @param request DTO chứa thông tin ngày thuê, ngày kết thúc và tiền cọc mong muốn
     * @param khachHang Tài khoản khách thuê đang đăng nhập
     * @return Hợp đồng mới được tạo ở trạng thái CHO_DUYET
     */
    public HopDong taoHopDong(HopDongRequestDTO request, TaiKhoan khachHang) {
        
        // 1. Kiểm tra ngày tháng thuê hợp lý
        if (request.getNgayKetThuc() != null && request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu!");
        }

        // 2. Tìm kiếm sự tồn tại của phòng trọ
        PhongTro phong = phongTroRepository.findById(request.getPhongTroId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng!"));

        // 3. Kiểm tra xem phòng có đang trống để thuê không
        if (phong.getTrangThai() != TrangThaiPhong.TRONG) {
            throw new BadRequestException("Phòng không ở trạng thái TRỐNG, không thể thuê!");
        }

        // 4. Kiểm tra phòng đã được thuê chính thức bởi người khác chưa
        if (hopDongRepository.existsByPhongTroAndTrangThai(phong, TrangThaiHopDong.DA_DUYET)) {
            throw new BadRequestException("Phòng này đã có người thuê chính thức!");
        }

        // 5. Tránh việc một khách hàng bấm gửi yêu cầu liên tiếp nhiều lần cho cùng một phòng
        if (hopDongRepository.existsByKhachHangAndPhongTroAndTrangThai(khachHang, phong, TrangThaiHopDong.CHO_DUYET)) {
            throw new BadRequestException("Bạn đã gửi yêu cầu thuê phòng này rồi, vui lòng chờ duyệt!");
        }

        // Tạo bản ghi hợp đồng mới ở trạng thái chờ duyệt
        HopDong hopDong = new HopDong();
        hopDong.setKhachHang(khachHang);
        hopDong.setPhongTro(phong);
        hopDong.setNgayBatDau(request.getNgayBatDau());
        hopDong.setNgayKetThuc(request.getNgayKetThuc());
        hopDong.setTienCoc(request.getTienCoc() != null ? request.getTienCoc() : BigDecimal.ZERO);
        hopDong.setTrangThai(TrangThaiHopDong.CHO_DUYET);

        log.info("Khách hàng {} tạo yêu cầu thuê phòng {}", khachHang.getUsername(), phong.getId());
        return hopDongRepository.save(hopDong);
    }

    /**
     * Lấy toàn bộ danh sách hợp đồng (chỉ dùng cho ADMIN).
     */
    public List<HopDong> layTatCaHopDong() {
        return hopDongRepository.findAll();
    }

    /**
     * Lấy danh sách hợp đồng của tất cả các phòng thuộc về một Chủ trọ.
     * @param chuTroId ID tài khoản của Chủ trọ
     */
    public List<HopDong> layHopDongTheoChuTro(Long chuTroId) {
        return hopDongRepository.findByPhongTro_ChuTroId(chuTroId);
    }

    /**
     * Lấy danh sách lịch sử hợp đồng của một Khách thuê.
     * @param khachId ID tài khoản của Khách thuê
     */
    public List<HopDong> layHopDongTheoKhach(Long khachId) {
        return hopDongRepository.findByKhachHang_Id(khachId);
    }

    /**
     * Lọc danh sách hợp đồng của Khách thuê dựa vào trạng thái cụ thể.
     */
    public List<HopDong> layHopDongTheoKhachVaTrangThai(Long khachId, TrangThaiHopDong trangThai) {
        return hopDongRepository.findByKhachHang_IdAndTrangThai(khachId, trangThai);
    }

    /**
     * Cập nhật trạng thái hợp đồng (Chủ trọ hoặc Admin phê duyệt duyệt/từ chối/thanh lý).
     * Luồng nghiệp vụ đặc biệt khi DUYỆT hợp đồng:
     * - Khi một hợp đồng được duyệt (`DA_DUYET`):
     *   1. Trạng thái phòng trọ chuyển thành `DA_THUE`.
     *   2. Tự động bác bỏ và chuyển toàn bộ các yêu cầu thuê phòng đang chờ duyệt khác (`CHO_DUYET`) của phòng này
     *      sang trạng thái từ chối (`TU_CHOI`) để tránh trùng lặp.
     * - Khi kết thúc/hủy/từ chối hợp đồng:
     *   1. Kiểm tra xem phòng đó còn có hợp đồng nào đang hiệu lực khác không.
     *   2. Nếu không còn ai thuê, tự động chuyển trạng thái phòng về `TRONG` để người khác có thể thuê.
     * 
     * @param hopDongId ID hợp đồng cần xử lý
     * @param trangThaiMoi Trạng thái mới muốn cập nhật
     * @param ngayKetThuc Cập nhật ngày kết thúc thực tế (nếu có)
     * @param currentUser Tài khoản thực hiện (để kiểm tra quyền sở hữu)
     */
    @Transactional
    public HopDong capNhatTrangThaiHopDong(Long hopDongId, TrangThaiHopDong trangThaiMoi, LocalDate ngayKetThuc, TaiKhoan currentUser) {
        HopDong hd = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng!"));

        // Kiểm tra quyền hạn: Nếu không phải Admin thì phải đúng là Chủ trọ sở hữu căn phòng trọ này mới được duyệt
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
             if (!hd.getPhongTro().getChuTroId().equals(currentUser.getId())) {
                throw new ForbiddenException("Bạn không có quyền thao tác trên hợp đồng của khu trọ khác!");
            }
        }

        // Kiểm tra trùng lặp: Nếu duyệt hợp đồng, đảm bảo phòng này chưa có hợp đồng đã duyệt khác hoạt động song song
        if (trangThaiMoi == TrangThaiHopDong.DA_DUYET) {
            if (hopDongRepository.existsByPhongTro_IdAndTrangThaiAndIdNot(hd.getPhongTro().getId(), TrangThaiHopDong.DA_DUYET, hd.getId())) {
                throw new BadRequestException("Phòng này đã có hợp đồng ĐÃ DUYỆT khác, không thể duyệt thêm!");
            }
        }

        hd.setTrangThai(trangThaiMoi);
        if (ngayKetThuc != null) {
            hd.setNgayKetThuc(ngayKetThuc);
        }

        if (hd.getPhongTro() != null) {
            PhongTro p = hd.getPhongTro();
            
            // Xử lý đổi trạng thái phòng tương ứng khi duyệt hợp đồng thuê
            if (trangThaiMoi == TrangThaiHopDong.DA_DUYET) {
                p.setTrangThai(TrangThaiPhong.DA_THUE);
                phongTroRepository.save(p);

                // BULK UPDATE: Tự động từ chối tất cả khách hàng khác đang gửi yêu cầu chờ duyệt cùng phòng này
                int rejectedCount = hopDongRepository.tuChoiCacHopDongChoDuyetKhac(
                        p.getId(), hd.getId(), TrangThaiHopDong.CHO_DUYET, TrangThaiHopDong.TU_CHOI);
                
                log.info("Đã dùng Bulk Update từ chối tự động {} hợp đồng khác của phòng {}", rejectedCount, p.getId());
            } 
            // Nếu hợp đồng kết thúc/hủy/thanh lý, trả phòng về trạng thái trống nếu không còn ai thuê chính thức
            else if (Arrays.asList(TrangThaiHopDong.TU_CHOI, TrangThaiHopDong.HUY, TrangThaiHopDong.DA_KET_THUC, TrangThaiHopDong.DA_THANH_LY).contains(trangThaiMoi)) {
                boolean conNguoiKhacThue = hopDongRepository
                        .existsByPhongTro_IdAndTrangThaiAndIdNot(p.getId(), TrangThaiHopDong.DA_DUYET, hd.getId());
                
                if (!conNguoiKhacThue) {
                    p.setTrangThai(TrangThaiPhong.TRONG);
                    phongTroRepository.save(p);
                }
            }
        }

        log.info("User {} chuyển trạng thái hợp đồng ID {} thành {}", currentUser.getUsername(), hopDongId, trangThaiMoi);
        return hopDongRepository.save(hd);
    }

    /**
     * Gia hạn thêm thời gian thuê cho hợp đồng hiện tại (Chủ trọ thực hiện).
     * @param id ID của hợp đồng cần gia hạn
     * @param ngayKetThucMoi Ngày hết hạn mới (phải sau ngày bắt đầu ban đầu)
     * @param currentUser Người thực hiện thao tác
     */
    @Transactional
    public HopDong giaHanHopDong(Long id, LocalDate ngayKetThucMoi, TaiKhoan currentUser) {
        HopDong hd = hopDongRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng!"));

        // Kiểm tra quyền: Chỉ Admin hoặc Chủ trọ của phòng này mới được phép gia hạn hợp đồng
        if (!"ROLE_ADMIN".equals(currentUser.getRole()) && !hd.getPhongTro().getChuTroId().equals(currentUser.getId())) {
            throw new ForbiddenException("Không có quyền gia hạn hợp đồng này!");
        }

        // Kiểm tra logic ngày hợp lệ
        if (ngayKetThucMoi != null && ngayKetThucMoi.isBefore(hd.getNgayBatDau())) {
            throw new BadRequestException("Ngày kết thúc mới phải sau ngày bắt đầu!");
        }

        hd.setNgayKetThuc(ngayKetThucMoi);
        log.info("Chủ trọ {} gia hạn hợp đồng {} đến ngày {}", currentUser.getUsername(), id, ngayKetThucMoi);
        return hopDongRepository.save(hd);
    }

    /**
     * Khách thuê gửi yêu cầu xin hủy/thanh lý hợp đồng trước hạn.
     * Hợp đồng sẽ được chuyển sang trạng thái chờ duyệt hủy `YEU_CAU_HUY`.
     * Chủ trọ sẽ duyệt yêu cầu này trên Dashboard để chính thức kết thúc hợp đồng.
     * 
     * @param hopDongId ID hợp đồng muốn hủy
     * @param currentUser Tài khoản khách thuê gửi yêu cầu
     */
    @Transactional
    public void huyHopDongBoiKhach(Long hopDongId, TaiKhoan currentUser) {
        HopDong hd = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng!"));

        // Đảm bảo khách thuê chỉ được phép gửi yêu cầu hủy hợp đồng của chính mình
        if (!hd.getKhachHang().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền hủy hợp đồng của người khác!");
        }

        // Chỉ cho phép xin hủy đối với hợp đồng đang có hiệu lực hoạt động
        if (hd.getTrangThai() != TrangThaiHopDong.DA_DUYET) {
            throw new BadRequestException("Chỉ có thể gửi yêu cầu hủy hợp đồng đang có hiệu lực!");
        }

        hd.setTrangThai(TrangThaiHopDong.YEU_CAU_HUY);
        hopDongRepository.save(hd);
        
        log.info("Khách hàng {} đã gửi yêu cầu hủy hợp đồng ID {}. Đang chờ chủ trọ phê duyệt.", 
                currentUser.getUsername(), hopDongId);
    }
}