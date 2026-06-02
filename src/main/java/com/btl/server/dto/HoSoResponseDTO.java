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
    private String tenNganHang;
    private String soTaiKhoan;
    private String chuTaiKhoan;

    /**
     * Khởi tạo HoSoResponseDTO từ thực thể KhachHang.
     */
    public HoSoResponseDTO(KhachHang entity) {
        this.id = entity.getId();
        this.hoTen = entity.getHoTen();
        this.ngaySinh = entity.getNgaySinh();
        this.gioiTinh = entity.getGioiTinh();
        this.soCccd = entity.getSoCccd();
        this.soDienThoai = entity.getSoDienThoai();
        this.email = entity.getEmail();
        this.diaChiThuongTru = entity.getDiaChiThuongTru();
        this.tenNganHang = entity.getTenNganHang();
        this.soTaiKhoan = entity.getSoTaiKhoan();
        this.chuTaiKhoan = entity.getChuTaiKhoan();
    }

    /**
     * Lấy giá trị của id.
     */
    public Long getId() { return id; }
    /**
     * Lấy giá trị của hoTen.
     */
    public String getHoTen() { return hoTen; }
    /**
     * Lấy giá trị của ngaySinh.
     */
    public LocalDate getNgaySinh() { return ngaySinh; }
    /**
     * Lấy giá trị của gioiTinh.
     */
    public String getGioiTinh() { return gioiTinh; }
    /**
     * Lấy giá trị của soCccd.
     */
    public String getSoCccd() { return soCccd; }
    /**
     * Lấy giá trị của soDienThoai.
     */
    public String getSoDienThoai() { return soDienThoai; }
    /**
     * Lấy giá trị của email.
     */
    public String getEmail() { return email; }
    /**
     * Lấy giá trị của diaChiThuongTru.
     */
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
    /**
     * Lấy giá trị của tenNganHang.
     */
    public String getTenNganHang() { return tenNganHang; }
    /**
     * Lấy giá trị của soTaiKhoan.
     */
    public String getSoTaiKhoan() { return soTaiKhoan; }
    /**
     * Lấy giá trị của chuTaiKhoan.
     */
    public String getChuTaiKhoan() { return chuTaiKhoan; }
}