package com.accordserver.accessingdatamysql;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class User {

    @Id // main key/id of the table. This both is only for variable "id".
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String password;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected User() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
     */
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, name='%s', password='%s']",
                id, name, password);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
