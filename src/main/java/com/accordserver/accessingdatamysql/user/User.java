package com.accordserver.accessingdatamysql.user;

import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.server.Server;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class User {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String password;
    private boolean online;
    private String description;

    // settings for userKey
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
    private String userKey;

    @ManyToMany
    @JoinTable(
            name = "user_server",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "server_id"))
    private List<Server> servers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "member_server",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "server_id"))
    private List<Server> memberServers = new ArrayList<>();

    // privileged Member / Channel
    @ManyToMany
    @JoinTable(
            name = "user_channel_privileged",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private List<Channels> privilegedChannel = new ArrayList<>();

    // audioMember / Channel
    @ManyToOne
    @JoinColumn(name = "audio_channel_id")
    private Channels audioChannel;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected User() {
    }

    /**
     * This constructor is the one you used to create instances of User to be saved to the database.
     */
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', password='%s']",
                id, name, password);
    }

    /**
     * Getter and Setter are needed to return an auto-generated answer to the Rest-call
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return online;
    }

    public User setOnline(boolean online) {
        this.online = online;
        return this;
    }

    public String getUserKey() {
        return userKey;
    }

    public User setUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServer(Server server) {
        this.servers.add(server);
    }

    public List<Server> getMemberServers() {
        return memberServers;
    }

    public void setMemberServers(Server memberServer) {
        this.memberServers.add(memberServer);
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public Channels getAudioChannel() {
        return audioChannel;
    }

    public void setAudioChannel(Channels audioChannel) {
        this.audioChannel = audioChannel;
    }

    public List<Channels> getPrivilegedChannel() {
        return privilegedChannel;
    }

    public void setPrivilegedChannel(List<Channels> privilegedChannel) {
        this.privilegedChannel = privilegedChannel;
    }
}
