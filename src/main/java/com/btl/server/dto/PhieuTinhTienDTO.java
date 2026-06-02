package com.btl.server.dto;

import java.math.BigDecimal;

public class PhieuTinhTienDTO {
    private Long phongId;
    private Integer thang;
    private Integer nam;
    
    private BigDecimal giaPhong;
    
    private Integer soDienDung;
    
    private BigDecimal tienDien;
    
    private Integer soNuocDung;
    
   private BigDecimal tienNuoc;
    
    private BigDecimal tongTien;

    /**
     * Lấy giá trị của phongId.
     */
    public Long getPhongId() { return phongId; }
    /**
     * Cập nhật giá trị cho phongId.
     */
    public void setPhongId(Long phongId) { this.phongId = phongId; }

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
     * Lấy giá trị của giaPhong.
     */
    public BigDecimal getGiaPhong() { return giaPhong; }
    /**
     * Cập nhật giá trị cho giaPhong.
     */
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }

    /**
     * Lấy giá trị của soDienDung.
     */
    public Integer getSoDienDung() { return soDienDung; }
    /**
     * Cập nhật giá trị cho soDienDung.
     */
    public void setSoDienDung(Integer soDienDung) { this.soDienDung = soDienDung; }

    /**
     * Lấy giá trị của tienDien.
     */
    public BigDecimal getTienDien() { return tienDien; }
    /**
     * Cập nhật giá trị cho tienDien.
     */
    public void setTienDien(BigDecimal tienDien) { this.tienDien = tienDien; }

    /**
     * Lấy giá trị của soNuocDung.
     */
    public Integer getSoNuocDung() { return soNuocDung; }
    /**
     * Cập nhật giá trị cho soNuocDung.
     */
    public void setSoNuocDung(Integer soNuocDung) { this.soNuocDung = soNuocDung; }

    /**
     * Lấy giá trị của tienNuoc.
     */
    public BigDecimal getTienNuoc() { return tienNuoc; }
    /**
     * Cập nhật giá trị cho tienNuoc.
     */
    public void setTienNuoc(BigDecimal tienNuoc) { this.tienNuoc = tienNuoc; }

    /**
     * Lấy giá trị của tongTien.
     */
    public BigDecimal getTongTien() { return tongTien; }
    /**
     * Cập nhật giá trị cho tongTien.
     */
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
}