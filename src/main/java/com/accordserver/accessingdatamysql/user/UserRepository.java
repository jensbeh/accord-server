package com.accordserver.accessingdatamysql.user;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called userRepository).
 */
public interface UserRepository extends CrudRepository<User, String> {
    User findByName(String name);

    User findByUserKey(String userKey);

    Iterable<User> findByOnline(boolean isOnline);
}