package com.btl.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.dto.KhachHangDTO;
import com.btl.server.dto.CapNhatHoSoDTO;
import com.btl.server.dto.HoSoResponseDTO;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.KhachHangRepository;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.repository.HopDongRepository;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final HopDongRepository hopDongRepository;

    public KhachHangService(KhachHangRepository khachHangRepository,
                            TaiKhoanRepository taiKhoanRepository,
                            HopDongRepository hopDongRepository) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.hopDongRepository = hopDongRepository;
    }

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public KhachHang saveKhach(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public List<KhachHangDTO> getKhachHangAnToan() {
        return khachHangRepository.findAll().stream().map(kh -> {
            KhachHangDTO dto = new KhachHangDTO();
            dto.setHoTen(kh.getHoTen());
            dto.setNgaySinh(kh.getNgaySinh());
            dto.setGioiTinh(kh.getGioiTinh());
            dto.setSoCccd(kh.getSoCccd());
            dto.setSoDienThoai(kh.getSoDienThoai());
            dto.setEmail(kh.getEmail());
            dto.setDiaChiThuongTru(kh.getDiaChiThuongTru());
            dto.setTenPhongDangThue("Xem chi tiết trong Hợp Đồng");
            return dto;
        }).collect(Collectors.toList());
    }

    public HoSoResponseDTO layHoSoCaNhan(String username) {
        TaiKhoan user = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));

        KhachHang hoSo = khachHangRepository.findByTaiKhoan(user)
                .orElseThrow(() -> new NotFoundException("Chưa có hồ sơ"));

        return new HoSoResponseDTO(hoSo);
    }

    public HoSoResponseDTO layHoSoTheoId(Long id, String currentUsername) {
        TaiKhoan currentUser = taiKhoanRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng hiện tại!"));

         TaiKhoan targetUser = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản đích!"));

        KhachHang hoSo = khachHangRepository.findByTaiKhoan(targetUser)
                .orElseThrow(() -> new NotFoundException("Khách hàng này chưa cập nhật hồ sơ!"));

        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            boolean isMyTenant = hopDongRepository.existsByKhachHang_IdAndPhongTro_ChuTroId(hoSo.getId(), currentUser.getId());
            if (!isMyTenant) {
                throw new ForbiddenException("Cảnh báo bảo mật: Bạn không có quyền xem hồ sơ của khách hàng này!");
            }
        }

        return new HoSoResponseDTO(hoSo);
    }

    @Transactional
    public HoSoResponseDTO capNhatHoSoCaNhan(String username, CapNhatHoSoDTO dto) {
        TaiKhoan user = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));

        validateCccd(dto.getSoCccd(), user.getId());

        KhachHang hoSo = buildOrUpdateHoSo(user, dto);

        return new HoSoResponseDTO(khachHangRepository.save(hoSo));
    }

    private void validateCccd(String cccd, Long userId) {
        khachHangRepository.findBySoCccd(cccd).ifPresent(kh -> {
            if (kh.getTaiKhoan() != null && !kh.getTaiKhoan().getId().equals(userId)) {
                throw new BadRequestException("Số CCCD đã được đăng ký cho một tài khoản khác!");
            }
        });
    }

    private KhachHang buildOrUpdateHoSo(TaiKhoan user, CapNhatHoSoDTO dto) {
        KhachHang hoSo = khachHangRepository.findByTaiKhoan(user)
                .orElseGet(() -> {
                    KhachHang newHoSo = new KhachHang();
                    newHoSo.setTaiKhoan(user);
                    return newHoSo;
                });

        hoSo.setHoTen(dto.getHoTen());
        hoSo.setNgaySinh(dto.getNgaySinh());
        hoSo.setGioiTinh(dto.getGioiTinh());
        hoSo.setSoCccd(dto.getSoCccd());
        hoSo.setSoDienThoai(dto.getSoDienThoai());
        hoSo.setEmail(dto.getEmail());
        hoSo.setDiaChiThuongTru(dto.getDiaChiThuongTru());

        return hoSo;
    }
}