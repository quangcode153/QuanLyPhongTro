package com.btl.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_gui_id")
    private Integer nguoiGuiId;

    @Column(name = "nguoi_nhan_id")
    private Integer nguoiNhanId;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian = LocalDateTime.now();

    /**
     * Lấy giá trị của id.
     */
    public Long getId() { return id; }
    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) { this.id = id; }
    /**
     * Lấy giá trị của nguoiGuiId.
     */
    public Integer getNguoiGuiId() { return nguoiGuiId; }
    /**
     * Cập nhật giá trị cho nguoiGuiId.
     */
    public void setNguoiGuiId(Integer nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }
    /**
     * Lấy giá trị của nguoiNhanId.
     */
    public Integer getNguoiNhanId() { return nguoiNhanId; }
    /**
     * Cập nhật giá trị cho nguoiNhanId.
     */
    public void setNguoiNhanId(Integer nguoiNhanId) { this.nguoiNhanId = nguoiNhanId; }
    /**
     * Lấy giá trị của noiDung.
     */
    public String getNoiDung() { return noiDung; }
    /**
     * Cập nhật giá trị cho noiDung.
     */
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    /**
     * Lấy giá trị của thoiGian.
     */
    public LocalDateTime getThoiGian() { return thoiGian; }
    /**
     * Cập nhật giá trị cho thoiGian.
     */
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}