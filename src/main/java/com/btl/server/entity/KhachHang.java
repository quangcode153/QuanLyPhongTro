package com.btl.server.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
@Table(name = "khach_thue")
public class KhachHang {

	@Id
	@Column(name = "khach_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "khach_id")
	@JsonIgnore
	private TaiKhoan taiKhoan;

	@Column(name = "ho_ten")
	private String hoTen;

	@Column(name = "ngay_sinh")
	private LocalDate ngaySinh;

	@Column(name = "gioi_tinh")
	private String gioiTinh;

	@Column(name = "so_cccd", unique = true)
	private String soCccd;

	@Column(name = "so_dien_thoai")
	private String soDienThoai;

	@Column(name = "email")
	private String email;

	@Column(name = "dia_chi_thuong_tru", columnDefinition = "TEXT")
	private String diaChiThuongTru;

	@Column(name = "ten_ngan_hang")
	private String tenNganHang;

	@Column(name = "so_tai_khoan")
	private String soTaiKhoan;

	@Column(name = "chu_tai_khoan")
	private String chuTaiKhoan;

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
	 * Lấy giá trị của taiKhoan.
	 */
	public TaiKhoan getTaiKhoan() {
		return taiKhoan;
	}

	/**
	 * Cập nhật giá trị cho taiKhoan.
	 */
	public void setTaiKhoan(TaiKhoan taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	/**
	 * Lấy giá trị của hoTen.
	 */
	public String getHoTen() {
		return hoTen;
	}

	/**
	 * Cập nhật giá trị cho hoTen.
	 */
	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	/**
	 * Lấy giá trị của ngaySinh.
	 */
	public LocalDate getNgaySinh() {
		return ngaySinh;
	}

	/**
	 * Cập nhật giá trị cho ngaySinh.
	 */
	public void setNgaySinh(LocalDate ngaySinh) {
		this.ngaySinh = ngaySinh;
	}

	/**
	 * Lấy giá trị của gioiTinh.
	 */
	public String getGioiTinh() {
		return gioiTinh;
	}

	/**
	 * Cập nhật giá trị cho gioiTinh.
	 */
	public void setGioiTinh(String gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	/**
	 * Lấy giá trị của soCccd.
	 */
	public String getSoCccd() {
		return soCccd;
	}

	/**
	 * Cập nhật giá trị cho soCccd.
	 */
	public void setSoCccd(String soCccd) {
		this.soCccd = soCccd;
	}

	/**
	 * Lấy giá trị của soDienThoai.
	 */
	public String getSoDienThoai() {
		return soDienThoai;
	}

	/**
	 * Cập nhật giá trị cho soDienThoai.
	 */
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	/**
	 * Lấy giá trị của email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Cập nhật giá trị cho email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Lấy giá trị của diaChiThuongTru.
	 */
	public String getDiaChiThuongTru() {
		return diaChiThuongTru;
	}

	/**
	 * Cập nhật giá trị cho diaChiThuongTru.
	 */
	public void setDiaChiThuongTru(String diaChiThuongTru) {
		this.diaChiThuongTru = diaChiThuongTru;
	}

	/**
	 * Lấy giá trị của tenNganHang.
	 */
	public String getTenNganHang() { return tenNganHang; }
	/**
	 * Cập nhật giá trị cho tenNganHang.
	 */
	public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }

	/**
	 * Lấy giá trị của soTaiKhoan.
	 */
	public String getSoTaiKhoan() { return soTaiKhoan; }
	/**
	 * Cập nhật giá trị cho soTaiKhoan.
	 */
	public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }

	/**
	 * Lấy giá trị của chuTaiKhoan.
	 */
	public String getChuTaiKhoan() { return chuTaiKhoan; }
	/**
	 * Cập nhật giá trị cho chuTaiKhoan.
	 */
	public void setChuTaiKhoan(String chuTaiKhoan) { this.chuTaiKhoan = chuTaiKhoan; }
}