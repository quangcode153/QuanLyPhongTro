package com.btl.server.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CapNhatHoSoDTO {

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở trong quá khứ")     private LocalDate ngaySinh;

    private String gioiTinh;

    @NotBlank(message = "Số CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 chữ số")     private String soCccd;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Địa chỉ thường trú không được để trống")
    private String diaChiThuongTru;

    private String tenNganHang;
    private String soTaiKhoan;
    private String chuTaiKhoan;

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
     * Lấy giá trị của tenNganHang.
     */
    public String getTenNganHang() { return tenNganHang; }
    /**
     * Cập nhật giá trị cho tenNganHang.
     */
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
    /**
     * Lấy giá trị của soTaiKhoan.
     */
    public String getSoTaiKhoan() { return soTaiKhoan; }
    /**
     * Cập nhật giá trị cho soTaiKhoan.
     */
    public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
    /**
     * Lấy giá trị của chuTaiKhoan.
     */
    public String getChuTaiKhoan() { return chuTaiKhoan; }
    /**
     * Cập nhật giá trị cho chuTaiKhoan.
     */
    public void setChuTaiKhoan(String chuTaiKhoan) { this.chuTaiKhoan = chuTaiKhoan; }
}