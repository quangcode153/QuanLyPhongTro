package com.btl.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.btl.server.enums.TrangThaiPhong;

@Entity
@Table(name = "phong_tro")
public class PhongTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên phòng không được để trống!")
    @Column(name = "ten_phong")
    private String tenPhong;

    // 🔥 TỐI ƯU 1: Đồng bộ sang BigDecimal và dùng @DecimalMin
    @NotNull(message = "Chưa nhập giá phòng!")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phòng không được là số âm!")
    @Column(name = "gia_phong")
    private BigDecimal giaPhong;

    // 🔥 TỐI ƯU 2: Ép dùng Enum và set mặc định là TRỐNG
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiPhong trangThai = TrangThaiPhong.TRONG;

    // 🔥 TỐI ƯU 3: Cấu hình TEXT để chủ trọ có thể viết mô tả siêu dài
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "chu_tro_id", nullable = false)
    private Long chuTroId;

    // ==========================
    // GETTERS & SETTERS
    // ==========================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public BigDecimal getGiaPhong() { return giaPhong; }
    public void setGiaPhong(BigDecimal giaPhong) { this.giaPhong = giaPhong; }

    public TrangThaiPhong getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhong trangThai) { this.trangThai = trangThai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Long getChuTroId() { return chuTroId; }
    public void setChuTroId(Long chuTroId) { this.chuTroId = chuTroId; }
}