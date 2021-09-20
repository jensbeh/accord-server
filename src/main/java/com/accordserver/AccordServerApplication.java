package com.accordserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccordServerApplication {

//    private static final Logger log = LoggerFactory.getLogger(AccordServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AccordServerApplication.class, args);
        System.out.println("Accord Server started!");
    }

//    @Bean
//    public CommandLineRunner demo(UserRepository repository) {
//        return (args) -> {
//            // save a few customers
////            repository.save(new User("Jack", "Bauer"));
////            repository.save(new User("Chloe", "O'Brian"));
////            repository.save(new User("Kim", "Bauer"));
////            repository.save(new User("David", "Palmer"));
////            repository.save(new User("Michelle", "Dessler"));
//
//            // fetch all users
//            log.info("users found with findAll():");
//            log.info("-------------------------------");
//            for (User user : repository.findAll()) {
//                log.info(user.toString());
//            }
//            log.info("");
//
//            // fetch an individual user by ID
//            User user = repository.findById(1L);
//            log.info("user found with findById(1L):");
//            log.info("--------------------------------");
//            log.info(user.toString());
//            log.info("");
//
//            // fetch users by last name
//            log.info("user found with findByLastName('Bauer'):");
//            log.info("--------------------------------------------");
//            repository.findByLastName("Bauer").forEach(bauer -> {
//                log.info(bauer.toString());
//            });
//            // for (User bauer : repository.findByLastName("Bauer")) {
//            //  log.info(bauer.toString());
//            // }
//            log.info("");
//        };
//    }
}