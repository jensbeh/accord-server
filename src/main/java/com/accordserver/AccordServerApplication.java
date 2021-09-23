package com.accordserver;

import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.user.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AccordServerApplication {

    public static void main(String[] args) {
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

        System.out.println("Accord-Server started!");
    }
}