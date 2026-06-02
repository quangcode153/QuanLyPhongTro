package com.btl.server.controller;

import com.btl.server.entity.TinNhan;
import com.btl.server.repository.TinNhanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller điều phối tin nhắn trò chuyện trực tiếp (Real-time Chat) giữa Khách thuê và Chủ trọ.
 * Kết hợp REST API truyền thống để lấy lịch sử chat và giao thức WebSocket để đẩy tin nhắn tức thời.
 */
@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TinNhanRepository tinNhanRepository;

    /**
     * REST API lấy toàn bộ lịch sử tin nhắn trò chuyện qua lại giữa hai người dùng.
     * @param user1 ID người thứ nhất
     * @param user2 ID người thứ hai
     */
    @GetMapping("/api/tin-nhan/{user1}/{user2}")
    public List<TinNhan> layLichSuChat(@PathVariable Long user1, @PathVariable Long user2) {
        return tinNhanRepository.timLichSuChat(user1, user2);
    }

    /**
     * WebSocket Message Endpoint (`/chat.send`) nhận tin nhắn thời gian thực từ phía Client gửi lên.
     * Lưu tin nhắn vào CSDL và đồng thời phát trực tiếp (broadcast) tới kênh đăng ký của cả Người gửi và Người nhận.
     */
    @MessageMapping("/chat.send")
    public void xuLyTinNhan(TinNhan tinNhan) {
        tinNhan.setThoiGian(LocalDateTime.now());
        tinNhanRepository.save(tinNhan);

        // Đẩy tin nhắn tức thời tới kênh WebSocket của người nhận và người gửi để đồng bộ giao diện Chat
        messagingTemplate.convertAndSend("/topic/chat/" + tinNhan.getNguoiNhanId(), tinNhan);
        messagingTemplate.convertAndSend("/topic/chat/" + tinNhan.getNguoiGuiId(), tinNhan);
    }
}