package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "hop_dong")
public class HopDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Chưa nhập ngày bắt đầu hợp đồng!")
    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Min(value = 0, message = "Tiền cọc không được là số âm!")
    @Column(name = "tien_coc")
    private Double tienCoc;

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "khach_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "phong_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PhongTro phongTro;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public Double getTienCoc() { return tienCoc; }
    public void setTienCoc(Double tienCoc) { this.tienCoc = tienCoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public PhongTro getPhongTro() { return phongTro; }
    public void setPhongTro(PhongTro phongTro) { this.phongTro = phongTro; }
}