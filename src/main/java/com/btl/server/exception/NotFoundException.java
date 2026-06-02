package com.btl.server.exception;

public class NotFoundException extends RuntimeException {
    /**
     * Khởi tạo ngoại lệ không tìm thấy tài nguyên với thông báo lỗi chi tiết.
     */
    public NotFoundException(String message) {
        super(message);
    }
}