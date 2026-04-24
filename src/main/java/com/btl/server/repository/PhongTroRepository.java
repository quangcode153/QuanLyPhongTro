package com.btl.server.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.PhongTro;

@Repository
public interface PhongTroRepository extends JpaRepository<PhongTro, Integer> {
    
    List<PhongTro> findByChuTroId(Integer chuTroId);

    List<PhongTro> findByTrangThai(String trangThai);

    List<PhongTro> findByTrangThaiAndGiaPhongLessThanEqual(String trangThai, Double giaToiDa);

    List<PhongTro> findByTenPhongContainingIgnoreCase(String tenPhong);
}