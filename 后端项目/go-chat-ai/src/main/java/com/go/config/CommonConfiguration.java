package com.go.config;
import com.go.tool.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.go.constants.SystemConstants.CUSTOMER_SERVICE_SYSTEM;

@Configuration
public class CommonConfiguration {

    //OpenAiChatModel  模型
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, OrderTools orderTools) {
        return ChatClient.builder(model) // 创建ChatClient工厂
                .defaultSystem(CUSTOMER_SERVICE_SYSTEM)
                .defaultAdvisors(new SimpleLoggerAdvisor())   //日志
                .defaultTools(orderTools)
                .build(); // 构建ChatClient实例

    }
}
