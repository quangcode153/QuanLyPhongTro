package com.btl.server.repository;

import com.btl.server.entity.HopDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Integer> {
    List<HopDong> findByPhongTro_ChuTroId(Integer chuTroId);
    List<HopDong> findByKhachHang_Id(Integer khachId);
    List<HopDong> findByPhongTro_Id(Integer phongId);
}