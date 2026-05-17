package com.btl.server.controller;

import com.btl.server.dto.HopDongRequestDTO;
import com.btl.server.entity.HopDong;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.HopDongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HopDongControllerTest {

    @Mock
    private HopDongService hopDongService;

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private HopDongController hopDongController;

    private TaiKhoan mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new TaiKhoan();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRole("ROLE_USER");
    }

    @Test
    void testXemDanhSachHopDong_ShouldReturn200AndEmptyList() {
        when(hopDongService.layTatCaHopDong()).thenReturn(Collections.emptyList());

        ResponseEntity<List<HopDong>> response = hopDongController.xemDanhSachHopDong();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hopDongService).layTatCaHopDong();
    }

    @Test
    void testKyHopDongMoiThanhCong_ShouldReturn200() {
        HopDongRequestDTO request = new HopDongRequestDTO();
        request.setPhongTroId(10L);

        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<?> response = hopDongController.kyHopDongMoi(request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đã tạo yêu cầu thuê phòng thành công!", ((Map<?, ?>) response.getBody()).get("message"));
        verify(hopDongService).taoHopDong(eq(request), eq(mockUser));
    }

    @Test
    void testLayHopDongCuaChuTro_ShouldThrowForbidden_WhenXemCuaNguoiKhac() {
        mockUser.setRole("ROLE_LANDLORD");
        mockUser.setId(2L);
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        assertThrows(ForbiddenException.class, () -> {
            hopDongController.layHopDongCuaChuTro(1L, principal);
        });
    }

    @Test
    void testLayHopDongCuaKhach_ShouldReturnAll_WhenTrangThaiLaALL() {
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(hopDongService.layHopDongTheoKhach(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<HopDong>> response = hopDongController.layHopDongCuaKhach(1L, "ALL", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hopDongService).layHopDongTheoKhach(1L);
    }

    @Test
    void testLayHopDongCuaKhach_ShouldReturnFiltered_WhenCoTrangThaiHopLe() {
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        when(hopDongService.layHopDongTheoKhachVaTrangThai(1L, TrangThaiHopDong.DA_DUYET))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<HopDong>> response = hopDongController.layHopDongCuaKhach(1L, "DA_DUYET", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hopDongService).layHopDongTheoKhachVaTrangThai(1L, TrangThaiHopDong.DA_DUYET);
    }

    @Test
    void testCapNhatTrangThai_Success() {
        mockUser.setRole("ROLE_LANDLORD");
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<?> response = hopDongController.capNhatTrangThai(100L, "DA_DUYET", null, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(hopDongService).capNhatTrangThaiHopDong(eq(100L), eq(TrangThaiHopDong.DA_DUYET), eq(null), eq(mockUser));
    }

    @Test
    void testCapNhatTrangThai_InvalidEnum_ShouldThrowException() {
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        assertThrows(com.btl.server.exception.BadRequestException.class, () -> {
            hopDongController.capNhatTrangThai(100L, "KHA_NGHI", null, principal);
        });
    }
}