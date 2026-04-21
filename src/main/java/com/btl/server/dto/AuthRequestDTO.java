package com.btl.server.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

    @NotBlank(message = "Tên đăng nhập không được để trống!")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    private String password;

     private String role; 

     public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}