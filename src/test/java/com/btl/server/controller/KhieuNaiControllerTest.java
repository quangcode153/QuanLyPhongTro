package com.btl.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.btl.server.entity.KhieuNai;
import com.btl.server.entity.TaiKhoan;
import com.btl.server.repository.KhieuNaiRepository;
import com.btl.server.repository.TaiKhoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class KhieuNaiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KhieuNaiRepository khieuNaiRepository;

    @MockBean
    private TaiKhoanRepository taiKhoanRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

   @Test
    public void testXemDanhSach_Success() throws Exception {
        KhieuNai kn1 = new KhieuNai();
        kn1.setId(1L);
        kn1.setTieuDe("Khiếu nại 1");

        KhieuNai kn2 = new KhieuNai();
        kn2.setId(2L);
        kn2.setTieuDe("Khiếu nại 2");

        List<KhieuNai> mockList = Arrays.asList(kn1, kn2);
        when(khieuNaiRepository.findAllByOrderByThoiGianGuiDesc()).thenReturn(mockList);

        mockMvc.perform(get("/api/khieu-nai")
                .with(user("admin_test").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGuiKhieuNai_Success() throws Exception {
        KhieuNaiController.KhieuNaiRequest requestBody = new KhieuNaiController.KhieuNaiRequest();
        requestBody.setTieuDe("Mất nước");
        requestBody.setNoiDung("Phòng 101 bị mất nước");

        TaiKhoan mockTaiKhoan = new TaiKhoan();
        mockTaiKhoan.setId(10L);
        mockTaiKhoan.setUsername("quang_test");
        when(taiKhoanRepository.findByUsername("quang_test")).thenReturn(Optional.of(mockTaiKhoan));

        KhieuNai savedKn = new KhieuNai();
        savedKn.setId(100L);
        savedKn.setTieuDe("Mất nước");
        savedKn.setTrangThai(KhieuNai.TrangThaiKhieuNai.CHO_XU_LY);
        
        when(khieuNaiRepository.save(any(KhieuNai.class))).thenReturn(savedKn);

        mockMvc.perform(post("/api/khieu-nai")
                .with(user("quang_test").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.tieuDe").value("Mất nước"));
    }

    @Test
    public void testGuiKhieuNai_Fail_NoAuth() throws Exception {
        KhieuNaiController.KhieuNaiRequest requestBody = new KhieuNaiController.KhieuNaiRequest();
        requestBody.setTieuDe("Mất nước");
        requestBody.setNoiDung("Phòng 101");

       mockMvc.perform(post("/api/khieu-nai")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGuiKhieuNai_Fail_UserNotFound() throws Exception {
        when(taiKhoanRepository.findByUsername("unknown_user"))
            .thenReturn(Optional.empty());

        KhieuNaiController.KhieuNaiRequest requestBody = new KhieuNaiController.KhieuNaiRequest();
        requestBody.setTieuDe("Test");
        requestBody.setNoiDung("Test");

        mockMvc.perform(post("/api/khieu-nai")
                .with(user("unknown_user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGuiKhieuNai_Fail_EmptyFields() throws Exception {
        KhieuNaiController.KhieuNaiRequest requestBody = new KhieuNaiController.KhieuNaiRequest();
        requestBody.setTieuDe(""); 
        requestBody.setNoiDung("");

        mockMvc.perform(post("/api/khieu-nai")
                .with(user("quang_test").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDanhDauDaXuLy_Success() throws Exception {
        Long khieuNaiId = 1L;

        KhieuNai existingKn = new KhieuNai();
        existingKn.setId(khieuNaiId);
        existingKn.setTrangThai(KhieuNai.TrangThaiKhieuNai.CHO_XU_LY);
        when(khieuNaiRepository.findById(khieuNaiId)).thenReturn(Optional.of(existingKn));

        KhieuNai updatedKn = new KhieuNai();
        updatedKn.setId(khieuNaiId);
        updatedKn.setTrangThai(KhieuNai.TrangThaiKhieuNai.DA_GIAI_QUYET);
        when(khieuNaiRepository.save(any(KhieuNai.class))).thenReturn(updatedKn);

        mockMvc.perform(put("/api/khieu-nai/" + khieuNaiId + "/xu-ly")
                .with(user("admin_test").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(khieuNaiId))
                .andExpect(jsonPath("$.trangThai").value("DA_GIAI_QUYET"));
    }

    @Test
    public void testDanhDauDaXuLy_Fail_WrongRole() throws Exception {
        Long khieuNaiId = 1L;

        mockMvc.perform(put("/api/khieu-nai/" + khieuNaiId + "/xu-ly")
                .with(user("quang_test").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}