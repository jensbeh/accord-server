package com.accordserver.accessingdatamysql.channels;

import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.messages.Messages;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.util.HexId;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Channels {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String type;
    private boolean privileged;

    // server
    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    // category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    // privileged Member
    @ManyToMany(mappedBy = "privilegedChannel")
    private List<User> privilegedMember = new ArrayList<>();

    // audioMember
    @OneToMany(
            mappedBy = "audioChannel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<User> audioMember = new ArrayList<>();

    // messages
    @LazyCollection(LazyCollectionOption.FALSE)
    // https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl
    @OneToMany(
            mappedBy = "channel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Messages> messages = new ArrayList<>();

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Channels() {
    }

    /**
     * This constructor is the one you used to create instances of Channels to be saved to the database.
     */
    public Channels(String name, String type, boolean privileged, Categories category, Server server) {
        this.id = HexId.generateHexId();
        this.name = name;
        this.type = type;
        this.privileged = privileged;
        this.category = category;
        this.server = server;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', type='%s', privileged='%b']",
                id, name, type, privileged);
    }

    /**
     * Getter and Setter are needed to return an auto-generated answer to the Rest-call
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public List<User> getAudioMember() {
        return audioMember;
    }

    public void setAudioMember(User audioMember) {
        this.audioMember.add(audioMember);
    }

    public List<User> getPrivilegedMember() {
        return privilegedMember;
    }

    public void setPrivilegedMember(User privilegedMember) {
        this.privilegedMember.add(privilegedMember);
    }

    public List<Messages> getMessages() {
        return messages;
    }

    public void setMessages(Messages message) {
        this.messages.add(message);
    }
}
