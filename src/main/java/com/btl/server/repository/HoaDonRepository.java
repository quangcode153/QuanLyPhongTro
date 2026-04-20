package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.HoaDon;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    
    boolean existsByPhongTroIdAndThangAndNam(Integer phongId, Integer thang, Integer nam);
}