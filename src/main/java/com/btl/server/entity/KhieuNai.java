package com.btl.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "khieu_nai")
public class KhieuNai {

    public enum TrangThaiKhieuNai {
        CHO_XU_LY,
        DANG_XU_LY,
        DA_GIAI_QUYET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_gui_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TaiKhoan nguoiGui;

    @Column(nullable = false)
    private String tieuDe;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiKhieuNai trangThai = TrangThaiKhieuNai.CHO_XU_LY;

    @Column(name = "thoi_gian_gui", updatable = false)
    private LocalDateTime thoiGianGui;

    /**
     * Tự động gán thời gian hiện tại khi khiếu nại được tạo lập lần đầu.
     */
    @PrePersist
    protected void onCreate() {
        if (this.thoiGianGui == null) {
            this.thoiGianGui = LocalDateTime.now();
        }
    }

    /**
     * Lấy giá trị của id.
     */
    public Long getId() { return id; }
    /**
     * Cập nhật giá trị cho id.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Lấy giá trị của nguoiGui.
     */
    public TaiKhoan getNguoiGui() { return nguoiGui; }
    /**
     * Cập nhật giá trị cho nguoiGui.
     */
    public void setNguoiGui(TaiKhoan nguoiGui) { this.nguoiGui = nguoiGui; }

    /**
     * Lấy giá trị của tieuDe.
     */
    public String getTieuDe() { return tieuDe; }
    /**
     * Cập nhật giá trị cho tieuDe.
     */
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    /**
     * Lấy giá trị của noiDung.
     */
    public String getNoiDung() { return noiDung; }
    /**
     * Cập nhật giá trị cho noiDung.
     */
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    /**
     * Lấy giá trị của trangThai.
     */
    public TrangThaiKhieuNai getTrangThai() { return trangThai; }
    /**
     * Cập nhật giá trị cho trangThai.
     */
    public void setTrangThai(TrangThaiKhieuNai trangThai) { this.trangThai = trangThai; }

    /**
     * Lấy giá trị của thoiGianGui.
     */
    public LocalDateTime getThoiGianGui() { return thoiGianGui; }
    /**
     * Cập nhật giá trị cho thoiGianGui.
     */
    public void setThoiGianGui(LocalDateTime thoiGianGui) { this.thoiGianGui = thoiGianGui; }
}