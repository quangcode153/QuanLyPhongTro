package com.btl.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;

/**
 * Repository xử lý các truy vấn CSDL liên quan đến bảng `hop_dong`.
 * Cung cấp các hàm kiểm tra tồn tại và các lệnh Bulk Update tối ưu hóa hiệu năng cập nhật dữ liệu hàng loạt.
 */
@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Long> {

    /**
     * Kiểm tra xem một phòng trọ cụ thể có đang tồn tại hợp đồng nào ở một trạng thái xác định hay không.
     */
    boolean existsByPhongTroAndTrangThai(PhongTro phongTro, TrangThaiHopDong trangThai);

    /**
     * Kiểm tra xem khách thuê đã có hợp đồng thuê ở phòng trọ xác định với trạng thái cụ thể chưa.
     */
    boolean existsByKhachHangAndPhongTroAndTrangThai(TaiKhoan khachHang, PhongTro phongTro, TrangThaiHopDong trangThai);

    /**
     * Lệnh Bulk Update tự động Từ chối hàng loạt (TU_CHOI) tất cả các yêu cầu thuê phòng đang chờ duyệt khác
     * của cùng một căn phòng trọ đó ngay sau khi Chủ trọ quyết định Duyệt cho một khách thuê cụ thể.
     * Sử dụng `@Modifying` để báo cho Hibernate đồng bộ lại Cache cấp 1.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE HopDong h SET h.trangThai = :trangThaiMoi WHERE h.phongTro.id = :phongTroId AND h.id <> :hopDongIdDuocDuyet AND h.trangThai = :trangThaiCu")
    int tuChoiCacHopDongChoDuyetKhac(
            @Param("phongTroId") Long phongTroId, 
            @Param("hopDongIdDuocDuyet") Long hopDongIdDuocDuyet, 
            @Param("trangThaiCu") TrangThaiHopDong trangThaiCu, 
            @Param("trangThaiMoi") TrangThaiHopDong trangThaiMoi);

    /**
     * Tìm kiếm toàn bộ danh sách hợp đồng thuê phòng thuộc phạm vi sở hữu của một chủ trọ.
     */
    List<HopDong> findByPhongTro_ChuTroId(Long chuTroId);

    /**
     * Tìm kiếm toàn bộ danh sách hợp đồng thuê của một khách thuê nhất định theo ID tài khoản.
     */
    List<HopDong> findByKhachHang_Id(Long khachHangId);

    /**
     * Tìm danh sách hợp đồng gắn liền với một phòng trọ cụ thể.
     */
    List<HopDong> findByPhongTro_Id(Long phongTroId);

    /**
     * Lọc danh sách hợp đồng của khách thuê theo trạng thái xác định.
     */
    List<HopDong> findByKhachHang_IdAndTrangThai(Long khachHangId, TrangThaiHopDong trangThai);

    /**
     * Kiểm tra xem phòng trọ có tồn tại hợp đồng đã duyệt nào khác ngoài hợp đồng hiện tại đang loại trừ hay không.
     */
    boolean existsByPhongTro_IdAndTrangThaiAndIdNot(Long phongTroId, TrangThaiHopDong trangThai, Long hopDongIdLoaiTru);

    /**
     * Lệnh Bulk Update kết thúc toàn bộ hợp đồng đang được duyệt của phòng trọ khi phòng được đưa về trạng thái Trống.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE HopDong h SET h.trangThai = :ketThuc WHERE h.phongTro.id = :phongId AND h.trangThai = :daDuyet")
    int ketThucHopDongTheoPhong(@Param("phongId") Long phongId, 
                                @Param("daDuyet") TrangThaiHopDong daDuyet, 
                                @Param("ketThuc") TrangThaiHopDong ketThuc);
    
    /**
     * Ràng buộc an toàn: Kiểm tra mối quan hệ khách thuê này có đang thuê phòng thuộc chủ trọ này quản lý không (dùng khi check quyền xem hồ sơ).
     */
    boolean existsByKhachHang_IdAndPhongTro_ChuTroId(Long khachHangId, Long chuTroId);

    /**
     * Xóa sạch toàn bộ hợp đồng liên kết với phòng trọ khi thực hiện xóa phòng trọ đó.
     */
    @Modifying
    void deleteByPhongTro_Id(Long phongId);
}