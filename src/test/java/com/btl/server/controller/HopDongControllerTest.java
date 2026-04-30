package com.btl.server.controller;

import com.btl.server.entity.HopDong;
import com.btl.server.entity.TaiKhoan;
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
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        mockUser.setId(1L); // 🔥 FIX: Integer -> Long
        mockUser.setUsername("testuser");
        mockUser.setRole("USER");
    }

    @Test
    void testXemDanhSachHopDong_ShouldReturn200AndEmptyList() {
        when(hopDongService.layTatCaHopDong()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<HopDong>> response = hopDongController.xemDanhSachHopDong();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(hopDongService).layTatCaHopDong();
        verifyNoMoreInteractions(hopDongService, taiKhoanRepository);
    }

    @Test
    void testKyHopDongMoiThanhCong_ShouldReturn200() {
        HopDong request = new HopDong();
        request.setTienCoc(1000.0);

        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        ResponseEntity<?> response = hopDongController.kyHopDongMoi(request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đã tạo yêu cầu thuê phòng thành công!", ((Map<?, ?>) response.getBody()).get("message"));
        verify(principal).getName();
        verify(taiKhoanRepository).findByUsername("testuser");
        verify(hopDongService).taoHopDong(any(HopDong.class));
        verifyNoMoreInteractions(hopDongService, taiKhoanRepository);
    }

    @Test
    void testLayHopDongCuaChuTro_ShouldThrowForbidden_WhenXemCuaNguoiKhac() {
        mockUser.setRole("LANDLORD");
        mockUser.setId(2L); // 🔥 FIX: Integer -> Long
        
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hopDongController.layHopDongCuaChuTro(1L, principal);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(principal).getName();
        verify(taiKhoanRepository).findByUsername("testuser");
        verifyNoMoreInteractions(hopDongService, taiKhoanRepository);
    }

    @Test
    void testLayHopDongCuaKhach_ShouldReturnAll_WhenKhongCoTrangThai() {
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(hopDongService.layHopDongTheoKhach(1L)).thenReturn(Collections.emptyList()); // 🔥 FIX: 1L

        ResponseEntity<List<HopDong>> response = hopDongController.layHopDongCuaKhach(1L, null, principal); // 🔥 FIX: 1L

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(principal).getName();
        verify(taiKhoanRepository).findByUsername("testuser");
        verify(hopDongService).layHopDongTheoKhach(1L);
        verifyNoMoreInteractions(hopDongService, taiKhoanRepository);
    }

    @Test
    void testLayHopDongCuaKhach_ShouldReturnFiltered_WhenCoTrangThai() {
        when(principal.getName()).thenReturn("testuser");
        when(taiKhoanRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(hopDongService.layHopDongTheoKhachVaTrangThai(1L, "ĐÃ_DUYỆT")).thenReturn(Collections.emptyList()); // 🔥 FIX: 1L

        ResponseEntity<List<HopDong>> response = hopDongController.layHopDongCuaKhach(1L, "ĐÃ_DUYỆT", principal); // 🔥 FIX: 1L

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(principal).getName();
        verify(taiKhoanRepository).findByUsername("testuser");
        verify(hopDongService).layHopDongTheoKhachVaTrangThai(1L, "ĐÃ_DUYỆT");
        verifyNoMoreInteractions(hopDongService, taiKhoanRepository);
    }
}