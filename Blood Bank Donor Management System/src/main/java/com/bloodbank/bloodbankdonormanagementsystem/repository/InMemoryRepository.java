package com.bloodbank.bloodbankdonormanagementsystem.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Generic In-Memory Repository backed by ConcurrentHashMap.
 * Reused for all entities (Donor, BloodUnit, BloodRequest, AppUser).
 * Zero raw types and zero unchecked warnings.
 *
 * @param <T>  The entity type
 * @param <ID> The entity ID type
 */
public class InMemoryRepository<T, ID> implements CrudRepository<T, ID> {

    private final Map<ID, T> storage = new ConcurrentHashMap<>();
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "ID extractor must not be null");
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Entity to save cannot be null");
        ID id = idExtractor.apply(entity);
        Objects.requireNonNull(id, "Extracted entity ID cannot be null");
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }
}