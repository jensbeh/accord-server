package com.accordserver.accessingdatamysql.categories;

import com.accordserver.accessingdatamysql.channels.Channels;
import com.accordserver.accessingdatamysql.server.Server;
import com.accordserver.util.HexId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// @Entity tells Hibernate to make a table out of this class. Hibernate automatically translates the entity into a table.
public class Categories {
    // main key/id of the table. This both is only for variable "id".
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    // server
    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    // channels
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Channels> channels = new ArrayList<>();

    /**
     * The default constructor exists only for the sake of JPA/MySQL. You do not use it directly, so it is designated as protected.
     */
    protected Categories() {
    }

    /**
     * This constructor is the one you used to create instances of Categories to be saved to the database.
     */
    public Categories(String name, Server server) {
        this.id = HexId.generateHexId();
        this.name = name;
        this.server = server;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s']",
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

    public List<Channels> getChannels() {
        return channels;
    }

    public Categories setChannel(Channels channel) {
        this.channels.add(channel);
        return this;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
