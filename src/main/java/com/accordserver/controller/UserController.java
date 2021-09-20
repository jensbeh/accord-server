package com.accordserver.controller;

import com.accordserver.accessingdatamysql.User;
import com.accordserver.accessingdatamysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    /**
     * SignIn a new user
     *
     * @param name     name of the user
     * @param password password of the user
     * @return state
     */

    @PostMapping("/users") // Map ONLY POST Requests - SignIn
    public @ResponseBody
    String signIn(@RequestHeader(value = "name") String name,
                  @RequestHeader(value = "password") String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        if (userRepository.findByName(name) == null) {
            User user = new User(name, password);
            userRepository.save(user);
            return "Saved";
        } else {
            return "Already registered";
        }
    }

    /**
     * Gets all registered users
     *
     * @return json list of users
     */
//    @GetMapping("/users")
//    public ResponseEntity<String> getUsers(@RequestHeader(value = "userKey") String userKey) {
//        return new ResponseEntity<String>(userKey + " userKey", HttpStatus.OK);
//    }
    @GetMapping("/users")
    public @ResponseBody
    Iterable<User> getUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    /**
     * login with an available user
     *
     * @param name     name of the user
     * @param password password of the user
     * @return state
     */
    @PostMapping("/users/login")
    public ResponseEntity<String> login(@RequestHeader(value = "name") String name,
                                        @RequestHeader(value = "password") String password) {
        return new ResponseEntity<String>(name + " " + password + " login", HttpStatus.OK);
    }
}