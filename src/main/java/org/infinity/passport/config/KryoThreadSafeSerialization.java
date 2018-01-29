package org.infinity.passport.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;

import java.lang.reflect.InvocationHandler;
import java.util.Collections;
import java.util.GregorianCalendar;

public class KryoThreadSafeSerialization {

    private volatile static KryoThreadSafeSerialization uniqueInstance;

    // Create a new KryoHolder for each thread
    private final ThreadLocal<KryoHolder> kryoThreadLocal = ThreadLocal
            .withInitial(() -> new KryoHolder(new Kryo()));

    private KryoThreadSafeSerialization() {

    }

    public static KryoThreadSafeSerialization getInstance() {
        if (uniqueInstance == null) {
            synchronized (KryoThreadSafeSerialization.class) {// thread safe
                if (uniqueInstance == null) {
                    uniqueInstance = new KryoThreadSafeSerialization();
                }
            }
        }
        return uniqueInstance;
    }

    public byte[] serialize(Object obj) {
        KryoHolder kryoHolder = kryoThreadLocal.get();
        try {
            /*//clear Output    -->每次调用的时候  重置*/
            kryoHolder.output.clear();
            kryoHolder.kryo.writeClassAndObject(kryoHolder.output, obj);
            /*// 无法避免拷贝~~~*/
            return kryoHolder.output.toBytes();
        } finally {
            kryoHolder.output.close();
            obj = null; //  for gc
        }
    }

    public Object deserialize(byte[] bytes) {
        KryoHolder kryoHolder = kryoThreadLocal.get();
        try {
            /*call it, and then use input object, discard any array*/
            kryoHolder.input.setBuffer(bytes, 0, bytes.length);
            return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
        } finally {
            kryoHolder.input.close();
            bytes = null; //  for gc
        }
    }

    private class KryoHolder {

        private Kryo kryo;
        static final int BUFFER_SIZE = 1024;
        private Output output = new Output(BUFFER_SIZE, -1); //reuse
        private Input input = new Input();

        KryoHolder(Kryo kryo) {
            this.kryo = kryo;
            this.kryo.setReferences(false);
            // register
            this.kryo.register(Collections.singletonList("").getClass(), new ArraysAsListSerializer());
            this.kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
            this.kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
            this.kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
            this.kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
            this.kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
            this.kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
            this.kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
            this.kryo.register(InvocationHandler.class, new JdkProxySerializer());
            this.kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(this.kryo);
            SynchronizedCollectionsSerializer.registerSerializers(this.kryo);
        }
    }
}