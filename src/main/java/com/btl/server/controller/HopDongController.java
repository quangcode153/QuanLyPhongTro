package com.btl.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.btl.server.dto.HopDongRequestDTO;
import com.btl.server.entity.HopDong;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.HopDongService;

import jakarta.validation.Valid;

/**
 * REST Controller điều phối các API quản lý Hợp Đồng thuê phòng (`/api/hop-dong`).
 * Cung cấp các thao tác: Khách gửi yêu cầu thuê phòng, Chủ trọ phê duyệt/từ chối/gia hạn/thanh lý hợp đồng,
 * và khách thuê chủ động xin hủy hợp đồng.
 */
@RestController
@RequestMapping("/api/hop-dong")
public class HopDongController {

    private static final Logger log = LoggerFactory.getLogger(HopDongController.class);

    private final HopDongService hopDongService;
    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Khởi tạo HopDongController với HopDongService và TaiKhoanRepository.
     */
    public HopDongController(HopDongService hopDongService, TaiKhoanRepository taiKhoanRepository) {
        this.hopDongService = hopDongService;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    /**
     * API Admin lấy toàn bộ danh sách hợp đồng thuê phòng của cả hệ thống.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HopDong>> xemDanhSachHopDong() {
        return ResponseEntity.ok(hopDongService.layTatCaHopDong());
    }

    /**
     * API Khách thuê (vai trò ROLE_USER) tạo yêu cầu thuê phòng trọ mới.
     * Mặc định tạo hợp đồng ở trạng thái chờ duyệt (CHO_DUYET).
     * 
     * @param request DDTO chứa id phòng, ngày bắt đầu, ngày kết thúc, tiền cọc
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> kyHopDongMoi(@Valid @RequestBody HopDongRequestDTO request, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Xác thực thất bại, user không tồn tại!"));

        hopDongService.taoHopDong(request, user);

        return ResponseEntity.ok(Map.of("message", "Đã tạo yêu cầu thuê phòng thành công!"));
    }

    /**
     * API Chủ trọ xem toàn bộ các hợp đồng thuộc phạm vi quản lý của mình.
     * Bảo mật: Chặn không cho chủ trọ này cố tình xem hợp đồng của chủ trọ khác.
     * 
     * @param chuTroId ID chủ trọ cần xem danh sách hợp đồng
     */
    @GetMapping("/chu-tro/{chuTroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<List<HopDong>> layHopDongCuaChuTro(@PathVariable Long chuTroId, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        // Bảo mật: Chủ trọ chỉ được xem dữ liệu của chính mình
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            if (!"ROLE_LANDLORD".equals(user.getRole()) || !user.getId().equals(chuTroId)) {
                throw new ForbiddenException("Không được phép xem dữ liệu của chủ trọ khác!");
            }
        }

        return ResponseEntity.ok(hopDongService.layHopDongTheoChuTro(chuTroId));
    }

    /**
     * API Khách thuê lấy danh sách hợp đồng của riêng họ.
     * Hỗ trợ bộ lọc theo trạng thái hợp đồng (ví dụ: chỉ lấy các hợp đồng đã duyệt DA_DUYET).
     */
    @GetMapping("/khach/{khachId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<HopDong>> layHopDongCuaKhach(
            @PathVariable Long khachId,
            @RequestParam(required = false, defaultValue = "ALL") String trangThai,
            Principal principal) {

        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        // Bảo mật: Khách chỉ được xem danh sách của chính mình
        if (!user.getId().equals(khachId)) {
            throw new ForbiddenException("Không được phép xem dữ liệu của người khác!");
        }

        if ("ALL".equalsIgnoreCase(trangThai)) {
            return ResponseEntity.ok(hopDongService.layHopDongTheoKhach(khachId));
        }

        try {
            TrangThaiHopDong enumTrangThai = TrangThaiHopDong.valueOf(trangThai.toUpperCase());
            return ResponseEntity.ok(hopDongService.layHopDongTheoKhachVaTrangThai(khachId, enumTrangThai));
        } catch (IllegalArgumentException e) {
            throw new com.btl.server.exception.BadRequestException("Trạng thái hợp đồng không hợp lệ!");
        }
    }

    /**
     * API Chủ trọ (hoặc Admin) cập nhật trạng thái hợp đồng thuê phòng.
     * Quy tắc nghiệp vụ (Business Rules):
     * - Duyệt hợp đồng (DA_DUYET): Chuyển phòng sang DA_THUE, bulk update tự động từ chối các yêu cầu thuê khác.
     * - Từ chối hợp đồng (TU_CHOI): Giữ nguyên trạng thái phòng trống.
     * 
     * @param id ID của hợp đồng cần cập nhật
     * @param trangThai Trạng thái muốn chuyển sang
     * @param ngayKetThuc Cung cấp ngày kết thúc tùy chọn nếu có điều chỉnh
     */
    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> capNhatTrangThai(
            @PathVariable Long id,
            @RequestParam String trangThai,
            @RequestParam(required = false) String ngayKetThuc,
            Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        try {
            TrangThaiHopDong trangThaiMoi = TrangThaiHopDong.valueOf(trangThai.toUpperCase());
            java.time.LocalDate date = null;
            if (ngayKetThuc != null && !ngayKetThuc.isEmpty()) {
                date = java.time.LocalDate.parse(ngayKetThuc);
            }
            hopDongService.capNhatTrangThaiHopDong(id, trangThaiMoi, date, user);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
        } catch (IllegalArgumentException e) {
            throw new com.btl.server.exception.BadRequestException("Trạng thái chuyển đổi không hợp lệ!");
        }
    }

    /**
     * API Chủ trọ cập nhật gia hạn ngày kết thúc của hợp đồng.
     */
    @PutMapping("/{id}/gia-han")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> capNhatGiaHan(@PathVariable Long id, @RequestParam String ngayKetThucMoi,
            Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        java.time.LocalDate date = java.time.LocalDate.parse(ngayKetThucMoi);
        hopDongService.giaHanHopDong(id, date, user);
        return ResponseEntity.ok(Map.of("message", "Gia hạn hợp đồng thành công!"));
    }

    /**
     * API Chủ trọ thanh lý hợp đồng thuê (DA_THANH_LY).
     * Nghiệp vụ: Chốt trả lại tiền cọc (sau khi trừ các hư hỏng tài sản nếu có), đưa phòng trọ về trạng thái trống.
     */
    @PutMapping("/{id}/thanh-ly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> thanhLyHopDong(@PathVariable Long id, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        hopDongService.capNhatTrangThaiHopDong(id, TrangThaiHopDong.DA_THANH_LY, null, user);
        return ResponseEntity.ok(Map.of("message", "Đã thanh lý hợp đồng thành công!"));
    }

    /**
     * API Khách thuê gửi yêu cầu đơn phương hủy hợp đồng thuê trước thời hạn (HUY).
     * Nghiệp vụ: Khách chấp nhận chịu mất cọc hoặc bồi thường theo đúng thỏa thuận ban đầu.
     */
    @PutMapping("/{id}/khach-huy")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> khachHuyHopDong(@PathVariable Long id, Principal principal) {
        TaiKhoan user = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User không tồn tại!"));

        hopDongService.huyHopDongBoiKhach(id, user);
        return ResponseEntity.ok(Map.of("message", "Đã hủy hợp đồng thành công! Tiền cọc sẽ bị khấu trừ theo quy định."));
    }
}