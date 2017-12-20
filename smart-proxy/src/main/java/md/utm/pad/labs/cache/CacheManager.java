package md.utm.pad.labs.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by anrosca on Dec, 2017
 */
@Component
public class CacheManager {
    private static final String KEY = "CachingHandlerInterceptor";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    private final Map<String, AtomicLong> cacheHits = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void lockCache() {
        lock.lock();
    }

    public void unlockCache() {
        lock.unlock();
    }

    public Map<String, AtomicLong> getCacheHits() {
        return Collections.unmodifiableMap(cacheHits);
    }

    public boolean containsKey(String key) {
        try {
            lock.lock();
            return hashOperations.hasKey(KEY, key);
        } finally {
            lock.unlock();
        }
    }

    public String getValue(String key) {
        try {
            lock.lock();
            if (containsKey(key))
                cacheHits.get(key).incrementAndGet();
            return hashOperations.get(KEY, key);
        } finally {
            lock.unlock();
        }
    }

    public void put(String key, String value) {
        try {
            lock.lock();
            cacheHits.put(key, new AtomicLong());
            hashOperations.put(KEY, key, value);
        } finally {
            lock.unlock();
        }
    }

    public void remove(String key) {
        try {
            lock.lock();
            cacheHits.remove(key);
            hashOperations.delete(KEY, key);
            redisTemplate.delete(key);
        } finally {
            lock.unlock();
        }
    }
}
