package com.btl.server.controller;

import com.btl.server.entity.TinNhan;
import com.btl.server.repository.TinNhanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private TinNhanRepository tinNhanRepository;

    @InjectMocks
    private ChatController chatController;

    private TinNhan tinNhanTest;

    @BeforeEach
    void setUp() {
        tinNhanTest = new TinNhan();
        tinNhanTest.setId(1);
        tinNhanTest.setNguoiGuiId(10);
        tinNhanTest.setNguoiNhanId(20);
        tinNhanTest.setNoiDung("Xin chao");
    }

    @Test
    void testLayLichSuChat() {
        List<TinNhan> danhSachMock = Arrays.asList(tinNhanTest);
        when(tinNhanRepository.timLichSuChat(10, 20)).thenReturn(danhSachMock);

        List<TinNhan> ketQua = chatController.layLichSuChat(10, 20);

        assertEquals(1, ketQua.size());
        assertEquals("Xin chao", ketQua.get(0).getNoiDung());
        verify(tinNhanRepository).timLichSuChat(10, 20);
    }

    @Test
    void testXuLyTinNhan() {
        chatController.xuLyTinNhan(tinNhanTest);

        assertNotNull(tinNhanTest.getThoiGian());
        verify(tinNhanRepository).save(tinNhanTest);
        verify(messagingTemplate).convertAndSend("/topic/chat/20", tinNhanTest);
        verify(messagingTemplate).convertAndSend("/topic/chat/10", tinNhanTest);
    }
}