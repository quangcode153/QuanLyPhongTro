package com.btl.server.service;

import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.HoaDon;
import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiHoaDon;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoaDonServiceTest {

    @Mock
    private HoaDonRepository hoaDonRepository;

    @Mock
    private ChiSoDienNuocRepository chiSoRepo;

    @Mock
    private NhatKyService nhatKyService;

    @InjectMocks
    private HoaDonService hoaDonService;

    private PhongTro mockPhong;
    private HoaDon mockHoaDon;
    private ChiSoDienNuoc mockChiSo;

    @BeforeEach
    void setUp() {
        mockPhong = new PhongTro();
        mockPhong.setId(1L);
        mockPhong.setTenPhong("101");
        mockPhong.setGiaPhong(new BigDecimal("3000000.0"));
        mockPhong.setChuTroId(10L);

        mockHoaDon = new HoaDon();
        mockHoaDon.setId(100L);
        mockHoaDon.setPhongTro(mockPhong);
        mockHoaDon.setThang(5);
        mockHoaDon.setNam(2026);
        mockHoaDon.setTienPhong(new BigDecimal("3000000.0"));
        mockHoaDon.setTienDien(new BigDecimal("350000.0"));
        mockHoaDon.setTienNuoc(new BigDecimal("200000.0"));
        mockHoaDon.setTongTien(new BigDecimal("3550000.0"));
        mockHoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);

        mockChiSo = new ChiSoDienNuoc();
        mockChiSo.setId(50L);
        mockChiSo.setPhongTro(mockPhong);
        mockChiSo.setThang(5);
        mockChiSo.setNam(2026);
        mockChiSo.setSoDienCu(100);
        mockChiSo.setSoDienMoi(200);
        mockChiSo.setSoNuocCu(50);
        mockChiSo.setSoNuocMoi(60);
    }

    @Test
    void testXoaHoaDonBiSai_ThanhCong() {
        when(hoaDonRepository.findById(100L)).thenReturn(Optional.of(mockHoaDon));
        when(chiSoRepo.findByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(Optional.of(mockChiSo));

        hoaDonService.xoaHoaDonBiSai(100L);

        verify(chiSoRepo).delete(mockChiSo);
        verify(hoaDonRepository).delete(mockHoaDon);
        verify(nhatKyService).ghiLog(eq("XÓA HÓA ĐƠN"), anyString());
    }

    @Test
    void testXoaHoaDonBiSai_NotFound() {
        when(hoaDonRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                hoaDonService.xoaHoaDonBiSai(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(hoaDonRepository, never()).delete(any());
    }

    @Test
    void testLayHoaDonCuaChuTro() {
        when(hoaDonRepository.findByPhongTroChuTroId(10L)).thenReturn(Arrays.asList(mockHoaDon));

        List<HoaDon> result = hoaDonService.layHoaDonCuaChuTro(10L);

        assertEquals(1, result.size());
        assertEquals(mockHoaDon, result.get(0));
    }

    @Test
    void testLayDanhSachHoaDonCuaKhach_KhongCanBoSung() {
        when(hoaDonRepository.findHoaDonByKhachHangId(2L)).thenReturn(Arrays.asList(mockHoaDon));

        List<HoaDon> result = hoaDonService.layDanhSachHoaDonCuaKhach(2L);

        assertEquals(1, result.size());
        verify(hoaDonRepository, never()).save(any());
    }

    @Test
    void testLayDanhSachHoaDonCuaKhach_CanBoSung_ThanhCong() {
        HoaDon canBoSung = new HoaDon();
        canBoSung.setId(101L);
        canBoSung.setPhongTro(mockPhong);
        canBoSung.setThang(5);
        canBoSung.setNam(2026);
        canBoSung.setTienPhong(null);

        when(hoaDonRepository.findHoaDonByKhachHangId(2L)).thenReturn(Arrays.asList(canBoSung));
        when(chiSoRepo.findByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(Optional.of(mockChiSo));
        when(hoaDonRepository.save(canBoSung)).thenReturn(canBoSung);

        List<HoaDon> result = hoaDonService.layDanhSachHoaDonCuaKhach(2L);

        assertEquals(1, result.size());
        HoaDon updated = result.get(0);
        assertEquals(new BigDecimal("3000000.0"), updated.getTienPhong());
        assertEquals(new BigDecimal("350000.0"), updated.getTienDien());
        assertEquals(new BigDecimal("200000.0"), updated.getTienNuoc());
        verify(hoaDonRepository).save(canBoSung);
    }

    @Test
    void testThanhToanHoaDon_ThanhCong() {
        when(hoaDonRepository.findById(100L)).thenReturn(Optional.of(mockHoaDon));
        when(hoaDonRepository.save(any(HoaDon.class))).thenReturn(mockHoaDon);

        hoaDonService.thanhToanHoaDon(100L, 2L);

        assertEquals(TrangThaiHoaDon.DA_THANH_TOAN, mockHoaDon.getTrangThai());
        verify(hoaDonRepository).save(mockHoaDon);
        verify(nhatKyService).ghiLog(eq("THANH TOÁN"), anyString());
    }

    @Test
    void testThanhToanHoaDon_NotFound() {
        when(hoaDonRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                hoaDonService.thanhToanHoaDon(999L, 2L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(hoaDonRepository, never()).save(any());
    }
}
