package com.btl.server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class HopDongRequestDTO {

    @NotNull(message = "ID phòng trọ không được để trống!")
    private Long phongTroId;

    @NotNull(message = "Ngày bắt đầu không được để trống!")
    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    @Min(value = 0, message = "Tiền cọc không được là số âm!")
    private BigDecimal tienCoc;

    /**
     * Lấy giá trị của phongTroId.
     */
    public Long getPhongTroId() { return phongTroId; }
    /**
     * Cập nhật giá trị cho phongTroId.
     */
    public void setPhongTroId(Long phongTroId) { this.phongTroId = phongTroId; }
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
}