package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.HoaDon;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    
    boolean existsByPhongTroIdAndThangAndNam(Long phongId, Integer thang, Integer nam);

    @Query("SELECT hd FROM HoaDon hd JOIN HopDong h ON hd.phongTro.id = h.phongTro.id WHERE h.khachHang.id = :khachHangId AND h.trangThai = 'DA_DUYET'")
    List<HoaDon> findHoaDonByKhachHangId(@Param("khachHangId") Long khachHangId);
}