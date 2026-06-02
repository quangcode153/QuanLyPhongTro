package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.btl.server.enums.TrangThaiHopDong;

@Entity
@Table(name = "hop_dong")
public class HopDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    @NotNull(message = "Chưa nhập ngày bắt đầu hợp đồng!")
    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

        @Min(value = 0, message = "Tiền cọc không được là số âm!")
    @Column(name = "tien_coc")
    private BigDecimal tienCoc; 

        @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "VARCHAR(50)")
    private TrangThaiHopDong trangThai = TrangThaiHopDong.CHO_DUYET;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_id")
    @JsonIgnoreProperties(value = {"hopDongs", "matKhau", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    private TaiKhoan khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id")
    @JsonIgnoreProperties(value = {"hopDongs", "hibernateLazyInitializer", "handler"}, allowSetters = true)
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
     * Lấy giá trị của ngayBatDau.
     */
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    /**
     * Cập nhật giá trị cho ngayBatDau.
     */
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    /**
     * Lấy giá trị của ngayKetThuc.
     */
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    /**
     * Cập nhật giá trị cho ngayKetThuc.
     */
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    /**
     * Lấy giá trị của tienCoc.
     */
    public BigDecimal getTienCoc() { return tienCoc; }
    /**
     * Cập nhật giá trị cho tienCoc.
     */
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }

    /**
     * Lấy giá trị của trangThai.
     */
    public TrangThaiHopDong getTrangThai() { return trangThai; }
    /**
     * Cập nhật giá trị cho trangThai.
     */
    public void setTrangThai(TrangThaiHopDong trangThai) { this.trangThai = trangThai; }

    /**
     * Lấy giá trị của khachHang.
     */
    public TaiKhoan getKhachHang() { return khachHang; }
    /**
     * Cập nhật giá trị cho khachHang.
     */
    public void setKhachHang(TaiKhoan khachHang) { this.khachHang = khachHang; }

    /**
     * Lấy giá trị của phongTro.
     */
    public PhongTro getPhongTro() { return phongTro; }
    /**
     * Cập nhật giá trị cho phongTro.
     */
    public void setPhongTro(PhongTro phongTro) { this.phongTro = phongTro; }
}