package com.example._025021238heyinlin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example._025021238heyinlin")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
