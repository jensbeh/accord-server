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

    private String content;
    private String fromUser;
    private String timestampMessage;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Messages() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
     */
    public Messages(String content, String fromUser, String timestampMessage) {
        this.content = content;
        this.fromUser = fromUser;
        this.timestampMessage = timestampMessage;
    }

    @Override
    public String toString() {
        return String.format(
                "Messages[id=%d, content='%s', from='%s', timestamp='%s']",
                id, content, fromUser, timestampMessage);
    }

    /**
     * Getter and Setter are needed to return an auto-generated answer to the Rest-call
     */
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getTimestampMessage() {
        return timestampMessage;
    }

    public void setTimestampMessage(String timestampMessage) {
        this.timestampMessage = timestampMessage;
    }
}
