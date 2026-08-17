package com.fabio.perfumeshop_api.chat.internal;


import com.fabio.perfumeshop_api.chat.internal.dto.ChatRequest;
import com.fabio.perfumeshop_api.chat.internal.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @PostMapping
    ChatResponse chat(@RequestBody ChatRequest request){
        return chatService.chat(request.messages());

    }

}
