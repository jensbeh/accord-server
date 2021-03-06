package com.accordserver.controller;

import com.accordserver.ResponseMessage;
import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.user.UserRepository;
import com.accordserver.util.LoginForm;
import com.accordserver.webSocket.SystemWebSocketHandler;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.accordserver.util.Constants.*;

@RestController
public class UserController {

    // This means to get the bean called userRepository,... . Which is auto-generated by Spring, we will use it to handle the data
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemWebSocketHandler systemWebSocketHandler;

    /**
     * SignIn a new user
     *
     * @param loginForm name and password of the user
     * @return rest answer
     */

    @PostMapping("/users") // Map ONLY POST Requests - SignIn
    public @ResponseBody
    ResponseMessage signIn(@RequestBody LoginForm loginForm) {
        if (userRepository.findByName(loginForm.getName()) == null) {
            User user = new User(loginForm.getName(), loginForm.getPassword()).setOnline(false).setDescription("");
            userRepository.save(user);
            return new ResponseMessage(SUCCESS, "User created", new JsonObject());
        } else {
            return new ResponseMessage(FAILED, "User already registered", new JsonObject());
        }
    }

    /**
     * Gets all registered users
     *
     * @param userKey key of the currentUser
     * @return json list of users
     */
    @GetMapping("/users")
    public @ResponseBody
    ResponseMessage getUsers(@RequestHeader(value = USER_KEY) String userKey) {
        List<User> onlineUsers = (List<User>) userRepository.findByOnline(true);

        for (User user : onlineUsers) {
            if (user.getUserKey().equals(userKey)) {

                JsonArray onlineUserData = new JsonArray();
                for (User cleanUser : onlineUsers) {
                    JsonObject newUserData = new JsonObject();
                    newUserData.put("id", cleanUser.getId());
                    newUserData.put("name", cleanUser.getName());
                    newUserData.put("description", cleanUser.getDescription());
                    onlineUserData.add(newUserData);
                }

                return new ResponseMessage(SUCCESS, "", onlineUserData);
            }
        }
        return new ResponseMessage(FAILED, "UserKey is not valid!", new JsonObject());
    }

    /**
     * login with an available user
     *
     * @param loginForm name and password of the user
     * @return rest answer
     */
    @PostMapping("/users/login")
    public ResponseMessage login(@RequestBody LoginForm loginForm) {
        User user = userRepository.findByName(loginForm.getName());

        if (user != null) {
            if (user.getPassword().equals(loginForm.getPassword())) {
                UUID userKeyUuid = UUID.randomUUID();
                String userKeyString = userKeyUuid.toString();

                user.setOnline(true).setUserKey(userKeyString);
                userRepository.save(user);

                JsonObject data = new JsonObject();
                data.put(USER_KEY, userKeyString);

                // WebSocket userJoined
                systemWebSocketHandler.sendUserJoined(user);

                return new ResponseMessage(SUCCESS, loginForm.getName() + " logged in!", data);
            } else {
                return new ResponseMessage(FAILED, "Wrong user-password!", new JsonObject());
            }
        } else {
            return new ResponseMessage(FAILED, loginForm.getName() + " can't logged in!", new JsonObject());
        }
    }

    /**
     * logout with an available user
     *
     * @param userKey userKey of the user
     * @return rest answer
     */
    @PostMapping("/users/logout")
    public ResponseMessage logout(@RequestHeader(value = USER_KEY) String userKey) {
        User user = userRepository.findByUserKey(userKey);

        if (user != null) {
            if (user.isOnline()) {
                user.setOnline(false).setUserKey(null);
                userRepository.save(user);

                // WebSocket userJoined
                systemWebSocketHandler.sendUserLeft(user);

                return new ResponseMessage(SUCCESS, "Logged out", new JsonObject());
            } else {
                return new ResponseMessage(FAILED, "User is offline!", new JsonObject());
            }
        } else {
            return new ResponseMessage(FAILED, "You can't logged out!", new JsonObject());
        }
    }
}