package com.accordserver.accessingdatamysql.messages;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Messages {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String text;
    private String from;
    private String timestamp;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Messages() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
     */
    public Messages(String text, String from, String timestamp) {
        this.text = text;
        this.from = from;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, text='%s', from='%s', timestamp='%s']",
                id, text, from, timestamp);
    }

    /**
     * Getter and Setter are needed to return an auto-generated answer to the Rest-call
     */
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
