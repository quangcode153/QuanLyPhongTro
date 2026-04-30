package com.btl.server.controller;

import com.btl.server.service.HoaDonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HoaDonControllerTest {

    @Mock
    private HoaDonService hoaDonService;

    @InjectMocks
    private HoaDonController hoaDonController;

    @Test
    void testXoaHoaDonThanhCong_TraVe204() {
        Integer idHoaDon = 1;

        ResponseEntity<Void> response = hoaDonController.xoaHoaDon(idHoaDon);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(hoaDonService).xoaHoaDonBiSai(idHoaDon);
    }

    @Test
    void testXoaHoaDonThatBai_NemRaLoi() {
        Integer idHoaDon = 99;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hóa đơn"))
                .when(hoaDonService).xoaHoaDonBiSai(idHoaDon);

        assertThrows(ResponseStatusException.class, () -> {
            hoaDonController.xoaHoaDon(idHoaDon);
        });
        verify(hoaDonService).xoaHoaDonBiSai(idHoaDon);
    }
}