package com.btl.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

import com.btl.server.entity.KhieuNai;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.KhieuNaiRepository;
import com.btl.server.repository.TaiKhoanRepository;

@RestController
@RequestMapping("/api/khieu-nai")
public class KhieuNaiController {

    @Autowired
    private KhieuNaiRepository khieuNaiRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KhieuNai>> xemDanhSach() {
        return ResponseEntity.ok(khieuNaiRepository.findAllByOrderByThoiGianGuiDesc());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('LANDLORD')")
    public ResponseEntity<?> guiKhieuNai(@RequestBody KhieuNaiRequest request, Principal principal) {
        
        TaiKhoan nguoiGui = taiKhoanRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không xác định được danh tính"));

        KhieuNai khieuNai = new KhieuNai();
        khieuNai.setTieuDe(request.getTieuDe());
        khieuNai.setNoiDung(request.getNoiDung());
        khieuNai.setNguoiGui(nguoiGui);
        khieuNai.setTrangThai(KhieuNai.TrangThaiKhieuNai.CHO_XU_LY);

        return ResponseEntity.ok(khieuNaiRepository.save(khieuNai));
    }

    public static class KhieuNaiRequest {
        private Integer nguoiGuiId;
        private String tieuDe;
        private String noiDung;

        public Integer getNguoiGuiId() { return nguoiGuiId; }
        public void setNguoiGuiId(Integer nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }
        public String getTieuDe() { return tieuDe; }
        public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
        public String getNoiDung() { return noiDung; }
        public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    }
}