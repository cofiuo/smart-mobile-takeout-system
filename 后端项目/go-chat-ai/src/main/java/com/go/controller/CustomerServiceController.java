package com.go.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class CustomerServiceController {

    private final ChatClient chatClient;

    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public String chat(@RequestBody Map<String,String> request){
        String userMessage = request.get("message");
        return chatClient.prompt(userMessage)  //传递用户 提示词（问题）
                .call()   //同步请求AI，等待AI全部输出完毕才返回结果。
                .content();   //返回响应的内容
    }
}
