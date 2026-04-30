package com.btl.server.controller;

import com.btl.server.dto.PhieuTinhTienDTO;
import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.service.ChiSoDienNuocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChiSoDienNuocControllerTest {

    @Mock
    private ChiSoDienNuocService chiSoService;

    @InjectMocks
    private ChiSoDienNuocController controller;

    private ChiSoDienNuoc chiSoTest;
    private PhieuTinhTienDTO phieuDto;

    @BeforeEach
    void setUp() {
        chiSoTest = new ChiSoDienNuoc();
        phieuDto = new PhieuTinhTienDTO();
    }

    @Test
    void testChotSoThangNayThanhCong() {
        when(chiSoService.chotSoVaTinhTien(chiSoTest)).thenReturn(phieuDto);

        ResponseEntity<?> response = controller.chotSoThangNay(chiSoTest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(phieuDto, response.getBody());
        verify(chiSoService).chotSoVaTinhTien(chiSoTest);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testChotSoThangNayThatBai_TraVeLoiConflict() {
        when(chiSoService.chotSoVaTinhTien(chiSoTest)).thenThrow(new RuntimeException("Đã chốt số tháng này"));

        ResponseEntity<?> response = controller.chotSoThangNay(chiSoTest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("LỖI TRÙNG LẶP", body.get("trangThai"));
        assertEquals("Đã chốt số tháng này", body.get("thongBao"));
        verify(chiSoService).chotSoVaTinhTien(chiSoTest);
    }
}