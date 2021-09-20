package com.accordserver.accessingdatamysql.channels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Channels {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String type;
    private boolean privileged;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Channels() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
     */
    public Channels(String name, String type, boolean privileged) {
        this.name = name;
        this.type = type;
        this.privileged = privileged;
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
    public int getId() {
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
}
