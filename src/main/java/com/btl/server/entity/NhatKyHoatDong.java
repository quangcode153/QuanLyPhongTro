package com.btl.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhat_ky_hoat_dong")
public class NhatKyHoatDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_thuc_hien")
    private String nguoiThucHien;

    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "chi_tiet", length = 500)
    private String chiTiet;

    /**
     * Lấy giá trị của id.
     */
    public Long getId() { return id; }
    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Lấy giá trị của thoiGian.
     */
    public LocalDateTime getThoiGian() { return thoiGian; }
    /**
     * Cập nhật giá trị cho thoiGian.
     */
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    /**
     * Lấy giá trị của nguoiThucHien.
     */
    public String getNguoiThucHien() { return nguoiThucHien; }
    /**
     * Cập nhật giá trị cho nguoiThucHien.
     */
    public void setNguoiThucHien(String nguoiThucHien) { this.nguoiThucHien = nguoiThucHien; }

    /**
     * Lấy giá trị của hanhDong.
     */
    public String getHanhDong() { return hanhDong; }
    /**
     * Cập nhật giá trị cho hanhDong.
     */
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    /**
     * Lấy giá trị của chiTiet.
     */
    public String getChiTiet() { return chiTiet; }
    /**
     * Cập nhật giá trị cho chiTiet.
     */
    public void setChiTiet(String chiTiet) { this.chiTiet = chiTiet; }
}