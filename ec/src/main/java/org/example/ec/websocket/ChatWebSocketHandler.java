package org.example.ec.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ec.entity.Messages;
import org.example.ec.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存所有在线用户的连接，key=token，value=连接对象
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // 每个用户对应一把锁，防止同时发消息时撞车
    private static final Map<String, ReentrantLock> sessionLocks = new ConcurrentHashMap<>();

    // 用户建立连接时触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getToken(session);
        if (token != null) {
            sessions.put(token, session);         // 记录连接
            sessionLocks.put(token, new ReentrantLock()); // 分配一把锁
            broadcastOnlineCount();               // 通知所有人在线人数变了
        }
    }

    // 收到用户发来的消息时触发
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String token = getToken(session);
        // 去Redis验证token是否有效
        String userIdStr = redisTemplate.opsForValue().get("token:" + token);
        if (userIdStr == null) {
            session.close(); // token过期就断开连接
            return;
        }

        // 把消息存入数据库
        Messages message = objectMapper.readValue(textMessage.getPayload(), Messages.class);
        message.setCreateTime(LocalDateTime.now());
        messagesService.sendMessage(message);

        // 组装要广播的消息内容
        Map<String, Object> broadcast = Map.of(
                "type",      "message",
                "channelId", message.getChannelId(),
                "author",    message.getAuthor(),
                "content",   message.getContent(),
                "color",     message.getColor(),
                "time",      LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
        );

        // 广播给频道里所有在线用户
        String payload = objectMapper.writeValueAsString(broadcast);
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            safeSend(entry.getKey(), entry.getValue(), payload);
        }
    }

    // 用户断开连接时触发
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String token = getToken(session);
        if (token != null) {
            sessions.remove(token);      // 移除连接
            sessionLocks.remove(token);  // 移除对应的锁
            broadcastOnlineCount();      // 通知所有人在线人数变了
        }
    }

    // 广播在线人数给所有人
    private void broadcastOnlineCount() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "type",  "onlineCount",
                "count", sessions.size()
        ));
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            safeSend(entry.getKey(), entry.getValue(), payload);
        }
    }

    // 加锁发送消息，同一时刻只允许一条消息发给同一个用户，防止撞车
    private void safeSend(String token, WebSocketSession session, String message) {
        ReentrantLock lock = sessionLocks.get(token);
        if (lock == null) return;
        lock.lock(); // 锁上，其他线程等待
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(); // 发完解锁
        }
    }

    // 从连接的URL参数里取出token
    // 例如 ws://localhost:8080/ws/chat?token=xxx，取出xxx
    private String getToken(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("token=")) {
            return query.substring(6);
        }
        return null;
    }
}