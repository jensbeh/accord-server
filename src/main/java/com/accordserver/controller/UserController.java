package com.restserveraccord.controller;

import com.restserveraccord.representations.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @PostMapping("/users")
    public User signIn(@RequestHeader(value = "name") String name,
                       @RequestHeader(value = "password") String password) {
        return new User(name + " signIn", password);
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers(@RequestHeader(value = "userKey") String userKey) {
        return new ResponseEntity<String>(userKey + " userKey", HttpStatus.OK);
    }

    @PostMapping("/users/login")
    public User login(@RequestHeader(value = "name") String name,
                      @RequestHeader(value = "password") String password) {
        return new User(name + " login", password);
    }
}