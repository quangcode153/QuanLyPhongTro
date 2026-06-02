package com.btl.server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tieuDe;
    @Column(columnDefinition = "TEXT")
    private String noiDung;
    
    private LocalDateTime ngayDang;
    private Long chuTroId;
    
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
     * Lấy giá trị của tieuDe.
     */
    public String getTieuDe() {
        return tieuDe;
    }
    /**
     * Cập nhật giá trị cho tieuDe.
     */
    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }
    /**
     * Lấy giá trị của noiDung.
     */
    public String getNoiDung() {
        return noiDung;
    }
    /**
     * Cập nhật giá trị cho noiDung.
     */
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
    /**
     * Lấy giá trị của ngayDang.
     */
    public LocalDateTime getNgayDang() {
        return ngayDang;
    }
    /**
     * Cập nhật giá trị cho ngayDang.
     */
    public void setNgayDang(LocalDateTime ngayDang) {
        this.ngayDang = ngayDang;
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
}