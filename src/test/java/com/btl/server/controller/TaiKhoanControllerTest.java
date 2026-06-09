package com.btl.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.repository.PhongTroRepository;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.KhachHangRepository;
import com.btl.server.security.JwtService;
import com.btl.server.service.MailService;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaiKhoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaiKhoanRepository taiKhoanRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PhongTroRepository phongTroRepository;

    @MockBean
    private HopDongRepository hopDongRepository;

    @MockBean
    private KhachHangRepository khachHangRepository;

    @MockBean
    private MailService mailService;

    private String taoAuthJson(String username, String password) {
        return "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
    }

    @Test
    public void testLogin_Success() throws Exception {
        TaiKhoan mockUser = new TaiKhoan();
        mockUser.setUsername("quang_pro");
        mockUser.setPassword("hashed_password");
        mockUser.setRole("ROLE_USER");
        mockUser.setLocked(false);

        when(taiKhoanRepository.findByUsername("quang_pro")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "hashed_password")).thenReturn(true);
        when(jwtService.generateToken("quang_pro", "ROLE_USER")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/tai-khoan/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taoAuthJson("quang_pro", "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công!"));
    }

    @Test
    public void testLogin_Fail_WrongPassword() throws Exception {
        TaiKhoan mockUser = new TaiKhoan();
        mockUser.setUsername("quang_pro");
        mockUser.setPassword("hashed");

        when(taiKhoanRepository.findByUsername("quang_pro")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(false);

        mockMvc.perform(post("/api/tai-khoan/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taoAuthJson("quang_pro", "123456")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sai tên đăng nhập hoặc mật khẩu!"));
    }

    @Test
    public void testLogin_Fail_UserNotFound() throws Exception {
        when(taiKhoanRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(passwordEncoder.matches("123456", "$2a$10$wTf2E/.n./l5.f.P./R7l.y0r.2X/n.O.m.r.y.Q.t.Q.O.m.X.Y.m.C")).thenReturn(false);

        mockMvc.perform(post("/api/tai-khoan/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taoAuthJson("ghost", "123456")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sai tên đăng nhập hoặc mật khẩu!")); 
    }

   @Test
    public void testLayThongTinCaNhan_Success() throws Exception {
       
        TaiKhoan mockUser = new TaiKhoan();
        mockUser.setId(99L);
        mockUser.setUsername("quang_pro");
        mockUser.setRole("ROLE_USER");

        when(taiKhoanRepository.findByUsername("quang_pro")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/tai-khoan/me")
                .with(user("quang_pro").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("quang_pro"))
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    public void testLayDanhSachNguoiDung_AsAdmin_Pagination() throws Exception {
        TaiKhoan tk1 = new TaiKhoan();
        tk1.setId(1L);
        tk1.setRole("ROLE_USER");
        tk1.setUsername("user1");

        Page<TaiKhoan> page = new PageImpl<>(Arrays.asList(tk1));
        when(taiKhoanRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/tai-khoan/admin/danh-sach-tai-khoan")
                .param("page", "0")
                .param("size", "20")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    public void testLayDanhSachNguoiDung_Unauthorized() throws Exception {
       mockMvc.perform(get("/api/tai-khoan/admin/danh-sach-tai-khoan"))
                .andExpect(status().isUnauthorized());
    }
}