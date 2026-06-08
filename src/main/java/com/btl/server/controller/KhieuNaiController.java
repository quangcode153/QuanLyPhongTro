package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;

import com.btl.server.entity.KhieuNai;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.KhieuNaiRepository;
import com.btl.server.repository.TaiKhoanRepository;

// REST Controller tiếp nhận toàn bộ các API xử lý sự cố khiếu nại (`/api/khieu-nai`). Khách thuê có thể gửi phản ánh lỗi phòng ốc, dịch vụ, và Admin thực hiện đánh dấu xử lý hoàn tất.
@RestController
@RequestMapping("/api/khieu-nai")
public class KhieuNaiController {
    @Autowired
    private KhieuNaiRepository khieuNaiRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // API Admin lấy toàn bộ danh sách khiếu nại trong hệ thống theo thứ tự mới gửi trước.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KhieuNai>> xemDanhSach() {
        return ResponseEntity.ok(khieuNaiRepository.findAllByOrderByThoiGianGuiDesc());
    }

    // API Thành viên (Khách thuê hoặc Chủ trọ) gửi khiếu nại phản hồi lỗi dịch vụ hoặc cơ sở vật chất. Mặc định tạo sự cố ở trạng thái chờ xử lý (CHO_XU_LY).
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('LANDLORD')")
    public ResponseEntity<?> guiKhieuNai(@RequestBody KhieuNaiRequest request, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để gửi khiếu nại!");
        }

        if (request.getTieuDe() == null || request.getTieuDe().trim().isEmpty() ||
            request.getNoiDung() == null || request.getNoiDung().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tiêu đề và nội dung không được để trống!");
        }

        TaiKhoan nguoiGui = taiKhoanRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tài khoản"));

        KhieuNai khieuNai = new KhieuNai();
        khieuNai.setTieuDe(request.getTieuDe());
        khieuNai.setNoiDung(request.getNoiDung());
        khieuNai.setNguoiGui(nguoiGui);
        khieuNai.setTrangThai(KhieuNai.TrangThaiKhieuNai.CHO_XU_LY);

        return ResponseEntity.ok(khieuNaiRepository.save(khieuNai));
    }

    // API Admin xác nhận đã khắc phục và đóng sự cố khiếu nại (DA_GIAI_QUYET).
    @PutMapping("/{id}/xu-ly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> danhDauDaXuLy(@PathVariable Long id) {
        KhieuNai khieuNai = khieuNaiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khiếu nại ID: " + id));

        khieuNai.setTrangThai(KhieuNai.TrangThaiKhieuNai.DA_GIAI_QUYET);

        return ResponseEntity.ok(khieuNaiRepository.save(khieuNai));
    }

    // Lớp Request truyền tải dữ liệu khiếu nại thô gửi lên từ Client.
    public static class KhieuNaiRequest {
        private String tieuDe;
        private String noiDung;

        // Lấy tiêu đề của khiếu nại.
        public String getTieuDe() { return tieuDe; }

        // Cập nhật tiêu đề của khiếu nại.
        public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

        // Lấy nội dung chi tiết của khiếu nại.
        public String getNoiDung() { return noiDung; }

        // Cập nhật nội dung chi tiết của khiếu nại.
        public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    }
}