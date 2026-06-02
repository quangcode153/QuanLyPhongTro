package com.btl.server.exception;

public class BadRequestException extends RuntimeException {
    /**
     * Khởi tạo ngoại lệ yêu cầu không hợp lệ với thông báo lỗi chi tiết.
     */
    public BadRequestException(String message) {
        super(message);
    }
}