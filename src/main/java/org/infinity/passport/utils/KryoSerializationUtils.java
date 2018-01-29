package org.infinity.passport.utils;

import org.infinity.passport.config.KryoThreadSafeSerialization;

public interface KryoSerializationUtils {

    /*将对象序列化为字节数组*/
    static byte[] serialize(Object obj) {
        return KryoThreadSafeSerialization.getInstance().serialize(obj);
    }

    /*将字节数组反序列化为对象*/
    static Object deserialize(byte[] bytes) {
        return KryoThreadSafeSerialization.getInstance().deserialize(bytes);
    }
}