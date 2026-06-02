package com.btl.server.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.entity.HopDong;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.service.PhongTroService;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.repository.HopDongRepository;

/**
 * REST Controller tiếp nhận toàn bộ API endpoints xử lý liên quan đến Phòng Trọ (`/api/phong-tro`).
 * Hỗ trợ các vai trò: Khách vãng lai, Khách thuê, Chủ trọ và Admin quản trị.
 */
@RestController
@RequestMapping("/api/phong-tro")
public class PhongTroController {

    private static final Logger log = LoggerFactory.getLogger(PhongTroController.class);

    private final PhongTroService phongTroService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final HopDongRepository hopDongRepository;

    public PhongTroController(PhongTroService phongTroService, 
                              TaiKhoanRepository taiKhoanRepository, 
                              HopDongRepository hopDongRepository) {
        this.phongTroService = phongTroService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.hopDongRepository = hopDongRepository;
    }

    /**
     * API lấy danh sách các phòng trọ.
     * Phân quyền nghiệp vụ:
     * - Nếu là Khách vãng lai (chưa đăng nhập): Lấy toàn bộ danh sách phòng để xem.
     * - Nếu là Chủ trọ (đã đăng nhập vai trò LANDLORD): Chỉ lọc lấy danh sách phòng thuộc quyền quản lý của riêng họ.
     * - Nếu là Admin hoặc Khách thuê: Trả về toàn bộ danh sách phòng trọ trong hệ thống.
     * 
     * @return Danh sách PhongTro dạng JSON
     */
    @GetMapping
    public ResponseEntity<List<PhongTro>> layDanhSachPhong(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(phongTroService.getAllPhongs());
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByUsername(principal.getName().toLowerCase()).orElse(null);
        
        // Nếu đăng nhập là Chủ trọ, trả về danh sách phòng riêng của họ
        if (taiKhoan != null && "ROLE_LANDLORD".equals(taiKhoan.getRole())) {
            return ResponseEntity.ok(phongTroService.getPhongByChuTroId(taiKhoan.getId()));
        }
        
        return ResponseEntity.ok(phongTroService.getAllPhongs());
    }

    /**
     * API Thêm mới một phòng trọ (Chỉ dành cho ADMIN hoặc LANDLORD).
     * @param phongTro Thực thể PhongTro nhận từ RequestBody
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<?> themPhongMoi(@Valid @RequestBody PhongTro phongTro, Principal principal) {
        if (principal == null) {
            throw new ForbiddenException("Bạn cần đăng nhập để thực hiện thao tác này!");
        }

        TaiKhoan chuTro = taiKhoanRepository.findByUsername(principal.getName().toLowerCase())
                .orElseThrow(() -> new ForbiddenException("Thông tin chủ trọ không hợp lệ!"));

        // Gán cứng ID chủ trọ cho phòng để tránh giả mạo
        phongTro.setChuTroId(chuTro.getId());
        
        // Phòng mới tạo mặc định ở trạng thái TRỐNG (TRONG)
        phongTro.setTrangThai(TrangThaiPhong.TRONG); 

        log.info("User {} đã thêm phòng trọ mới", chuTro.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(phongTroService.savePhong(phongTro));
    }

    /**
     * API Cập nhật thông tin phòng trọ.
     * Bảo mật: Sử dụng custom expression SpEL `@phongTroSecurity.isOwner(#id, authentication.name)`
     * để kiểm tra xem chủ trọ đăng nhập có thực sự là chủ sở hữu của căn phòng trọ này hay không trước khi sửa.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> capNhatPhong(@PathVariable Long id, @Valid @RequestBody PhongTro phongTroMoi) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        
        if (existingPhong == null) {
            throw new NotFoundException("Không tìm thấy phòng trọ!");
        }

        phongTroMoi.setId(id);
        phongTroMoi.setChuTroId(existingPhong.getChuTroId());
        
        log.info("Cập nhật thông tin phòng ID: {}", id);
        return ResponseEntity.ok(phongTroService.savePhong(phongTroMoi));
    }

    /**
     * API Cập nhật riêng trạng thái hoạt động của phòng trọ (TRỐNG, ĐÃ THUÊ, ĐANG SỬA).
     * Bảo mật: Chỉ Admin hoặc Chủ sở hữu phòng mới được phép gọi API này.
     * Nghiệp vụ tự động: Nếu phòng chuyển sang TRỐNG, tự động quyết toán chuyển tất cả các hợp đồng
     * đang thuê của phòng này sang trạng thái ĐÃ KẾT THÚC.
     */
    @PutMapping("/{id}/trang-thai")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> capNhatTrangThaiPhong(@PathVariable Long id, @RequestParam String trangThai) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        if (existingPhong == null) {
            throw new NotFoundException("Không tìm thấy phòng trọ!");
        }

