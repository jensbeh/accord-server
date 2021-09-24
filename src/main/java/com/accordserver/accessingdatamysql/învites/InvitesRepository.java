package com.accordserver.accessingdatamysql.învites;

import com.accordserver.accessingdatamysql.categories.Categories;
import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called invitesRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called userRepository).
 */
public interface InvitesRepository extends CrudRepository<Invites, String> {
    Iterable<Invites> findByServerId(String serverId);
}