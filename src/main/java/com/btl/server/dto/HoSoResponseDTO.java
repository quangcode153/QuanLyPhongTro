package com.btl.server.dto;

import com.btl.server.entity.KhachHang;
import java.time.LocalDate;

public class HoSoResponseDTO {
    private Integer id;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soCccd;
    private String soDienThoai;
    private String email;
    private String diaChiThuongTru;

    // Constructor tự động map từ Entity sang DTO
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

    // --- GETTERS --- (Chỉ cần Getters để parse ra JSON)
    public Integer getId() { return id; }
    public String getHoTen() { return hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getSoCccd() { return soCccd; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
}