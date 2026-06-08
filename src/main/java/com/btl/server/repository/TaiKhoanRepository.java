package com.btl.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.TaiKhoan;
import java.util.List;
import java.util.Optional;

// Repository
@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
    // Tìm tài khoản theo username
    Optional<TaiKhoan> findByUsername(String username);

    // Tìm tài khoản theo role
    Optional<TaiKhoan> findFirstByRole(String role);

    // Tìm danh sách tài khoản theo role
    List<TaiKhoan> findByRole(String role);

    // Lấy tài khoản kèm phân trang
    Page<TaiKhoan> findAll(Pageable pageable);

    // Lấy danh sách chủ trọ
    @Query("SELECT t.id AS id, t.username AS username, t.locked AS locked, k.hoTen AS hoTen " +
            "FROM TaiKhoan t LEFT JOIN t.khachHang k WHERE t.role = 'ROLE_LANDLORD'")
    List<ChuTroProjection> findChuTroProjections();

    // Projection chủ trọ
    public interface ChuTroProjection {
        Long getId();

        String getUsername();

        Boolean getLocked();

        String getHoTen();
    }
}