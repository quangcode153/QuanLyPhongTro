package com.btl.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nguoi_gui_id")
    private Integer nguoiGuiId;

    @Column(name = "nguoi_nhan_id")
    private Integer nguoiNhanId;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getNguoiGuiId() { return nguoiGuiId; }
    public void setNguoiGuiId(Integer nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }
    public Integer getNguoiNhanId() { return nguoiNhanId; }
    public void setNguoiNhanId(Integer nguoiNhanId) { this.nguoiNhanId = nguoiNhanId; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}