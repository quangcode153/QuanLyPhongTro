package com.btl.server.service;

import com.btl.server.repository.HoaDonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoaDonServiceTest {

    @Mock
    private HoaDonRepository hoaDonRepository;

    @Mock
    private NhatKyService nhatKyService;

    @InjectMocks
    private HoaDonService hoaDonService;

    @Test
    void testXoaHoaDonBiSaiThanhCong() {
        Integer idHoaDon = 1;
        when(hoaDonRepository.existsById(idHoaDon)).thenReturn(true);

        hoaDonService.xoaHoaDonBiSai(idHoaDon);

        verify(hoaDonRepository).deleteById(idHoaDon);
        verify(nhatKyService).ghiLog("XÓA HÓA ĐƠN", "Đã xóa vĩnh viễn hóa đơn có ID = " + idHoaDon);
    }

    @Test
    void testXoaHoaDonBiSaiThatBai_KhongTonTai() {
        Integer idHoaDon = 99;
        when(hoaDonRepository.existsById(idHoaDon)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            hoaDonService.xoaHoaDonBiSai(idHoaDon);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(hoaDonRepository, never()).deleteById(anyInt());
        verify(nhatKyService, never()).ghiLog(anyString(), anyString());
    }
}