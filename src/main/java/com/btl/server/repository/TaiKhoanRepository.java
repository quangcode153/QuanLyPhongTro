package com.btl.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.TaiKhoan;
import java.util.List;
import java.util.Optional;

/**
 * Repository xử lý các truy vấn CSDL liên quan đến bảng `tai_khoan`.
 * Kế thừa JpaRepository để cung cấp đầy đủ các phương thức CRUD cơ bản.
 */
@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {

    /**
     * Tìm kiếm tài khoản dựa vào tên đăng nhập (Username).
     */
    Optional<TaiKhoan> findByUsername(String username);

    /**
     * Tìm tài khoản đầu tiên khớp với một vai trò (Role) xác định (thường dùng để
     * tìm tài khoản Admin).
     */
    Optional<TaiKhoan> findFirstByRole(String role);

    /**
     * Tìm danh sách tất cả tài khoản thuộc một vai trò nhất định.
     */
    List<TaiKhoan> findByRole(String role);

    /**
     * Lấy danh sách tất cả các tài khoản hỗ trợ Phân trang dữ liệu.
     */
    Page<TaiKhoan> findAll(Pageable pageable);

    /**
     * Lấy danh sách hình chiếu thu gọn (Projection) của các tài khoản có vai trò
     * Chủ trọ (ROLE_LANDLORD).
     * Sử dụng câu lệnh JPQL JOIN để tối ưu hiệu năng, giảm dung lượng RAM nạp đối
     * tượng dư thừa.
     */
    @Query("SELECT t.id AS id, t.username AS username, t.locked AS locked, k.hoTen AS hoTen " +
            "FROM TaiKhoan t LEFT JOIN t.khachHang k WHERE t.role = 'ROLE_LANDLORD'")
    List<ChuTroProjection> findChuTroProjections();

    /**
     * Interface Projection định nghĩa cấu trúc dữ liệu thu gọn của Chủ trọ để trả
     * về phía Client.
     */
    public interface ChuTroProjection {
        /**
         * Lấy ID tài khoản của chủ trọ.
         */
        Long getId();

        /**
         * Lấy tên đăng nhập của chủ trọ.
         */
        String getUsername();

        /**
         * Lấy trạng thái khóa của tài khoản chủ trọ.
         */
        Boolean getLocked();

        /**
         * Lấy họ tên thực của chủ trọ.
         */
        String getHoTen();
    }
}