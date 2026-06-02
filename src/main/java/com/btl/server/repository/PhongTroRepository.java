package com.btl.server.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiPhong;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository xử lý các truy vấn CSDL liên quan đến bảng `phong_tro`.
 * Hỗ trợ tìm kiếm, lọc theo giá, theo chủ trọ, và tìm kiếm đa tiêu chí nâng cao.
 */
@Repository
public interface PhongTroRepository extends JpaRepository<PhongTro, Long> {
    
    /**
     * Tìm danh sách các phòng trọ được sở hữu bởi một Chủ trọ dựa trên ID tài khoản chủ trọ.
     */
    List<PhongTro> findByChuTroId(Long chuTroId);

    /**
     * Tìm danh sách các phòng trọ theo trạng thái hoạt động (TRONG, DA_THUE, DANG_SUA).
     */
    List<PhongTro> findByTrangThai(TrangThaiPhong trangThai);

    /**
     * Lọc danh sách phòng theo trạng thái hoạt động và giá phòng nhỏ hơn hoặc bằng mức giá tối đa.
     */
    List<PhongTro> findByTrangThaiAndGiaPhongLessThanEqual(TrangThaiPhong trangThai, BigDecimal giaToiDa);

    /**
     * Tìm kiếm phòng trọ theo tên phòng (không phân biệt chữ hoa chữ thường).
     */
    List<PhongTro> findByTenPhongContainingIgnoreCase(String tenPhong);

    /**
     * Truy vấn tìm kiếm đa tiêu chí nâng cao (Dynamic Advanced Search Query).
     * Cho phép lọc linh hoạt theo tên phòng, địa chỉ khu trọ, khoảng giá thuê (từ - đến) và trạng thái phòng.
     * Sử dụng câu lệnh JPQL kết hợp toán tử điều kiện `IS NULL` để nạp động các tham số truyền vào.
     */
    @Query("SELECT p FROM PhongTro p WHERE " +
           "(:tenPhong IS NULL OR LOWER(p.tenPhong) LIKE LOWER(CONCAT('%', :tenPhong, '%'))) AND " +
           "(:diaChi IS NULL OR LOWER(p.diaChi) LIKE LOWER(CONCAT('%', :diaChi, '%'))) AND " +
           "(:giaToiThieu IS NULL OR p.giaPhong >= :giaToiThieu) AND " +
           "(:giaToiDa IS NULL OR p.giaPhong <= :giaToiDa) AND " +
           "(:trangThai IS NULL OR p.trangThai = :trangThai)")
    List<PhongTro> searchPhongTro(@Param("tenPhong") String tenPhong,
                                  @Param("diaChi") String diaChi,
                                  @Param("giaToiThieu") BigDecimal giaToiThieu,
                                  @Param("giaToiDa") BigDecimal giaToiDa,
                                  @Param("trangThai") TrangThaiPhong trangThai);
}