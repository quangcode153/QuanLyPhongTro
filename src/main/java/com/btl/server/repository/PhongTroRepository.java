package com.btl.server.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiPhong;

import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiPhong;

@Repository
public interface PhongTroRepository extends JpaRepository<PhongTro, Long> {
    
    List<PhongTro> findByChuTroId(Long chuTroId);

    List<PhongTro> findByTrangThai(TrangThaiPhong trangThai);

    List<PhongTro> findByTrangThaiAndGiaPhongLessThanEqual(TrangThaiPhong trangThai, BigDecimal giaToiDa);

    List<PhongTro> findByTenPhongContainingIgnoreCase(String tenPhong);
}