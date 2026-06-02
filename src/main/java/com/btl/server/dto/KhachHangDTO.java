package com.btl.server.dto;

import java.time.LocalDate;

public class KhachHangDTO {
    
   
    private String hoTen; 
    
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soCccd;
    private String soDienThoai;
    private String email;
    private String diaChiThuongTru;
    
    private String tenPhongDangThue;

  
    /**
     * Lấy giá trị của hoTen.
     */
    public String getHoTen() { return hoTen; }
    /**
     * Cập nhật giá trị cho hoTen.
     */
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    /**
     * Lấy giá trị của ngaySinh.
     */
    public LocalDate getNgaySinh() { return ngaySinh; }
    /**
     * Cập nhật giá trị cho ngaySinh.
     */
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    /**
     * Lấy giá trị của gioiTinh.
     */
    public String getGioiTinh() { return gioiTinh; }
    /**
     * Cập nhật giá trị cho gioiTinh.
     */
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    /**
     * Lấy giá trị của soCccd.
     */
    public String getSoCccd() { return soCccd; }
    /**
     * Cập nhật giá trị cho soCccd.
     */
    public void setSoCccd(String soCccd) { this.soCccd = soCccd; }

    /**
     * Lấy giá trị của soDienThoai.
     */
    public String getSoDienThoai() { return soDienThoai; }
    /**
     * Cập nhật giá trị cho soDienThoai.
     */
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    /**
     * Lấy giá trị của email.
     */
    public String getEmail() { return email; }
    /**
     * Cập nhật giá trị cho email.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Lấy giá trị của diaChiThuongTru.
     */
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
    /**
     * Cập nhật giá trị cho diaChiThuongTru.
     */
    public void setDiaChiThuongTru(String diaChiThuongTru) { this.diaChiThuongTru = diaChiThuongTru; }

    /**
     * Lấy giá trị của tenPhongDangThue.
     */
    public String getTenPhongDangThue() { return tenPhongDangThue; }
    /**
     * Cập nhật giá trị cho tenPhongDangThue.
     */
    public void setTenPhongDangThue(String tenPhongDangThue) { this.tenPhongDangThue = tenPhongDangThue; }
}