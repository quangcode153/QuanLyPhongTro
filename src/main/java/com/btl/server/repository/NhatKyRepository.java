package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.NhatKyHoatDong;

/**
 * Repository xử lý các truy vấn CSDL liên quan đến bảng nhật ký audit của hệ thống `nhat_ky_hoat_dong`.
 */
@Repository
public interface NhatKyRepository extends JpaRepository<NhatKyHoatDong, Integer> {
}