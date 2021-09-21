package com.accordserver.accessingdatamysql.channels;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called userRepository).
 */
public interface ChannelsRepository extends CrudRepository<Channels, String> {
    Iterable<Channels> findByCategoryId(int categoryId);

//    Channels findByName(String name);
//
//    Channels findByUserKey(String userKey);
//
//    Iterable<Channels> findByOnline(boolean isOnline);
}

