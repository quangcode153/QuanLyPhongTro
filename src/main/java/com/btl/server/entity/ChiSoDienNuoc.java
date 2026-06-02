package com.btl.server.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "chi_so_dien_nuoc")
public class ChiSoDienNuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thang")
    private Integer thang;

    @Column(name = "nam")
    private Integer nam;

    @Column(name = "so_dien_cu")
    private Integer soDienCu;

    @Column(name = "so_dien_moi")
    private Integer soDienMoi;

    @Column(name = "so_nuoc_cu")
    private Integer soNuocCu;

    @Column(name = "so_nuoc_moi")
    private Integer soNuocMoi;

    @ManyToOne
    @JoinColumn(name = "phong_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
     * Lấy giá trị của soDienCu.
     */
    public Integer getSoDienCu() { return soDienCu; }
    /**
     * Cập nhật giá trị cho soDienCu.
     */
    public void setSoDienCu(Integer soDienCu) { this.soDienCu = soDienCu; }

    /**
     * Lấy giá trị của soDienMoi.
     */
    public Integer getSoDienMoi() { return soDienMoi; }
    /**
     * Cập nhật giá trị cho soDienMoi.
     */
    public void setSoDienMoi(Integer soDienMoi) { this.soDienMoi = soDienMoi; }

    /**
     * Lấy giá trị của soNuocCu.
     */
    public Integer getSoNuocCu() { return soNuocCu; }
    /**
     * Cập nhật giá trị cho soNuocCu.
     */
    public void setSoNuocCu(Integer soNuocCu) { this.soNuocCu = soNuocCu; }

    /**
     * Lấy giá trị của soNuocMoi.
     */
    public Integer getSoNuocMoi() { return soNuocMoi; }
    /**
     * Cập nhật giá trị cho soNuocMoi.
     */
    public void setSoNuocMoi(Integer soNuocMoi) { this.soNuocMoi = soNuocMoi; }

    /**
     * Lấy giá trị của phongTro.
     */
    public PhongTro getPhongTro() { return phongTro; }
    /**
     * Cập nhật giá trị cho phongTro.
     */
    public void setPhongTro(PhongTro phongTro) { this.phongTro = phongTro; }
}