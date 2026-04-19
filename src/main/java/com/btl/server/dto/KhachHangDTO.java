package com.btl.server.dto;

public class KhachHangDTO {
    private String tenKhach;
    private String soDienThoai;
    private String tenPhongDangThue;

    public String getTenKhach() { return tenKhach; }
    public void setTenKhach(String tenKhach) { this.tenKhach = tenKhach; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getTenPhongDangThue() { return tenPhongDangThue; }
    public void setTenPhongDangThue(String tenPhongDangThue) { this.tenPhongDangThue = tenPhongDangThue; }
}