        TrangThaiPhong trangThaiEnum;
        try {
            trangThaiEnum = TrangThaiPhong.valueOf(trangThai.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái phòng không hợp lệ!");
        }
        existingPhong.setTrangThai(trangThaiEnum);
        phongTroService.savePhong(existingPhong);

        // Nghiệp vụ đồng bộ trạng thái:
        if (trangThaiEnum == TrangThaiPhong.TRONG) {
            List<HopDong> hopDongs = hopDongRepository.findByPhongTro_Id(id);
            int count = 0;
            for (HopDong hd : hopDongs) {
                if (hd.getTrangThai() == TrangThaiHopDong.DA_DUYET) {
                    hd.setTrangThai(TrangThaiHopDong.DA_KET_THUC);
                    hopDongRepository.save(hd);
                    count++;
                }
            }
            if (count > 0) {
                log.info("Phòng {} chuyển trạng thái Trống. Đã thanh lý tự động {} hợp đồng.", id, count);
            }
        }
        
        return ResponseEntity.ok(existingPhong);
    }

    /**
     * API Xóa phòng trọ ra khỏi CSDL.
     * Bảo mật: Chỉ Admin hoặc Chủ sở hữu phòng mới được phép gọi.
     * Kế thừa logic kiểm tra an toàn trong `PhongTroService.deletePhong(id)`.
     */
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @phongTroSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> xoaPhong(@PathVariable Long id) {
        PhongTro existingPhong = phongTroService.getPhongById(id);
        if (existingPhong == null) {
            throw new NotFoundException("Không tìm thấy phòng trọ!");
        }

        phongTroService.deletePhong(id);
        log.info("Đã xóa phòng ID: {}", id);
        
        return ResponseEntity.ok(Map.of("message", "Đã xóa thành công phòng và các hợp đồng liên quan!"));
    }

    /**
     * API Tìm kiếm phòng trọ theo trạng thái cụ thể.
     */
    @GetMapping("/tim-kiem")
    public ResponseEntity<List<PhongTro>> timPhong(@RequestParam String trangThai) {
        try {
            TrangThaiPhong trangThaiEnum = TrangThaiPhong.valueOf(trangThai.toUpperCase());
            return ResponseEntity.ok(phongTroService.timPhongTheoTrangThai(trangThaiEnum));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái phòng không hợp lệ!");
        }
    }

    /**
     * API Lọc danh sách phòng theo trạng thái và giới hạn giá thuê tối đa.
     */
    @GetMapping("/loc-phong")
    public ResponseEntity<List<PhongTro>> locPhongTheoGia(
            @RequestParam String trangThai, @RequestParam BigDecimal giaToiDa) {
        try {
            TrangThaiPhong trangThaiEnum = TrangThaiPhong.valueOf(trangThai.toUpperCase());
            return ResponseEntity.ok(phongTroService.locPhongTheoGia(trangThaiEnum, giaToiDa));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái phòng không hợp lệ!");
        }
    }

    /**
     * API Tìm kiếm phòng trọ đa tiêu chí kết hợp (Tên phòng, Địa chỉ, Khoảng giá, Trạng thái).
     * Thường dùng cho bộ lọc tìm kiếm nâng cao ở trang chủ phía Frontend.
     */
    @GetMapping("/search")
    public ResponseEntity<List<PhongTro>> searchPhong(
            @RequestParam(required = false) String tenPhong,
            @RequestParam(required = false) String diaChi,
            @RequestParam(required = false) BigDecimal giaToiThieu,
            @RequestParam(required = false) BigDecimal giaToiDa,
            @RequestParam(required = false) String trangThai) {
        TrangThaiPhong trangThaiEnum = null;
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            try {
                trangThaiEnum = TrangThaiPhong.valueOf(trangThai.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Trạng thái phòng không hợp lệ!");
            }
        }
        return ResponseEntity.ok(phongTroService.searchPhongTro(tenPhong, diaChi, giaToiThieu, giaToiDa, trangThaiEnum));
    }

    /**
     * API Lấy toàn bộ danh sách phòng thuộc quản lý của một chủ trọ dựa theo ID.
     */
    @GetMapping("/chu-tro/{chuTroId}")
    public ResponseEntity<List<PhongTro>> layPhongTheoChuTro(@PathVariable Long chuTroId) {
        return ResponseEntity.ok(phongTroService.getPhongByChuTroId(chuTroId));
    }
}