package com.accordserver.accessingdatamysql.învites;

import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.util.HexId;

import javax.persistence.*;

import static com.accordserver.util.Constants.*;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Invites {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String link;
    private String type;
    private int max;
    private int current;

    // server
    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Invites() {
    }

    /**
     * This constructor is the one you used to create instances of Invites to be saved to the database.
     */
    public Invites(String type, Server server) {
        this.id = HexId.generateHexId();
        this.link = REST_SERVER_URL + SERVER_PATH + server.getId() + INVITES_PATH + this.id;
        this.type = type;
        this.server = server;
    }

    @Override
    public String toString() {
        return String.format(
                "Invite[id=%s, name='%s']",
                id, link);
    }

    /**
     * Getter and Setter are needed to return an auto-generated answer to the Rest-call
     */
    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
