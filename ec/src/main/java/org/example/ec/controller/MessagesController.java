package org.example.ec.controller;

import common.Result;
import org.example.ec.entity.Messages;
import org.example.ec.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    @GetMapping("/messages/{channelId}")
    public Result<List<Messages>> findAll(@PathVariable Integer channelId) {
        List<Messages> message = messagesService.getMessages(channelId);
        return Result.success("success", message);
    }
}
