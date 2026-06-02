package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.HoaDon;
import java.util.List;

/**
 * Repository xử lý các truy vấn CSDL liên quan đến bảng `hoa_don`.
 * Cung cấp các câu lệnh JPQL tính toán doanh thu, đếm hóa đơn chưa thanh toán, nợ đọng cho từng Chủ trọ.
 */
@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    
    /**
     * Kiểm tra xem phòng trọ đã được xuất hóa đơn trong chu kỳ tháng/năm xác định chưa.
     */
    boolean existsByPhongTroIdAndThangAndNam(Long phongId, Integer thang, Integer nam);

    /**
     * Truy vấn lấy danh sách hóa đơn thuộc về một Khách thuê cụ thể.
     * Liên kết hóa đơn với hợp đồng đang có hiệu lực (DA_DUYET) của khách để lọc chính xác.
     */
    @Query("SELECT hd FROM HoaDon hd JOIN HopDong h ON hd.phongTro.id = h.phongTro.id WHERE h.khachHang.id = :khachHangId AND h.trangThai = 'DA_DUYET'")
    List<HoaDon> findHoaDonByKhachHangId(@Param("khachHangId") Long khachHangId);

    /**
     * Tìm danh sách tất cả các hóa đơn của các phòng thuộc quyền quản lý của một chủ trọ.
     */
    List<HoaDon> findByPhongTroChuTroId(Long chuTroId);

    /**
     * Tính tổng doanh thu thực tế đã thu được (DA_THANH_TOAN) của chủ trọ trong một chu kỳ tháng/năm xác định.
     */
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.phongTro.chuTroId = :chuTroId AND h.thang = :thang AND h.nam = :nam AND h.trangThai = 'DA_THANH_TOAN'")
    java.math.BigDecimal sumDoanhThuByChuTroAndThangNam(@Param("chuTroId") Long chuTroId, @Param("thang") Integer thang, @Param("nam") Integer nam);

    /**
     * Đếm số lượng hóa đơn chưa thanh toán (CHUA_THANH_TOAN) thuộc khu trọ của chủ trọ.
     */
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.phongTro.chuTroId = :chuTroId AND h.trangThai = 'CHUA_THANH_TOAN'")
    Integer countHoaDonChuaThanhToan(@Param("chuTroId") Long chuTroId);

    /**
     * Tính tổng số tiền nợ đọng chưa thu được (CHUA_THANH_TOAN) thuộc khu trọ của chủ trọ.
     */
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.phongTro.chuTroId = :chuTroId AND h.trangThai = 'CHUA_THANH_TOAN'")
    java.math.BigDecimal sumTienChuaThanhToan(@Param("chuTroId") Long chuTroId);

    /**
     * Xóa sạch toàn bộ hóa đơn liên kết với một phòng trọ khi thực hiện xóa phòng trọ đó.
     */
    @Modifying
    void deleteByPhongTro_Id(Long phongId);
}