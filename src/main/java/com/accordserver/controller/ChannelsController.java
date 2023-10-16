package com.accordserver.controller;

import com.accordserver.ResponseMessage;
import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.categories.CategoriesRepository;
import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.channels.ChannelsRepository;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.accessingdatamysql.server.ServerRepository;
import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.user.UserRepository;
import com.accordserver.udpserver.UdpServer;
import com.accordserver.webSocket.SystemWebSocketHandler;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.accordserver.util.Constants.*;

@RestController
public class ChannelsController {

    // This means to get the bean called channelsRepository,... . Which is auto-generated by Spring, we will use it to handle the data
    @Autowired
    private ChannelsRepository channelsRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemWebSocketHandler systemWebSocketHandler;

    private UdpServer udpServer;

    /**
     * creates a new channel
     * WHO CAN DO? -> OWNER
     *
     * @param data     name of the new channel
     * @param userKey  key of the user
     * @param serverId id of the server where the channel should be added
     * @return rest answer
     */
    @PostMapping("/servers/{serverId}/categories/{categoryId}/channels") // Map ONLY POST Requests - createCategory
    public @ResponseBody
    ResponseMessage createChannel(@RequestBody Map<String, Object> data, @RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId) {
        User currentUser = userRepository.findByUserKey(userKey);
        Server currentServer = serverRepository.findById(serverId).get();

        if (currentUser.getId().equals(currentServer.getOwner())) {
            Categories currentCategory = categoriesRepository.findById(categoryId).get();
            // create channel and save it
            Channels newChannel = new Channels(data.get("name").toString(), data.get("type").toString(), (boolean) data.get("privileged"), currentCategory, currentServer);

            currentCategory.setChannel(newChannel);

            categoriesRepository.save(currentCategory);

            // send webSocket message
            systemWebSocketHandler.sendChannelCreated(currentServer, currentCategory, newChannel);

            JsonObject responseData = new JsonObject();
            responseData.put("id", newChannel.getId());
            responseData.put("name", newChannel.getName());
            responseData.put("type", newChannel.getType());
            responseData.put("privileged", newChannel.isPrivileged());
            responseData.put("category", currentCategory.getId());
            responseData.put("members", new JsonArray());
            responseData.put("audioMembers", new JsonArray());

            return new ResponseMessage(SUCCESS, "", responseData);
        } else {
            return new ResponseMessage(FAILED, "Not the correct user or wrong server!", new JsonObject());
        }
    }

    /**
     * Gets all channels of the given category and server
     * WHO CAN DO? -> ALL SERVER USER
     *
     * @param userKey    key of the user
     * @param serverId   id of the server where the channels should be returned
     * @param categoryId id of the category where the channels should be returned
     * @return json list of all channels of the given category
     */
    @GetMapping("/servers/{serverId}/categories/{categoryId}/channels")
    public @ResponseBody
    ResponseMessage getChannels(@RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId) {

        List<Channels> channelList = (List<Channels>) channelsRepository.findByCategoryId(categoryId);

        JsonArray responseChannelDataList = new JsonArray();
        for (Channels channel : channelList) {
            JsonObject responseChannelData = new JsonObject();
            responseChannelData.put("id", channel.getId());
            responseChannelData.put("name", channel.getName());
            responseChannelData.put("type", channel.getType());
            responseChannelData.put("privileged", channel.isPrivileged());
            responseChannelData.put("category", categoryId);

            // add privileged member
            JsonArray jsonArrayPrivilegedMember = new JsonArray();
            for (User user : channel.getPrivilegedMember()) {
                jsonArrayPrivilegedMember.add(user.getId());
            }
            responseChannelData.put("members", jsonArrayPrivilegedMember);

            // add audio member
            JsonArray jsonArrayAudioMember = new JsonArray();
            for (User user : channel.getAudioMember()) {
                jsonArrayAudioMember.add("user.getId()");
            }
            responseChannelData.put("audioMembers", jsonArrayAudioMember);

            responseChannelDataList.add(responseChannelData);
        }

        return new ResponseMessage(SUCCESS, "", responseChannelDataList);
    }

    /**
     * update channel
     * WHO CAN DO? -> ONLY OWNER
     *
     * @param userKey key of the user
     * @return json list of all server
     */
    @PutMapping("/servers/{serverId}/categories/{categoryId}/channels/{channelId}")
    public @ResponseBody
    ResponseMessage updateChannel(@RequestBody Map<String, Object> data, @RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId, @PathVariable("channelId") String channelId) {
        User currentUser = userRepository.findByUserKey(userKey);

        Server currentServer = serverRepository.findById(serverId).get();
        Categories currentCategory = categoriesRepository.findById(categoryId).get();
        Channels currentChannel = channelsRepository.findById(channelId).get();

        if (currentServer.getOwner().equals(currentUser.getId())) {
            String newChannelName = data.get("name").toString();

            // set privileged
            boolean privileged = (boolean) data.get("privileged");
            if (currentChannel.isPrivileged()) {
                JsonArray privilegedMemberIds = (JsonArray) data.get("members");
                System.out.println("privilegedMemberIds: " + privilegedMemberIds);
            }

            // update channel
            currentChannel.setName(newChannelName);
            channelsRepository.save(currentChannel);

            // send webSocket message
            systemWebSocketHandler.sendChannelUpdated(currentServer, currentCategory, currentChannel);

            // return json
            JsonObject responseChannelData = new JsonObject();
            responseChannelData.put("id", currentChannel.getId());
            responseChannelData.put("name", currentChannel.getName());
            responseChannelData.put("type", currentChannel.getType());
            responseChannelData.put("privileged", currentChannel.isPrivileged());
            responseChannelData.put("category", currentCategory.getId());

            // add privileged member
            JsonArray jsonArrayPrivilegedMember = new JsonArray();
            for (User user : currentChannel.getPrivilegedMember()) {
                jsonArrayPrivilegedMember.add(user.getId());
            }
            responseChannelData.put("members", jsonArrayPrivilegedMember);

            // add audio member
            JsonArray jsonArrayAudioMember = new JsonArray();
            for (User user : currentChannel.getAudioMember()) {
                jsonArrayAudioMember.add(user.getId());
            }
            responseChannelData.put("audioMembers", jsonArrayAudioMember);

            return new ResponseMessage(SUCCESS, "", responseChannelData);
        } else {
            return new ResponseMessage(FAILED, "This is not your server!", new JsonObject());
        }
    }

