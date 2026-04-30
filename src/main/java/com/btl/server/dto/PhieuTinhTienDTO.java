package com.btl.server.dto;

public class PhieuTinhTienDTO {
    private Long phongId;
    private Integer thang;
    private Integer nam;
    private Double giaPhong;
    private Integer soDienDung;
    private Double tienDien;
    private Integer soNuocDung;
    private Double tienNuoc;
    private Double tongTien;

    public Long getPhongId() { return phongId; }
    public void setPhongId(Long phongId) { this.phongId = phongId; }

    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }

    public Double getGiaPhong() { return giaPhong; }
    public void setGiaPhong(Double giaPhong) { this.giaPhong = giaPhong; }

    public Integer getSoDienDung() { return soDienDung; }
    public void setSoDienDung(Integer soDienDung) { this.soDienDung = soDienDung; }

    public Double getTienDien() { return tienDien; }
    public void setTienDien(Double tienDien) { this.tienDien = tienDien; }

    public Integer getSoNuocDung() { return soNuocDung; }
    public void setSoNuocDung(Integer soNuocDung) { this.soNuocDung = soNuocDung; }

    public Double getTienNuoc() { return tienNuoc; }
    public void setTienNuoc(Double tienNuoc) { this.tienNuoc = tienNuoc; }

    public Double getTongTien() { return tongTien; }
    public void setTongTien(Double tongTien) { this.tongTien = tongTien; }
}