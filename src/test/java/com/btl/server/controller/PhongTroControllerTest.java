package com.btl.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.btl.server.entity.PhongTro;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.HopDongRepository;
import com.btl.server.repository.TaiKhoanRepository;
import com.btl.server.service.PhongTroService;
import com.btl.server.security.PhongTroSecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PhongTroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhongTroService phongTroService;

    @MockBean
    private TaiKhoanRepository taiKhoanRepository;

    @MockBean
    private HopDongRepository hopDongRepository;

    @MockBean(name = "phongTroSecurity")
    private PhongTroSecurityService phongTroSecurity; 

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testThemPhongMoi_Fail_Unauthorized() throws Exception {
        PhongTro phongMoi = new PhongTro();
         mockMvc.perform(post("/api/phong-tro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(phongMoi)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCapNhatPhong_Success_AsOwner() throws Exception {
        Long id = 1L;
        PhongTro existing = new PhongTro();
        existing.setChuTroId(100L);
        
        when(phongTroService.getPhongById(id)).thenReturn(existing);
        when(phongTroSecurity.isOwner(anyLong(), anyString())).thenReturn(true);

        mockMvc.perform(put("/api/phong-tro/" + id)
                .with(user("chu_tro").roles("LANDLORD"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PhongTro())))
                .andExpect(status().isOk());
    }

     @Test
    public void testXoaPhong_Forbidden_WrongOwner() throws Exception {
        Long id = 1L;
        when(phongTroService.getPhongById(id)).thenReturn(new PhongTro());
        
        when(phongTroSecurity.isOwner(anyLong(), anyString())).thenReturn(false);

        mockMvc.perform(delete("/api/phong-tro/" + id)
                .with(user("khach_thue").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testXoaPhong_Success_AsAdmin() throws Exception {
        Long id = 1L;
        when(phongTroService.getPhongById(id)).thenReturn(new PhongTro());
        
        when(phongTroSecurity.isOwner(anyLong(), anyString())).thenReturn(false);

        mockMvc.perform(delete("/api/phong-tro/" + id)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}