package com.bloodbank.bloodbankdonormanagementsystem.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD Repository interface.
 * Strictly typed with bounded generics to avoid any raw types.
 *
 * @param <T>  The entity type
 * @param <ID> The entity ID type (typically String)
 */
public interface CrudRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean existsById(ID id);

    long count();

    boolean deleteById(ID id);
}