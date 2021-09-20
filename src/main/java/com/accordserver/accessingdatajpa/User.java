package com.restserveraccord.accessingdatajpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // The User class is annotated with @Entity, indicating that it is a JPA entity. It is assumed that this entity is mapped to a table named "User".
public class User {

    @Id // main key/id of the table. This both is only for variable "id".
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;

    /**
     * The default constructor exists only for the sake of JPA. You do not use it directly, so it is designated as protected.
     */
    protected User() {
    }

    /**
     * This constructor is the one you used to create instances of Customer to be saved to the database.
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
