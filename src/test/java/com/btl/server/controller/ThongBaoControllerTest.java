package com.btl.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.btl.server.entity.ThongBao;
import com.btl.server.repository.ThongBaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ThongBaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ThongBaoRepository thongBaoRepository;

    @InjectMocks
    private ThongBaoController thongBaoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(thongBaoController).build();
    }

    @Test
    public void testDangThongBao_Success() throws Exception {
        ThongBao inputThongBao = new ThongBao();
        inputThongBao.setTieuDe("Đóng tiền điện");
        inputThongBao.setNoiDung("Các phòng chú ý đóng tiền điện tháng 5 trước ngày 10.");
        inputThongBao.setChuTroId(1L);

        ThongBao savedThongBao = new ThongBao();
        savedThongBao.setId(100L);
        savedThongBao.setTieuDe("Đóng tiền điện");
        savedThongBao.setNoiDung("Các phòng chú ý đóng tiền điện tháng 5 trước ngày 10.");
        savedThongBao.setChuTroId(1L);
        savedThongBao.setNgayDang(LocalDateTime.now());

       when(thongBaoRepository.save(any(ThongBao.class))).thenReturn(savedThongBao);

         mockMvc.perform(post("/api/thong-bao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputThongBao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.tieuDe").value("Đóng tiền điện"))
                .andExpect(jsonPath("$.chuTroId").value(1L));
    }

    @Test
    public void testLayThongBaoTheoChuTro_Success() throws Exception {
        Long chuTroId = 1L;
        ThongBao tb1 = new ThongBao();
        tb1.setId(101L);
        tb1.setTieuDe("Tin 1");
        tb1.setChuTroId(chuTroId);

        ThongBao tb2 = new ThongBao();
        tb2.setId(102L);
        tb2.setTieuDe("Tin 2");
        tb2.setChuTroId(chuTroId);

        List<ThongBao> mockList = Arrays.asList(tb1, tb2);

        when(thongBaoRepository.findByChuTroIdOrderByNgayDangDesc(chuTroId)).thenReturn(mockList);

         mockMvc.perform(get("/api/thong-bao/chu-tro/" + chuTroId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101L))
                .andExpect(jsonPath("$[0].tieuDe").value("Tin 1"))
                .andExpect(jsonPath("$[1].id").value(102L))
                .andExpect(jsonPath("$[1].tieuDe").value("Tin 2"));
    }
}