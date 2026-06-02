package com.btl.server.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.btl.server.entity.ThongBao;
import com.btl.server.repository.ThongBaoRepository;

/**
 * REST Controller điều phối các API đăng thông báo tin tức (`/api/thong-bao`).
 * Hỗ trợ chủ trọ đăng bài viết, nhắc nhở đóng tiền phòng và khách thuê xem danh sách thông báo.
 */
@RestController
@RequestMapping("/api/thong-bao")
public class ThongBaoController {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    /**
     * API Chủ trọ đăng tải thông báo mới cho toàn bộ cư dân hoặc phòng cụ thể.
     */
    @PostMapping
    public ResponseEntity<ThongBao> dangThongBao(@RequestBody ThongBao thongBao) {
        thongBao.setNgayDang(LocalDateTime.now());
        
        ThongBao saved = thongBaoRepository.save(thongBao);
        return ResponseEntity.ok(saved);
    }

    /**
     * API Khách thuê hoặc Chủ trọ tra cứu toàn bộ danh sách thông báo của khu trọ theo thứ tự tin mới nhất lên trước.
     * @param chuTroId ID của chủ trọ quản lý khu trọ
     */
    @GetMapping("/chu-tro/{chuTroId}")
    public ResponseEntity<List<ThongBao>> layThongBaoTheoChuTro(@PathVariable Long chuTroId) {
        List<ThongBao> danhSach = thongBaoRepository.findByChuTroIdOrderByNgayDangDesc(chuTroId);
        return ResponseEntity.ok(danhSach);
    }
}