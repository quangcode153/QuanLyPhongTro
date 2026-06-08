package com.btl.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.btl.server.entity.ChiSoDienNuoc;
import java.util.List;
import java.util.Optional;

// Repository
@Repository
public interface ChiSoDienNuocRepository extends JpaRepository<ChiSoDienNuoc, Long> {
    // Tìm bản ghi chốt số điện nước của phòng trọ trong chu kỳ tháng/nam cụ thể.
    Optional<ChiSoDienNuoc> findByPhongTroIdAndThangAndNam(Long phongId, Integer thang, Integer nam);
    
    // Lấy toàn bộ lịch sử chốt số điện nước của phòng trọ sắp xếp giảm dần theo thời gian (mới nhất lên đầu).
    List<ChiSoDienNuoc> findByPhongTroIdOrderByNamDescThangDesc(Long phongId);

    // Xóa sạch toàn bộ chỉ số điện nước liên kết với phòng trọ khi thực hiện xóa phòng trọ đó.
    @Modifying
    void deleteByPhongTro_Id(Long phongId);
}