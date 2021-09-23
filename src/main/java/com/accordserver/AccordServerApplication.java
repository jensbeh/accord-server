package com.accordserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccordServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccordServerApplication.class, args);
        System.out.println("Accord-Server started!");
    }
}