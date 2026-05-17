package com.btl.server.service;

import com.btl.server.dto.PhieuTinhTienDTO;
import com.btl.server.entity.ChiSoDienNuoc;
import com.btl.server.entity.HoaDon;
import com.btl.server.entity.PhongTro;
import com.btl.server.enums.TrangThaiHoaDon;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.ChiSoDienNuocRepository;
import com.btl.server.repository.HoaDonRepository;
import com.btl.server.repository.PhongTroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChiSoDienNuocServiceTest {

    @Mock
    private ChiSoDienNuocRepository chiSoRepo;

    @Mock
    private PhongTroRepository phongTroRepository;

    @Mock
    private HoaDonRepository hoaDonRepository;

    @InjectMocks
    private ChiSoDienNuocService chiSoDienNuocService;

    private PhongTro mockPhong;
    private ChiSoDienNuoc mockChiSo;
    private HoaDon mockHoaDon;

    @BeforeEach
    void setUp() {
        mockPhong = new PhongTro();
        mockPhong.setId(1L);
        mockPhong.setTenPhong("101");
        mockPhong.setGiaPhong(new BigDecimal("3000000.0"));
        mockPhong.setChuTroId(10L);

        mockChiSo = new ChiSoDienNuoc();
        mockChiSo.setId(100L);
        mockChiSo.setPhongTro(mockPhong);
        mockChiSo.setThang(5);
        mockChiSo.setNam(2026);
        mockChiSo.setSoDienCu(100);
        mockChiSo.setSoDienMoi(200);
        mockChiSo.setSoNuocCu(50);
        mockChiSo.setSoNuocMoi(70);

        mockHoaDon = new HoaDon();
        mockHoaDon.setId(200L);
        mockHoaDon.setPhongTro(mockPhong);
        mockHoaDon.setThang(5);
        mockHoaDon.setNam(2026);
        mockHoaDon.setTienPhong(mockPhong.getGiaPhong());
        mockHoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);
    }

    @Test
    void testChotSoVaTinhTien_ThanhCong() {
        when(hoaDonRepository.existsByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(false);
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(mockPhong));
        when(chiSoRepo.save(any(ChiSoDienNuoc.class))).thenReturn(mockChiSo);
        when(hoaDonRepository.save(any(HoaDon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PhieuTinhTienDTO result = chiSoDienNuocService.chotSoVaTinhTien(mockChiSo);

        assertNotNull(result);
        assertEquals(1L, result.getPhongId());
        assertEquals(5, result.getThang());
        assertEquals(2026, result.getNam());
        assertEquals(new BigDecimal("3000000.0"), result.getGiaPhong());
        assertEquals(100, result.getSoDienDung());
        assertEquals(20, result.getSoNuocDung());

        assertEquals(new BigDecimal("350000.0"), result.getTienDien());
        assertEquals(new BigDecimal("400000.0"), result.getTienNuoc());
        assertEquals(new BigDecimal("3750000.0"), result.getTongTien());

        verify(chiSoRepo).save(mockChiSo);
        verify(hoaDonRepository).save(any(HoaDon.class));
    }

    @Test
    void testChotSoVaTinhTien_HoaDonDaTonTai_BadRequest() {
        when(hoaDonRepository.existsByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> chiSoDienNuocService.chotSoVaTinhTien(mockChiSo));
    }

    @Test
    void testChotSoVaTinhTien_ChiSoMoiNhoHonChiSoCu_BadRequest() {
        mockChiSo.setSoDienMoi(99);

        assertThrows(BadRequestException.class, () -> chiSoDienNuocService.chotSoVaTinhTien(mockChiSo));
    }

    @Test
    void testChotSoVaTinhTien_PhongNotFound() {
        when(hoaDonRepository.existsByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(false);
        when(phongTroRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> chiSoDienNuocService.chotSoVaTinhTien(mockChiSo));
    }

    @Test
    void testCapNhatChiSoVaTinhTien_ThanhCong() {
        ChiSoDienNuoc chiSoMoi = new ChiSoDienNuoc();
        chiSoMoi.setSoDienCu(100);
        chiSoMoi.setSoDienMoi(220);
        chiSoMoi.setSoNuocCu(50);
        chiSoMoi.setSoNuocMoi(80);

        when(hoaDonRepository.findById(200L)).thenReturn(Optional.of(mockHoaDon));
        when(chiSoRepo.findByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(Optional.of(mockChiSo));
        when(chiSoRepo.save(any(ChiSoDienNuoc.class))).thenReturn(mockChiSo);

        PhieuTinhTienDTO result = chiSoDienNuocService.capNhatChiSoVaTinhTien(200L, chiSoMoi);

        assertNotNull(result);
        assertEquals(120, result.getSoDienDung());
        assertEquals(30, result.getSoNuocDung());
        assertEquals(new BigDecimal("420000.0"), result.getTienDien());
        assertEquals(new BigDecimal("600000.0"), result.getTienNuoc());
        assertEquals(new BigDecimal("4020000.0"), result.getTongTien());

        verify(chiSoRepo).save(mockChiSo);
        verify(hoaDonRepository).save(mockHoaDon);
    }

    @Test
    void testCapNhatChiSoVaTinhTien_HoaDonDaThanhToan_BadRequest() {
        mockHoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        when(hoaDonRepository.findById(200L)).thenReturn(Optional.of(mockHoaDon));

        assertThrows(BadRequestException.class, () -> chiSoDienNuocService.capNhatChiSoVaTinhTien(200L, mockChiSo));
    }

    @Test
    void testCapNhatChiSoVaTinhTien_ChiSoMoiNhoHonChiSoCu_BadRequest() {
        ChiSoDienNuoc chiSoMoi = new ChiSoDienNuoc();
        chiSoMoi.setSoDienCu(100);
        chiSoMoi.setSoDienMoi(90);

        when(hoaDonRepository.findById(200L)).thenReturn(Optional.of(mockHoaDon));

        assertThrows(BadRequestException.class, () -> chiSoDienNuocService.capNhatChiSoVaTinhTien(200L, chiSoMoi));
    }

    @Test
    void testCapNhatChiSoVaTinhTien_ChiSoNotFound() {
        ChiSoDienNuoc chiSoMoi = new ChiSoDienNuoc();
        chiSoMoi.setSoDienCu(100);
        chiSoMoi.setSoDienMoi(150);
        chiSoMoi.setSoNuocCu(50);
        chiSoMoi.setSoNuocMoi(60);

        when(hoaDonRepository.findById(200L)).thenReturn(Optional.of(mockHoaDon));
        when(chiSoRepo.findByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> chiSoDienNuocService.capNhatChiSoVaTinhTien(200L, chiSoMoi));
    }

    @Test
    void testLayChiSoDienNuoc() {
        when(chiSoRepo.findByPhongTroIdAndThangAndNam(1L, 5, 2026)).thenReturn(Optional.of(mockChiSo));

        Optional<ChiSoDienNuoc> result = chiSoDienNuocService.layChiSoDienNuoc(1L, 5, 2026);

        assertTrue(result.isPresent());
        assertEquals(mockChiSo, result.get());
    }
}
