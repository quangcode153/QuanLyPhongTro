package com.btl.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> xuLyLoiNhapLieu(MethodArgumentNotValidException ex) {
        Map<String, String> danhSachLoi = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String tenTruongBiSai = ((FieldError) error).getField();
            String tinNhanBaoLoi = error.getDefaultMessage();
            danhSachLoi.put(tenTruongBiSai, tinNhanBaoLoi);
        });
        
        return danhSachLoi;
    }
}