    /**
     * delete whole category
     * WHO CAN DO? -> ONLY OWNER
     *
     * @param userKey key of the user
     * @return json list of all server
     */
    @DeleteMapping("/servers/{serverId}/categories/{categoryId}/channels/{channelId}")
    public @ResponseBody
    ResponseMessage deleteChannel(@RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId, @PathVariable("channelId") String channelId) {
        User currentUser = userRepository.findByUserKey(userKey);

        Server currentServer = serverRepository.findById(serverId).get();
        Categories currentCategory = categoriesRepository.findById(categoryId).get();
        Channels currentChannel = channelsRepository.findById(channelId).get();

        if (currentServer.getOwner().equals(currentUser.getId())) {

            // delete channel
            channelsRepository.delete(currentChannel);

            // send webSocket message
            systemWebSocketHandler.sendChannelDeleted(currentServer, currentCategory, currentChannel, currentUser);

            // return json
            JsonObject channelData = new JsonObject();
            channelData.put("id", currentChannel.getId());
            channelData.put("name", currentChannel.getName());
            channelData.put("type", currentChannel.getType());
            channelData.put("privileged", currentChannel.isPrivileged());
            channelData.put("category", currentCategory.getId());

            // add privileged member
            JsonArray jsonArrayPrivilegedMember = new JsonArray();
            for (User user : currentChannel.getPrivilegedMember()) {
                jsonArrayPrivilegedMember.add(user.getId());
            }
            channelData.put("members", jsonArrayPrivilegedMember);

            channelData.put("audioMembers", new JsonArray());

            return new ResponseMessage(SUCCESS, "", channelData);
        } else {
            return new ResponseMessage(FAILED, "This is not your server!", new JsonObject());
        }
    }

    /**
     * joins an existing audio channel
     * WHO CAN DO? -> ALL SERVER USER
     *
     * @param userKey  key of the user
     * @param serverId id of the server where the channel should be added
     * @return rest answer
     */
    @PostMapping("/servers/{serverId}/categories/{categoryId}/channels/{channelId}/join")
    public @ResponseBody
    ResponseMessage joinAudioChannel(@RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId, @PathVariable("channelId") String channelId) {
        User currentUser = userRepository.findByUserKey(userKey);
        Server currentServer = serverRepository.findById(serverId).get();
        Categories currentCategory = categoriesRepository.findById(categoryId).get();
        Channels currentChannel = channelsRepository.findById(channelId).get();

        // join audio channel and save it
        currentChannel.setAudioMember(currentUser);
        channelsRepository.save(currentChannel);

        // send webSocket message
        systemWebSocketHandler.sendAudioChannelJoined(currentServer, currentCategory, currentChannel, currentUser);

        JsonObject responseData = new JsonObject();
        responseData.put("id", currentChannel.getId());

        // add user to connected user in udp-server
        udpServer.addUdpClient(currentUser.getName());

        return new ResponseMessage(SUCCESS, "", responseData);
    }

    /**
     * leaves an existing audio channel
     * WHO CAN DO? -> ALL SERVER USER
     *
     * @param userKey  key of the user
     * @param serverId id of the server where the channel should be added
     * @return rest answer
     */
    @PostMapping("/servers/{serverId}/categories/{categoryId}/channels/{channelId}/leave")
    public @ResponseBody
    ResponseMessage leaveAudioChannel(@RequestHeader(value = USER_KEY) String userKey, @PathVariable("serverId") String serverId, @PathVariable("categoryId") String categoryId, @PathVariable("channelId") String channelId) {
        User currentUser = userRepository.findByUserKey(userKey);
        Server currentServer = serverRepository.findById(serverId).get();
        Categories currentCategory = categoriesRepository.findById(categoryId).get();
        Channels currentChannel = channelsRepository.findById(channelId).get();

        // leave audio channel and save it

        currentChannel.removeAudioMember(currentUser);
        channelsRepository.save(currentChannel);

        // send webSocket message
        systemWebSocketHandler.sendAudioChannelLeft(currentServer, currentCategory, currentChannel, currentUser);

        JsonObject responseData = new JsonObject();
        responseData.put("id", currentChannel.getId());

        // removes user from connected user in udp-server
        udpServer.removeUdpClient(currentUser.getName(), currentChannel.getId());

        return new ResponseMessage(SUCCESS, "", responseData);
    }

    public void setUdpServer(UdpServer udpServer) {
        this.udpServer = udpServer;
    }
}