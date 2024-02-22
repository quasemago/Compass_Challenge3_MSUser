package com.compassuol.sp.challenge.msuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MSUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MSUserApplication.class, args);
    }

}
