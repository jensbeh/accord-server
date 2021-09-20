package com.accordserver.accessingdatamysql.messages;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called userRepository).
 */
public interface MessagesRepository extends CrudRepository<Messages, String> {
//    Messages findByName(String name);
//
//    Messages findByUserKey(String userKey);
//
//    Iterable<Messages> findByOnline(boolean isOnline);
}

