package org.infinity.passport.service.impl.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

@Deprecated
public abstract class MongoBaseCrudServiceImpl<T, ID extends Serializable> implements MongoCrudService<T, ID> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    private Class<T>        modelClass;

    @SuppressWarnings("unchecked")
    public MongoBaseCrudServiceImpl() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        Type[] typeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        this.modelClass = (Class<T>) typeArguments[0];
    }

    @Override
    public boolean exists(ID id) {
        Objects.requireNonNull(id, "The given id must not be null!");
        Object result = mongoTemplate.findById(id, this.modelClass);
        return result != null ? true : false;
    }

    @Override
    public long count() {
        return mongoTemplate.count(null, this.modelClass);
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, this.modelClass);
    }

    @Override
    public long count(T entity) {
        if (entity == null) {
            return 0L;
        }
        return this.count(new Query(Criteria.byExample(entity)));
    }

    @Override
    public T findOne(ID id) {
        return mongoTemplate.findById(id, this.modelClass);
    }

    @Override
    public Optional<T> findOne(Query query) {
        List<T> list = this.find(query);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return Optional.of(list.get(0));
    }

    @Override
    public Optional<T> findOne(T entity) {
        if (entity == null) {
            return null;
        }
        return this.findOne(new Query(Criteria.byExample(entity)));
    }

    @Override
    public List<T> findAll() {
        return mongoTemplate.findAll(this.modelClass);
    }

    @Override
    public Page<T> find(Pageable pageable) {
        List<T> list = mongoTemplate.find(new Query().with(pageable), this.modelClass);
        return new PageImpl<T>(list, pageable, this.count());
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, this.modelClass);
    }

    @Override
    public List<T> find(T entity) {
        if (entity == null) {
            return null;
        }
        return this.find(new Query(Criteria.byExample(entity)));
    }

    @Override
    public Page<T> find(Query query, Pageable pageable) {
        query.with(pageable);
        List<T> list = this.find(query);
        return new PageImpl<T>(list, pageable, this.count(query));
    }

    @Override
    public Page<T> find(T entity, Pageable pageable) {
        if (entity == null) {
            return null;
        }
        return this.find(new Query(Criteria.byExample(entity)), pageable);
    }

    @Override
    public T insert(T entity) {
        Objects.requireNonNull(entity, "Entity must not be null!");
        mongoTemplate.insert(entity);
        return entity;
    }

    @Override
    public List<T> insertAll(List<T> list) {
        Objects.requireNonNull(list, "The given Iterable of entities not be null!");
        if (list.isEmpty()) {
            return list;
        }
        mongoTemplate.insertAll(list);
        return list;
    }

    @Override
    public T update(T entity) {
        mongoTemplate.save(entity);
        return entity;
    }

    @Override
    public List<T> updateAll(List<T> list) {
        Objects.requireNonNull(list, "The given Iterable of entities not be null!");
        for (T entity : list) {
            update(entity);
        }
        return list;
    }

    @Override
    public void delete(ID id) {
        mongoTemplate.remove(findOne(id));
    }

    @Override
    public void delete(T entity) {
        mongoTemplate.remove(entity);
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(new Query(), this.modelClass);
    }
}