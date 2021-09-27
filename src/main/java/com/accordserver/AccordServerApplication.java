package com.accordserver;

import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.user.UserRepository;
import com.accordserver.udpserver.UdpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AccordServerApplication {

    public static void main(String[] args) {
        // start the SpringApplication with Rest-API and WebSockets
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(AccordServerApplication.class, args);

        // set all users offline
        UserRepository userRepository = configurableApplicationContext.getBean(UserRepository.class);
        for (User user : userRepository.findAll()) {
            if (user.isOnline()) {
                user.setOnline(false);
                user.setUserKey(null);
                userRepository.save(user);
            }
        }

        // start the udpServer to route the audio packets
        UdpServer udpServer = new UdpServer();

        System.out.println("Accord-Server started!");
    }
}