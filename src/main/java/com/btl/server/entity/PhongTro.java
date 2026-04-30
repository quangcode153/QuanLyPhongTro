package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "phong_tro")
public class PhongTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên phòng không được để trống!")
    @Column(name = "ten_phong")
    private String tenPhong;

    @NotNull(message = "Chưa nhập giá phòng!")
    @Min(value = 0, message = "Giá phòng không được là số âm!")
    @Column(name = "gia_phong")
    private Double giaPhong;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "chu_tro_id")
    private Long chuTroId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public Double getGiaPhong() { return giaPhong; }
    public void setGiaPhong(Double giaPhong) { this.giaPhong = giaPhong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Long getChuTroId() { return chuTroId; }
    public void setChuTroId(Long chuTroId) { this.chuTroId = chuTroId; }
}