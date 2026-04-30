package com.btl.server.controller;

import com.btl.server.dto.CapNhatHoSoDTO;
import com.btl.server.dto.HoSoResponseDTO;
import com.btl.server.dto.KhachHangDTO;
import com.btl.server.entity.KhachHang;
import com.btl.server.service.KhachHangService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KhachHangControllerTest {

    @Mock
    private KhachHangService khachHangService;

    @Mock
    private Principal principal;

    @InjectMocks
    private KhachHangController khachHangController;

    @Test
    void testLayDanhSachKhachHang_ShouldReturn200() {
        when(khachHangService.getKhachHangAnToan()).thenReturn(Collections.emptyList());

        ResponseEntity<List<KhachHangDTO>> response = khachHangController.layDanhSachKhachHang();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(khachHangService).getKhachHangAnToan();
        verifyNoMoreInteractions(khachHangService);
    }

    @Test
    void testLayHoSoCaNhan_ShouldReturn200_WhenValid() {
        HoSoResponseDTO mockResponse = new HoSoResponseDTO(new KhachHang());
        when(principal.getName()).thenReturn("user_test");
        when(khachHangService.layHoSoCaNhan("user_test")).thenReturn(mockResponse);

        ResponseEntity<HoSoResponseDTO> response = khachHangController.layHoSoCaNhan(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(principal).getName();
        verify(khachHangService).layHoSoCaNhan("user_test");
    }

    @Test
    void testCapNhatHoSoCaNhan_ShouldReturn200() {
        CapNhatHoSoDTO dto = new CapNhatHoSoDTO();
        HoSoResponseDTO mockResponse = new HoSoResponseDTO(new KhachHang());
        
        when(principal.getName()).thenReturn("user_test");
        when(khachHangService.capNhatHoSoCaNhan("user_test", dto)).thenReturn(mockResponse);

        ResponseEntity<HoSoResponseDTO> response = khachHangController.capNhatHoSoCaNhan(principal, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(khachHangService).capNhatHoSoCaNhan("user_test", dto);
    }

    @Test
    void testLayChiTietKhachHang_ShouldReturn200_WithLongId() {
        HoSoResponseDTO mockResponse = new HoSoResponseDTO(new KhachHang());
        Long targetId = 1L; // Sử dụng Long cho đồng bộ
        
        when(principal.getName()).thenReturn("admin");
        when(khachHangService.layHoSoTheoId(targetId, "admin")).thenReturn(mockResponse);

        ResponseEntity<HoSoResponseDTO> response = khachHangController.layChiTietKhachHang(targetId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(khachHangService).layHoSoTheoId(targetId, "admin");
    }

    @Test
    void testLayHoSoCaNhan_ShouldThrowException_WhenPrincipalNull() {
        // Vì trong Controller ông gọi trực tiếp principal.getName() nên sẽ văng NullPointer/IllegalState
        assertThrows(RuntimeException.class, () -> khachHangController.layHoSoCaNhan(null));
        verifyNoInteractions(khachHangService);
    }
}