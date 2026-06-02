package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.btl.server.enums.TrangThaiPhong;

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
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phòng không được là số âm!")
    @Column(name = "gia_phong")
    private BigDecimal giaPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiPhong trangThai = TrangThaiPhong.TRONG;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "chu_tro_id", nullable = false)
    private Long chuTroId;

    @Column(name = "gia_dien")
    private BigDecimal giaDien;

    @Column(name = "gia_nuoc")
    private BigDecimal giaNuoc;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "dien_tich")
    private Double dienTich;

    @Column(name = "hinh_anh", columnDefinition = "LONGTEXT")
    private String hinhAnh;

    @Column(name = "tien_coc")
    private BigDecimal tienCoc;

    /**
     * Lấy giá trị của id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Lấy giá trị của tenPhong.
     */
    public String getTenPhong() {
        return tenPhong;
    }

    /**
     * Cập nhật giá trị cho tenPhong.
     */
    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    /**
     * Lấy giá trị của giaPhong.
     */
    public BigDecimal getGiaPhong() {
        return giaPhong;
    }

    /**
     * Cập nhật giá trị cho giaPhong.
     */
    public void setGiaPhong(BigDecimal giaPhong) {
        this.giaPhong = giaPhong;
    }

    /**
     * Lấy giá trị của trangThai.
     */
    public TrangThaiPhong getTrangThai() {
        return trangThai;
    }

    /**
     * Cập nhật giá trị cho trangThai.
     */
    public void setTrangThai(TrangThaiPhong trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy giá trị của moTa.
     */
    public String getMoTa() {
        return moTa;
    }

    /**
     * Cập nhật giá trị cho moTa.
     */
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy giá trị của chuTroId.
     */
    public Long getChuTroId() {
        return chuTroId;
    }

    /**
     * Cập nhật giá trị cho chuTroId.
     */
    public void setChuTroId(Long chuTroId) {
        this.chuTroId = chuTroId;
    }

    /**
     * Lấy giá trị của giaDien.
     */
    public BigDecimal getGiaDien() {
        return giaDien;
    }

    /**
     * Cập nhật giá trị cho giaDien.
     */
    public void setGiaDien(BigDecimal giaDien) {
        this.giaDien = giaDien;
    }

    /**
     * Lấy giá trị của giaNuoc.
     */
    public BigDecimal getGiaNuoc() {
        return giaNuoc;
    }

    /**
     * Cập nhật giá trị cho giaNuoc.
     */
    public void setGiaNuoc(BigDecimal giaNuoc) {
        this.giaNuoc = giaNuoc;
    }

    /**
     * Lấy giá trị của diaChi.
     */
    public String getDiaChi() {
        return diaChi;
    }

    /**
     * Cập nhật giá trị cho diaChi.
     */
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    /**
     * Lấy giá trị của dienTich.
     */
    public Double getDienTich() {
        return dienTich;
    }

    /**
     * Cập nhật giá trị cho dienTich.
     */
    public void setDienTich(Double dienTich) {
        this.dienTich = dienTich;
    }

    /**
     * Lấy giá trị của hinhAnh.
     */
    public String getHinhAnh() {
        return hinhAnh;
    }

    /**
     * Cập nhật giá trị cho hinhAnh.
     */
    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    /**
     * Lấy giá trị của tienCoc.
     */
    public BigDecimal getTienCoc() {
        return tienCoc;
    }

    /**
     * Cập nhật giá trị cho tienCoc.
     */
    public void setTienCoc(BigDecimal tienCoc) {
        this.tienCoc = tienCoc;
    }
}