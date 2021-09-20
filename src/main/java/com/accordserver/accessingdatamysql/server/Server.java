package com.accordserver.accessingdatamysql.server;

import com.accordserver.accessingdatamysql.user.User;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String owner;

    @ManyToMany(mappedBy = "servers")/*(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "servers")*/
    private List<User> users = new ArrayList<>();

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Server() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
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

    public String getOwner() {
        return owner;
    }

    public Server setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUser(User user) {
        this.users.add(user);
    }
}
