package com.btl.server.dto;

import com.btl.server.entity.KhachHang;
import java.time.LocalDate;

public class HoSoResponseDTO {
    private Long id;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soCccd;
    private String soDienThoai;
    private String email;
    private String diaChiThuongTru;

    public HoSoResponseDTO(KhachHang entity) {
        this.id = entity.getId();
        this.hoTen = entity.getHoTen();
        this.ngaySinh = entity.getNgaySinh();
        this.gioiTinh = entity.getGioiTinh();
        this.soCccd = entity.getSoCccd();
        this.soDienThoai = entity.getSoDienThoai();
        this.email = entity.getEmail();
        this.diaChiThuongTru = entity.getDiaChiThuongTru();
    }

    public Long getId() { return id; }
    public String getHoTen() { return hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getSoCccd() { return soCccd; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
}