package com.btl.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.PhongTroService;

/**
 * Component bảo mật tùy chỉnh (`phongTroSecurity`) dùng trong cấu hình @PreAuthorize của Controller.
 * Giúp kiểm tra trực tiếp xem tài khoản đăng nhập hiện tại có đúng là Chủ sở hữu của căn phòng trọ hay không.
 */
@Component("phongTroSecurity")
public class PhongTroSecurityService {

    @Autowired
    private PhongTroService phongTroService;
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    /**
     * Hàm kiểm tra quyền sở hữu phòng trọ.
     * @param phongId ID phòng trọ cần thao tác
     * @param username Tên đăng nhập của tài khoản yêu cầu
     * @return true nếu đúng là chủ sở hữu phòng trọ, ngược lại false
     */
    public boolean isOwner(Long phongId, String username) {
        PhongTro phong = phongTroService.getPhongById(phongId);
        TaiKhoan user = taiKhoanRepository.findByUsername(username).orElse(null);
        
        if (phong == null || user == null) return false;
        // So sánh ID chủ trọ của phòng với ID của tài khoản đang đăng nhập
        return phong.getChuTroId().equals(user.getId());
    }
}