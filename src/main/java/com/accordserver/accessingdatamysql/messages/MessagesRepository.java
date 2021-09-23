package com.accordserver.accessingdatamysql.messages;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called messagesRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called messagesRepository).
 */
public interface MessagesRepository extends CrudRepository<Messages, String> {
    Messages findBytimestampMessage(long timestamp);

    Iterable<Messages> findByChannelId(int channelId);
}

