package com.accordserver.accessingdatamysql.server;

import com.accordserver.accessingdatamysql.categories.Categories;
import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.user.User;
import com.accordserver.accessingdatamysql.învites.Invites;
import com.accordserver.util.HexId;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Server {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String owner;

    // user
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(
            name = "user_server",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

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

    // invites
    @OneToMany(
            mappedBy = "server",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Invites> invites = new ArrayList<>();

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Server() {
    }

    /**
     * This constructor is the one you used to create instances of Server to be saved to the database.
     */
    public Server(String name) {
        this.id = HexId.generateHexId();
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "Server[id=%s, name='%s']",
                id, name);
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

    public String getOwner() {
        return owner;
    }

    public Server setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public Set<User> getUsers() {
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

    public List<Invites> getInvites() {
        return invites;
    }

    public void setInvites(Invites invite) {
        this.invites.add(invite);
    }
}
