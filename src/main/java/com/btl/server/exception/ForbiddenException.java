package com.btl.server.exception;

public class ForbiddenException extends RuntimeException {
    /**
     * Khởi tạo ngoại lệ truy cập bị từ chối/cấm với thông báo lỗi chi tiết.
     */
    public ForbiddenException(String message) {
        super(message);
    }
}