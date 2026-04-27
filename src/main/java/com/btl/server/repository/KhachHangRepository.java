package com.btl.server.repository;

import java.util.Optional;
import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
   
    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);
    Optional<KhachHang> findBySoCccd(String soCccd);
}