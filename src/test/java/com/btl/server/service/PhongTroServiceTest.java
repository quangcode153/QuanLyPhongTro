package com.btl.server.service;

import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.PhongTroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhongTroServiceTest {

    @Mock
    private PhongTroRepository phongTroRepository;

    @Mock
    private HopDongRepository hopDongRepository;

    @Mock
    private HoaDonRepository hoaDonRepository;

    @Mock
    private ChiSoDienNuocRepository chiSoDienNuocRepository;

    @InjectMocks
    private PhongTroService phongTroService;

    private PhongTro mockPhong;

    @BeforeEach
    void setUp() {
        mockPhong = new PhongTro();
        mockPhong.setId(1L);
        mockPhong.setTenPhong("Phòng 101");
        mockPhong.setGiaPhong(new BigDecimal("3000000.0"));
        mockPhong.setTrangThai(TrangThaiPhong.TRONG);
        mockPhong.setChuTroId(10L);
    }

    @Test
    void testGetAllPhongs() {
        when(phongTroRepository.findAll()).thenReturn(Arrays.asList(mockPhong));

        List<PhongTro> list = phongTroService.getAllPhongs();

        assertEquals(1, list.size());
        assertEquals("Phòng 101", list.get(0).getTenPhong());
    }

    @Test
    void testSavePhong() {
        when(phongTroRepository.save(any(PhongTro.class))).thenReturn(mockPhong);

        PhongTro saved = phongTroService.savePhong(mockPhong);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void testGetPhongById_ThanhCong() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));

        PhongTro result = phongTroService.getPhongById(1L);

        assertNotNull(result);
        assertEquals("Phòng 101", result.getTenPhong());
    }

    @Test
    void testGetPhongById_NotFound() {
        when(phongTroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> phongTroService.getPhongById(99L));
    }

    @Test
    void testGetPhongByChuTroId() {
        when(phongTroRepository.findByChuTroId(10L)).thenReturn(Arrays.asList(mockPhong));

        List<PhongTro> list = phongTroService.getPhongByChuTroId(10L);

        assertEquals(1, list.size());
    }

    @Test
    void testTimPhongTheoTrangThai() {
        when(phongTroRepository.findByTrangThai(TrangThaiPhong.TRONG)).thenReturn(Arrays.asList(mockPhong));

        List<PhongTro> list = phongTroService.timPhongTheoTrangThai(TrangThaiPhong.TRONG);

        assertEquals(1, list.size());
    }

    @Test
    void testLocPhongTheoGia() {
        BigDecimal giaToiDa = new BigDecimal("3500000.0");
        when(phongTroRepository.findByTrangThaiAndGiaPhongLessThanEqual(TrangThaiPhong.TRONG, giaToiDa))
                .thenReturn(Arrays.asList(mockPhong));

        List<PhongTro> list = phongTroService.locPhongTheoGia(TrangThaiPhong.TRONG, giaToiDa);

        assertEquals(1, list.size());
    }

    @Test
    void testSearchPhongTro() {
        when(phongTroRepository.searchPhongTro("101", null, BigDecimal.ZERO, new BigDecimal("4000000.0"), TrangThaiPhong.TRONG))
                .thenReturn(Arrays.asList(mockPhong));

        List<PhongTro> list = phongTroService.searchPhongTro("101", null, BigDecimal.ZERO, new BigDecimal("4000000.0"), TrangThaiPhong.TRONG);

        assertEquals(1, list.size());
    }

    @Test
    void testCapNhatTrangThaiPhong_SangDaThue() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));
        when(phongTroRepository.save(any(PhongTro.class))).thenReturn(mockPhong);

        PhongTro result = phongTroService.capNhatTrangThaiPhong(1L, TrangThaiPhong.DA_THUE);

        assertEquals(TrangThaiPhong.DA_THUE, result.getTrangThai());
        verify(hopDongRepository, never()).ketThucHopDongTheoPhong(any(), any(), any());
    }

    @Test
    void testCapNhatTrangThaiPhong_SangTrong_TuDongThanhLyHopDong() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));
        when(phongTroRepository.save(any(PhongTro.class))).thenReturn(mockPhong);
        when(hopDongRepository.ketThucHopDongTheoPhong(1L, TrangThaiHopDong.DA_DUYET, TrangThaiHopDong.DA_KET_THUC)).thenReturn(1);

        PhongTro result = phongTroService.capNhatTrangThaiPhong(1L, TrangThaiPhong.TRONG);

        assertEquals(TrangThaiPhong.TRONG, result.getTrangThai());
        verify(hopDongRepository).ketThucHopDongTheoPhong(1L, TrangThaiHopDong.DA_DUYET, TrangThaiHopDong.DA_KET_THUC);
    }

    @Test
    void testDeletePhong_ThanhCong() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));

        phongTroService.deletePhong(1L);

        verify(chiSoDienNuocRepository).deleteByPhongTro_Id(1L);
        verify(hoaDonRepository).deleteByPhongTro_Id(1L);
        verify(hopDongRepository).deleteByPhongTro_Id(1L);
        verify(phongTroRepository).delete(mockPhong);
    }

    @Test
    void testDeletePhong_PhongDangCoKhachThue_BadRequest() {
        mockPhong.setTrangThai(TrangThaiPhong.DA_THUE);
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));

        assertThrows(BadRequestException.class, () -> phongTroService.deletePhong(1L));

        verify(phongTroRepository, never()).delete(any());
    }
}
