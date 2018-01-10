package org.infinity.passport.service.impl.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

@Deprecated
public interface MongoCrudService<T, ID extends Serializable> {

    boolean exists(ID id);

    long count();

    long count(Query query);

    long count(T entity);

    T findOne(ID id);

    Optional<T> findOne(Query query);

    Optional<T> findOne(T entity);

    List<T> findAll();

    Page<T> find(Pageable pageable);

    List<T> find(Query query);

    List<T> find(T entity);

    Page<T> find(Query query, Pageable pageable);

    Page<T> find(T entity, Pageable pageable);

    T insert(T entity);

    List<T> insertAll(List<T> list);

    T update(T entity);

    List<T> updateAll(List<T> list);

    void delete(ID id);

    void delete(T entity);

    void deleteAll();
}
