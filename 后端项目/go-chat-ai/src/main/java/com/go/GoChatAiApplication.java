package com.go;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.go.mapper")
public class GoChatAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoChatAiApplication.class, args);
    }

}


