package com.btl.server.service;

import com.btl.server.dto.HopDongRequestDTO;
import com.btl.server.entity.HopDong;
import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.enums.TrangThaiHopDong;
import com.btl.server.enums.TrangThaiPhong;
import com.btl.server.exception.BadRequestException;
import com.btl.server.exception.ForbiddenException;
import com.btl.server.exception.NotFoundException;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.PhongTroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HopDongServiceTest {

    @Mock
    private HopDongRepository hopDongRepository;

    @Mock
    private PhongTroRepository phongTroRepository;

    @InjectMocks
    private HopDongService hopDongService;

    private TaiKhoan khachHang;
    private PhongTro phongTro;
    private HopDongRequestDTO requestDTO;
    private HopDong mockHopDong;

    @BeforeEach
    void setUp() {
        khachHang = new TaiKhoan();
        khachHang.setId(2L);
        khachHang.setUsername("tenant");
        khachHang.setRole("USER");

        phongTro = new PhongTro();
        phongTro.setId(1L);
        phongTro.setTenPhong("Phòng 101");
        phongTro.setGiaPhong(new BigDecimal("2500000.0"));
        phongTro.setTrangThai(TrangThaiPhong.TRONG);
        phongTro.setChuTroId(10L);

        requestDTO = new HopDongRequestDTO();
        requestDTO.setPhongTroId(1L);
        requestDTO.setNgayBatDau(LocalDate.now());
        requestDTO.setNgayKetThuc(LocalDate.now().plusMonths(6));
        requestDTO.setTienCoc(new BigDecimal("1000000.0"));

        mockHopDong = new HopDong();
        mockHopDong.setId(100L);
        mockHopDong.setKhachHang(khachHang);
        mockHopDong.setPhongTro(phongTro);
        mockHopDong.setNgayBatDau(requestDTO.getNgayBatDau());
        mockHopDong.setNgayKetThuc(requestDTO.getNgayKetThuc());
        mockHopDong.setTienCoc(requestDTO.getTienCoc());
        mockHopDong.setTrangThai(TrangThaiHopDong.CHO_DUYET);
    }

    @Test
    void testTaoHopDong_ThanhCong() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(phongTro));
        when(hopDongRepository.existsByPhongTroAndTrangThai(phongTro, TrangThaiHopDong.DA_DUYET)).thenReturn(false);
        when(hopDongRepository.existsByKhachHangAndPhongTroAndTrangThai(khachHang, phongTro, TrangThaiHopDong.CHO_DUYET)).thenReturn(false);
        when(hopDongRepository.save(any(HopDong.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HopDong result = hopDongService.taoHopDong(requestDTO, khachHang);

        assertNotNull(result);
        assertEquals(khachHang, result.getKhachHang());
        assertEquals(phongTro, result.getPhongTro());
        assertEquals(TrangThaiHopDong.CHO_DUYET, result.getTrangThai());
        verify(hopDongRepository).save(any(HopDong.class));
    }

    @Test
    void testTaoHopDong_NgayKetThucTruocNgayBatDau_BadRequest() {
        requestDTO.setNgayKetThuc(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> hopDongService.taoHopDong(requestDTO, khachHang));
    }

    @Test
    void testTaoHopDong_PhongNotFound() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> hopDongService.taoHopDong(requestDTO, khachHang));
    }

    @Test
    void testTaoHopDong_PhongKhongTrong_BadRequest() {
        phongTro.setTrangThai(TrangThaiPhong.DA_THUE);
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(phongTro));

        assertThrows(BadRequestException.class, () -> hopDongService.taoHopDong(requestDTO, khachHang));
    }

    @Test
    void testTaoHopDong_DaCoNguoiThueChinhThuc_BadRequest() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(phongTro));
        when(hopDongRepository.existsByPhongTroAndTrangThai(phongTro, TrangThaiHopDong.DA_DUYET)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> hopDongService.taoHopDong(requestDTO, khachHang));
    }

    @Test
    void testTaoHopDong_DaGuiYeuCauChoDuyet_BadRequest() {
        when(phongTroRepository.findById(1L)).thenReturn(Optional.of(phongTro));
        when(hopDongRepository.existsByPhongTroAndTrangThai(phongTro, TrangThaiHopDong.DA_DUYET)).thenReturn(false);
        when(hopDongRepository.existsByKhachHangAndPhongTroAndTrangThai(khachHang, phongTro, TrangThaiHopDong.CHO_DUYET)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> hopDongService.taoHopDong(requestDTO, khachHang));
    }

    @Test
    void testLayTatCaHopDong() {
        when(hopDongRepository.findAll()).thenReturn(Arrays.asList(mockHopDong));

        List<HopDong> list = hopDongService.layTatCaHopDong();

        assertEquals(1, list.size());
        assertEquals(mockHopDong, list.get(0));
    }

    @Test
    void testLayHopDongTheoChuTro() {
        when(hopDongRepository.findByPhongTro_ChuTroId(10L)).thenReturn(Arrays.asList(mockHopDong));

        List<HopDong> list = hopDongService.layHopDongTheoChuTro(10L);

        assertEquals(1, list.size());
        assertEquals(mockHopDong, list.get(0));
    }

    @Test
    void testLayHopDongTheoKhach() {
        when(hopDongRepository.findByKhachHang_Id(2L)).thenReturn(Arrays.asList(mockHopDong));

        List<HopDong> list = hopDongService.layHopDongTheoKhach(2L);

        assertEquals(1, list.size());
    }

    @Test
    void testLayHopDongTheoKhachVaTrangThai() {
        when(hopDongRepository.findByKhachHang_IdAndTrangThai(2L, TrangThaiHopDong.CHO_DUYET)).thenReturn(Arrays.asList(mockHopDong));

        List<HopDong> list = hopDongService.layHopDongTheoKhachVaTrangThai(2L, TrangThaiHopDong.CHO_DUYET);

        assertEquals(1, list.size());
    }

    @Test
    void testCapNhatTrangThaiHopDong_DuyetThanhCong() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("LANDLORD");

        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));
        when(hopDongRepository.existsByPhongTro_IdAndTrangThaiAndIdNot(1L, TrangThaiHopDong.DA_DUYET, 100L)).thenReturn(false);
        when(hopDongRepository.save(any(HopDong.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HopDong result = hopDongService.capNhatTrangThaiHopDong(100L, TrangThaiHopDong.DA_DUYET, null, landlord);

        assertNotNull(result);
        assertEquals(TrangThaiHopDong.DA_DUYET, result.getTrangThai());
        assertEquals(TrangThaiPhong.DA_THUE, phongTro.getTrangThai());
        verify(phongTroRepository).save(phongTro);
        verify(hopDongRepository).tuChoiCacHopDongChoDuyetKhac(1L, 100L, TrangThaiHopDong.CHO_DUYET, TrangThaiHopDong.TU_CHOI);
    }

    @Test
    void testCapNhatTrangThaiHopDong_TuChoi_GiaiPhongPhong() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("LANDLORD");

        mockHopDong.setTrangThai(TrangThaiHopDong.CHO_DUYET);
        phongTro.setTrangThai(TrangThaiPhong.DA_THUE);

        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));
        when(hopDongRepository.existsByPhongTro_IdAndTrangThaiAndIdNot(1L, TrangThaiHopDong.DA_DUYET, 100L)).thenReturn(false);
        when(hopDongRepository.save(any(HopDong.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HopDong result = hopDongService.capNhatTrangThaiHopDong(100L, TrangThaiHopDong.TU_CHOI, null, landlord);

        assertNotNull(result);
        assertEquals(TrangThaiHopDong.TU_CHOI, result.getTrangThai());
        assertEquals(TrangThaiPhong.TRONG, phongTro.getTrangThai());
        verify(phongTroRepository).save(phongTro);
    }

    @Test
    void testCapNhatTrangThaiHopDong_KhongCoQuyen_Forbidden() {
        TaiKhoan otherLandlord = new TaiKhoan();
        otherLandlord.setId(99L);
        otherLandlord.setRole("LANDLORD");

        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));

        assertThrows(ForbiddenException.class, () ->
                hopDongService.capNhatTrangThaiHopDong(100L, TrangThaiHopDong.DA_DUYET, null, otherLandlord));
    }

    @Test
    void testCapNhatTrangThaiHopDong_TrungDuyet_BadRequest() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("LANDLORD");

        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));
        when(hopDongRepository.existsByPhongTro_IdAndTrangThaiAndIdNot(1L, TrangThaiHopDong.DA_DUYET, 100L)).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                hopDongService.capNhatTrangThaiHopDong(100L, TrangThaiHopDong.DA_DUYET, null, landlord));
    }

    @Test
    void testGiaHanHopDong_ThanhCong() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("LANDLORD");

        LocalDate newDate = LocalDate.now().plusYears(1);
        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));
        when(hopDongRepository.save(any(HopDong.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HopDong result = hopDongService.giaHanHopDong(100L, newDate, landlord);

        assertNotNull(result);
        assertEquals(newDate, result.getNgayKetThuc());
    }

    @Test
    void testGiaHanHopDong_NgayKetThucMoiTruocNgayBatDau_BadRequest() {
        TaiKhoan landlord = new TaiKhoan();
        landlord.setId(10L);
        landlord.setRole("LANDLORD");

        LocalDate newDate = LocalDate.now().minusMonths(1);
        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));

        assertThrows(BadRequestException.class, () ->
                hopDongService.giaHanHopDong(100L, newDate, landlord));
    }

    @Test
    void testHuyHopDongBoiKhach_ThanhCong() {
        mockHopDong.setTrangThai(TrangThaiHopDong.DA_DUYET);
        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));

        hopDongService.huyHopDongBoiKhach(100L, khachHang);

        assertEquals(TrangThaiHopDong.YEU_CAU_HUY, mockHopDong.getTrangThai());
        verify(hopDongRepository).save(mockHopDong);
    }

    @Test
    void testHuyHopDongBoiKhach_KhongCoQuyen_Forbidden() {
        TaiKhoan stranger = new TaiKhoan();
        stranger.setId(88L);

        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));

        assertThrows(ForbiddenException.class, () ->
                hopDongService.huyHopDongBoiKhach(100L, stranger));
    }

    @Test
    void testHuyHopDongBoiKhach_TrangThaiKhongHopLe_BadRequest() {
        mockHopDong.setTrangThai(TrangThaiHopDong.CHO_DUYET);
        when(hopDongRepository.findById(100L)).thenReturn(Optional.of(mockHopDong));

        assertThrows(BadRequestException.class, () ->
                hopDongService.huyHopDongBoiKhach(100L, khachHang));
    }
}
