package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.KhieuNai;
import java.util.List;

// Repository
@Repository
public interface KhieuNaiRepository extends JpaRepository<KhieuNai, Long> {
    // Lấy danh sách toàn bộ khiếu nại phản ánh trong hệ thống xếp giảm dần theo thời gian gửi (mới nhất lên đầu).
    List<KhieuNai> findAllByOrderByThoiGianGuiDesc();
    
    // Tìm danh sách phản ánh khiếu nại theo trạng thái xử lý (ví dụ: chỉ lấy các sự cố chưa xử lý CHO_XU_LY).
    List<KhieuNai> findByTrangThaiOrderByThoiGianGuiDesc(String trangThai);
}