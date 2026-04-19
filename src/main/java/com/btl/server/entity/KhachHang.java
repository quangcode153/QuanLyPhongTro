package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "khach_thue")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "khach_id")
    private Integer id;

    @NotBlank(message = "Tên khách hàng không được để trống!")
    @Column(name = "ho_ten")
    private String tenKhach;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @ManyToOne
    @JoinColumn(name = "id_phong_dang_thue")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PhongTro phongTro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTenKhach() { return tenKhach; }
    public void setTenKhach(String tenKhach) { this.tenKhach = tenKhach; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public PhongTro getPhongTro() { return phongTro; }
    public void setPhongTro(PhongTro phongTro) { this.phongTro = phongTro; }
}