package org.infinity.passport.config;

import org.infinity.passport.utils.KryoSerializationUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class KryoRedisSerializer<T> implements RedisSerializer<T> {

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        return KryoSerializationUtils.serialize(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return (T) KryoSerializationUtils.deserialize(bytes);
    }
}
