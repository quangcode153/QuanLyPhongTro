package com.btl.server.dto;

import java.math.BigDecimal;
import java.util.List;

public class ThongKeDTO {
    
    private BigDecimal tongDoanhThuThangNay;
    private BigDecimal tongDoanhThuThangTruoc;
    private Double tyLeTangTruong; 

    private int tongSoPhong;
    private int soPhongDaThue;
    private int soPhongTrong;

    private int soHoaDonChuaThanhToan;
    private BigDecimal tongTienChuaThanhToan;

    private List<BieuDoDoanhThu> bieuDoDoanhThu;

    public static class BieuDoDoanhThu {
        private int thang;
        private int nam;
        private BigDecimal doanhThu;

        /**
         * Khởi tạo dữ liệu biểu đồ doanh thu theo tháng và năm.
         */
        public BieuDoDoanhThu(int thang, int nam, BigDecimal doanhThu) {
            this.thang = thang;
            this.nam = nam;
            this.doanhThu = doanhThu;
        }

        /**
         * Lấy giá trị của thang.
         */
        public int getThang() { return thang; }
        /**
         * Lấy giá trị của nam.
         */
        public int getNam() { return nam; }
        /**
         * Lấy giá trị của doanhThu.
         */
        public BigDecimal getDoanhThu() { return doanhThu; }
    }

    /**
     * Lấy giá trị của tongDoanhThuThangNay.
     */
    public BigDecimal getTongDoanhThuThangNay() { return tongDoanhThuThangNay; }
    /**
     * Cập nhật giá trị cho tongDoanhThuThangNay.
     */
    public void setTongDoanhThuThangNay(BigDecimal tongDoanhThuThangNay) { this.tongDoanhThuThangNay = tongDoanhThuThangNay; }

    /**
     * Lấy giá trị của tongDoanhThuThangTruoc.
     */
    public BigDecimal getTongDoanhThuThangTruoc() { return tongDoanhThuThangTruoc; }
    /**
     * Cập nhật giá trị cho tongDoanhThuThangTruoc.
     */
    public void setTongDoanhThuThangTruoc(BigDecimal tongDoanhThuThangTruoc) { this.tongDoanhThuThangTruoc = tongDoanhThuThangTruoc; }

    /**
     * Lấy giá trị của tyLeTangTruong.
     */
    public Double getTyLeTangTruong() { return tyLeTangTruong; }
    /**
     * Cập nhật giá trị cho tyLeTangTruong.
     */
    public void setTyLeTangTruong(Double tyLeTangTruong) { this.tyLeTangTruong = tyLeTangTruong; }

    /**
     * Lấy giá trị của tongSoPhong.
     */
    public int getTongSoPhong() { return tongSoPhong; }
    /**
     * Cập nhật giá trị cho tongSoPhong.
     */
    public void setTongSoPhong(int tongSoPhong) { this.tongSoPhong = tongSoPhong; }

    /**
     * Lấy giá trị của soPhongDaThue.
     */
    public int getSoPhongDaThue() { return soPhongDaThue; }
    /**
     * Cập nhật giá trị cho soPhongDaThue.
     */
    public void setSoPhongDaThue(int soPhongDaThue) { this.soPhongDaThue = soPhongDaThue; }

    /**
     * Lấy giá trị của soPhongTrong.
     */
    public int getSoPhongTrong() { return soPhongTrong; }
    /**
     * Cập nhật giá trị cho soPhongTrong.
     */
    public void setSoPhongTrong(int soPhongTrong) { this.soPhongTrong = soPhongTrong; }

    /**
     * Lấy giá trị của soHoaDonChuaThanhToan.
     */
    public int getSoHoaDonChuaThanhToan() { return soHoaDonChuaThanhToan; }
    /**
     * Cập nhật giá trị cho soHoaDonChuaThanhToan.
     */
    public void setSoHoaDonChuaThanhToan(int soHoaDonChuaThanhToan) { this.soHoaDonChuaThanhToan = soHoaDonChuaThanhToan; }

    /**
     * Lấy giá trị của tongTienChuaThanhToan.
     */
    public BigDecimal getTongTienChuaThanhToan() { return tongTienChuaThanhToan; }
    /**
     * Cập nhật giá trị cho tongTienChuaThanhToan.
     */
    public void setTongTienChuaThanhToan(BigDecimal tongTienChuaThanhToan) { this.tongTienChuaThanhToan = tongTienChuaThanhToan; }

    /**
     * Lấy giá trị của bieuDoDoanhThu.
     */
    public List<BieuDoDoanhThu> getBieuDoDoanhThu() { return bieuDoDoanhThu; }
    /**
     * Cập nhật giá trị cho bieuDoDoanhThu.
     */
    public void setBieuDoDoanhThu(List<BieuDoDoanhThu> bieuDoDoanhThu) { this.bieuDoDoanhThu = bieuDoDoanhThu; }
}