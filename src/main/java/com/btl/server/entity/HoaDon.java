package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.btl.server.enums.TrangThaiHoaDon;

@Entity
@Table(name = "hoa_don")
public class HoaDon {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Thiếu thông tin tháng!")
    @Min(value = 1, message = "Tháng không hợp lệ!")
    @Max(value = 12, message = "Tháng không hợp lệ!")
    @Column(name = "thang")
    private Integer thang;

    @NotNull(message = "Thiếu thông tin năm!")
    @Min(value = 2000, message = "Năm không hợp lệ!")
    @Column(name = "nam")
    private Integer nam;

    @NotNull(message = "Chưa có tổng tiền!")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng tiền không được là số âm!")
    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiHoaDon trangThai = TrangThaiHoaDon.CHUA_THANH_TOAN;

    @Column(name = "tien_phong")
    private BigDecimal tienPhong;

    @Column(name = "tien_dien")
    private BigDecimal tienDien;

    @Column(name = "tien_nuoc")
    private BigDecimal tienNuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "hoaDons"})
    private PhongTro phongTro;

    /**
     * Lấy giá trị của id.
     */
    public Long getId() { return id; }
    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Lấy giá trị của thang.
     */
    public Integer getThang() { return thang; }
    /**
     * Cập nhật giá trị cho thang.
     */
    public void setThang(Integer thang) { this.thang = thang; }

    /**
     * Lấy giá trị của nam.
     */
    public Integer getNam() { return nam; }
    /**
     * Cập nhật giá trị cho nam.
     */
    public void setNam(Integer nam) { this.nam = nam; }

    /**
     * Lấy giá trị của tongTien.
     */
    public BigDecimal getTongTien() { return tongTien; }
    /**
     * Cập nhật giá trị cho tongTien.
     */
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    /**
     * Lấy giá trị của tienPhong.
     */
    public BigDecimal getTienPhong() { return tienPhong; }
    /**
     * Cập nhật giá trị cho tienPhong.
     */
    public void setTienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; }

    /**
     * Lấy giá trị của tienDien.
     */
    public BigDecimal getTienDien() { return tienDien; }
    /**
     * Cập nhật giá trị cho tienDien.
     */
    public void setTienDien(BigDecimal tienDien) { this.tienDien = tienDien; }

    /**
     * Lấy giá trị của tienNuoc.
     */
    public BigDecimal getTienNuoc() { return tienNuoc; }
    /**
     * Cập nhật giá trị cho tienNuoc.
     */
    public void setTienNuoc(BigDecimal tienNuoc) { this.tienNuoc = tienNuoc; }

    /**
     * Lấy giá trị của trangThai.
     */
    public TrangThaiHoaDon getTrangThai() { return trangThai; }
    /**
     * Cập nhật giá trị cho trangThai.
     */
    public void setTrangThai(TrangThaiHoaDon trangThai) { this.trangThai = trangThai; }

    /**
     * Lấy giá trị của phongTro.
     */
    public PhongTro getPhongTro() { return phongTro; }
    /**
     * Cập nhật giá trị cho phongTro.
     */
    public void setPhongTro(PhongTro phongTro) { this.phongTro = phongTro; }
}