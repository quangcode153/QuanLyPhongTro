package com.btl.server.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan; // Đảm bảo import TaiKhoan

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Long> {

    boolean existsByPhongTroAndTrangThai(PhongTro phongTro, String trangThai);

    boolean existsByKhachHangAndPhongTroAndTrangThai(TaiKhoan khachHang, PhongTro phongTro, String trangThai);

    List<HopDong> findByPhongTro_ChuTroId(Long chuTroId);

    List<HopDong> findByKhachHang_Id(Long khachHangId);

    List<HopDong> findByPhongTro_Id(Long phongTroId);

    List<HopDong> findByKhachHang_IdAndTrangThai(Long khachHangId, String trangThai);

    boolean existsByPhongTro_IdAndTrangThaiAndIdNot(Long phongTroId, String trangThai, Integer hopDongIdLoaiTru);

     boolean existsByKhachHang_IdAndPhongTro_ChuTroId(Long khachHangId, Long chuTroId);
}