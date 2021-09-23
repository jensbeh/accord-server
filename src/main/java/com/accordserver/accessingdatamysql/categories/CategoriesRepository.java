package com.accordserver.accessingdatamysql.categories;

import org.springframework.data.repository.CrudRepository;

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called categoriesRepository
 * CRUD refers Create, Read, Update, Delete
 * Spring automatically implements this repository interface in a bean that has the same name (with a change in the case - it is called categoriesRepository).
 */
public interface CategoriesRepository extends CrudRepository<Categories, String> {
    Iterable<Categories> findByServerId(int serverId);

    Categories findById(int categoryId);
}

