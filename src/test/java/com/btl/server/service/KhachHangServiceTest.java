package com.btl.server.service;

import com.btl.server.dto.CapNhatHoSoDTO;
import com.btl.server.dto.HoSoResponseDTO;
import com.btl.server.entity.KhachHang;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.KhachHangRepository;
import com.btl.server.repository.TaiKhoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KhachHangServiceTest {

    @Mock
    private KhachHangRepository khachHangRepository;

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private HopDongRepository hopDongRepository;

    @InjectMocks
    private KhachHangService khachHangService;

    private TaiKhoan mockUser;
    private KhachHang mockHoSo;

    @BeforeEach
    void setUp() {
        mockUser = new TaiKhoan();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRole("ROLE_USER");

        mockHoSo = new KhachHang();
        mockHoSo.setId(1L);
        mockHoSo.setHoTen("Nguyen Van A");
        mockHoSo.setTaiKhoan(mockUser);
    }

    @Test
    void testLayHoSoCaNhan_ThanhCong() {
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(khachHangRepository.findByTaiKhoan(mockUser)).thenReturn(Optional.of(mockHoSo));

        HoSoResponseDTO result = khachHangService.layHoSoCaNhan("testuser");

        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getHoTen());
    }

    @Test
    void testLayHoSoCaNhan_UserNotFound() {
        when(taiKhoanRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> khachHangService.layHoSoCaNhan("unknown"));
    }

    @Test
    void testLayHoSoTheoId_Admin_ThanhCong() {
        TaiKhoan admin = new TaiKhoan();
        admin.setRole("ROLE_ADMIN");
        when(taiKhoanRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        when(taiKhoanRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(khachHangRepository.findByTaiKhoan(mockUser)).thenReturn(Optional.of(mockHoSo));

        HoSoResponseDTO result = khachHangService.layHoSoTheoId(1L, "admin");

        assertNotNull(result);
        verify(hopDongRepository, never()).existsByKhachHang_IdAndPhongTro_ChuTroId(any(), any());
    }

    @Test
    void testLayHoSoTheoId_Landlord_CoQuyen() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("ROLE_LANDLORD");
        when(taiKhoanRepository.findByUsername("landlord")).thenReturn(Optional.of(landlord));

        when(taiKhoanRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(khachHangRepository.findByTaiKhoan(mockUser)).thenReturn(Optional.of(mockHoSo));
        when(hopDongRepository.existsByKhachHang_IdAndPhongTro_ChuTroId(1L, 10L)).thenReturn(true);

        HoSoResponseDTO result = khachHangService.layHoSoTheoId(1L, "landlord");

        assertNotNull(result);
    }

    @Test
    void testLayHoSoTheoId_Landlord_KhongCoQuyen_Forbidden() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("ROLE_LANDLORD");
        when(taiKhoanRepository.findByUsername("landlord")).thenReturn(Optional.of(landlord));

        when(taiKhoanRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(khachHangRepository.findByTaiKhoan(mockUser)).thenReturn(Optional.of(mockHoSo));
        when(hopDongRepository.existsByKhachHang_IdAndPhongTro_ChuTroId(1L, 10L)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> khachHangService.layHoSoTheoId(1L, "landlord"));
    }

    @Test
    void testCapNhatHoSoCaNhan_TrungCCCD_BadRequest() {
        CapNhatHoSoDTO dto = new CapNhatHoSoDTO();
        dto.setSoCccd("123456789");

        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        TaiKhoan stranger = new TaiKhoan();
        stranger.setId(99L);
        KhachHang strangerHoSo = new KhachHang();
        strangerHoSo.setTaiKhoan(stranger);

        when(khachHangRepository.findBySoCccd("123456789")).thenReturn(Optional.of(strangerHoSo));

        assertThrows(BadRequestException.class, () -> khachHangService.capNhatHoSoCaNhan("testuser", dto));
    }

    @Test
    void testCapNhatHoSoCaNhan_LuuMoi_ThanhCong() {
        CapNhatHoSoDTO dto = new CapNhatHoSoDTO();
        dto.setHoTen("Moi Cập Nhật");
        dto.setSoCccd("987654321");

        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(khachHangRepository.findByTaiKhoan(mockUser)).thenReturn(Optional.empty());
        when(khachHangRepository.findBySoCccd("987654321")).thenReturn(Optional.empty());
        when(khachHangRepository.save(any(KhachHang.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HoSoResponseDTO result = khachHangService.capNhatHoSoCaNhan("testuser", dto);

        assertEquals("Moi Cập Nhật", result.getHoTen());
        verify(khachHangRepository).save(any(KhachHang.class));
    }
}