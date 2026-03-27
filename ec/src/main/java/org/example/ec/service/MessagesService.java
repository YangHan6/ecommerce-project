package org.example.ec.service;

import org.example.ec.entity.Messages;
import org.example.ec.mapper.MessagesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesService {
    @Autowired
    private MessagesMapper messagesMapper;

    public void sendMessage(Messages message) {
        int result = messagesMapper.insertMessage(message);
        if (result == 0){
            throw new RuntimeException("发送消息失败");
        }
    }

    public List<Messages> getMessages(Integer channelId) {
        List<Messages> message = messagesMapper.getByChannel(channelId);
        return message;
    }
}
