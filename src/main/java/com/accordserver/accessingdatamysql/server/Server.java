package com.accordserver.accessingdatamysql.server;

import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Server {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private int owner;

    // user
    @ManyToMany(mappedBy = "servers")
    private List<User> users = new ArrayList<>();

    // members
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "memberServers",
            cascade = CascadeType.ALL)
    private List<User> members = new ArrayList<>();

    // categories
    @OneToMany(
            mappedBy = "server",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Categories> categories = new ArrayList<>();

    // channels
    @OneToMany(
            mappedBy = "server",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Channels> channels = new ArrayList<>();

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Server() {
    }

    /**
     * This constructor is the one you used to create instances of Server to be saved to the database.
     */
    public Server(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "Server[id=%d, name='%s']",
                id, name);
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

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner() {
        return owner;
    }

    public Server setOwner(int owner) {
        this.owner = owner;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUser(User user) {
        this.users.add(user);
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public Server setCategory(Categories category) {
        this.categories.add(category);
        return this;
    }

    public List<Channels> getChannels() {
        return channels;
    }

    public Server setChannel(Channels channel) {
        this.channels.add(channel);
        return this;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(User member) {
        this.members.add(member);
    }
}
