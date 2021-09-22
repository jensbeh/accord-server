package com.accordserver.accessingdatamysql.messages;

import org.hibernate.type.BigIntegerType;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called userRepository).
 */
public interface MessagesRepository extends CrudRepository<Messages, String> {
    Messages findBytimestampMessage(long timestamp);

    Iterable<Messages> findByChannelId(int channelId);
//    Messages findByName(String name);
//
//    Messages findByUserKey(String userKey);
//
//    Iterable<Messages> findByOnline(boolean isOnline);
}

