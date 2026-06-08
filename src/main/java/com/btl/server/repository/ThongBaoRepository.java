package com.btl.server.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.ThongBao;

// Repository
@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    // Lấy danh sách thông báo tin tức thuộc khu trọ của một chủ trọ quản lý xếp theo ngày đăng giảm dần (mới nhất lên đầu).
    List<ThongBao> findByChuTroIdOrderByNgayDangDesc(Long chuTroId);
}