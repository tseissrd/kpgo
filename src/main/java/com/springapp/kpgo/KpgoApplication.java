package com.springapp.kpgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KpgoApplication {

    public static void main(String[] args) {
            SpringApplication.run(KpgoApplication.class, args);
    }

}
