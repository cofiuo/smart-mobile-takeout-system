package com.go2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Go2ServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Go2ServeApplication.class, args);


        System.out.println("http://localhost:10001/");
        System.out.println("http://localhost:10001/test");
        System.out.println("http://localhost:10001/category/list");
        System.out.println("http://localhost:10001/account/list");
    }

}
