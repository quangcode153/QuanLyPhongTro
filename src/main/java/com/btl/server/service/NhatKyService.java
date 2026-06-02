package com.btl.server.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btl.server.entity.NhatKyHoatDong;
import com.btl.server.repository.NhatKyRepository;

/**
 * Service ghi nhận Nhật ký hoạt động (System Audit Log) của toàn hệ thống.
 * Hỗ trợ lưu trữ thông tin thời gian, người thực hiện, thao tác thực hiện và nội dung chi tiết.
 */
@Service
public class NhatKyService {

    @Autowired
    private NhatKyRepository nhatKyRepository;

    /**
     * Ghi nhận một sự kiện log mới vào CSDL.
     * @param hanhDong Tên hành động hoặc loại sự kiện (ví dụ: XÓA HÓA ĐƠN, THANH TOÁN...)
     * @param chiTiet Mô tả chi tiết hành động
     */
    public void ghiLog(String hanhDong, String chiTiet) {
        NhatKyHoatDong log = new NhatKyHoatDong();
        log.setThoiGian(LocalDateTime.now());
        log.setNguoiThucHien("Admin"); // Mặc định ghi nhận vai trò hệ thống thực hiện
        log.setHanhDong(hanhDong);
        log.setChiTiet(chiTiet);

        nhatKyRepository.save(log);
    }
}