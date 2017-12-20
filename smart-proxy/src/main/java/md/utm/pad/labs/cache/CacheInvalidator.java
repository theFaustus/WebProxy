package md.utm.pad.labs.cache;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Dec, 2017
 */
@Component
public class CacheInvalidator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CacheInvalidator.class);
    private static final int CACHE_INVALIDATION_DELAY = 20;
    private static final double CACHE_INVALIDATION_RATE = .5;

    @Autowired
    private CacheManager cacheManager;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this, CACHE_INVALIDATION_DELAY, CACHE_INVALIDATION_DELAY, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void dispose() {
        executorService.shutdownNow();
    }

    public void run() {
        List<CacheItem> itemsToDelete = Collections.emptyList();
        try {
            LOGGER.info("Invalidating the cache");
            cacheManager.lockCache();
            Map<String, AtomicLong> cacheHits = cacheManager.getCacheHits();
            List<CacheItem> cacheItems = cacheHits.entrySet()
                    .stream()
                    .map(CacheItem::fromMapEntry)
                    .sorted()
                    .collect(Collectors.toList());
            itemsToDelete = makeItemsToDelete(cacheItems);
            cleanCache(itemsToDelete);
        } finally {
            cacheManager.unlockCache();
            LOGGER.info("Cache invalidation finished. Removed items: " + itemsToDelete);
        }
    }

    private void cleanCache(List<CacheItem> cacheItems) {
        for (CacheItem item : cacheItems)
            cacheManager.remove(item.getKey());
    }

    private List<CacheItem> makeItemsToDelete(List<CacheItem> cacheItems) {
        int itemsToDelete = Double.valueOf(CACHE_INVALIDATION_RATE * cacheItems.size()).intValue();
        return itemsToDelete > 0 ? cacheItems.subList(0, itemsToDelete) : Collections.emptyList();
    }

    private static class CacheItem implements Comparable<CacheItem> {
        private final String key;
        private final AtomicLong cacheHits;

        public CacheItem(String key, AtomicLong cacheHits) {
            this.key = key;
            this.cacheHits = cacheHits;
        }

        public static CacheItem fromMapEntry(Map.Entry<String, AtomicLong> entry) {
            return new CacheItem(entry.getKey(), entry.getValue());
        }

        public String getKey() {
            return key;
        }

        public AtomicLong getCacheHits() {
            return cacheHits;
        }

        @Override
        public int compareTo(CacheItem other) {
            return Long.compare(cacheHits.get(), other.cacheHits.get());
        }

        @Override
        public String toString() {
            return "CacheItem{" +
                    "key='" + key + '\'' +
                    ", cacheHits=" + cacheHits +
                    '}';
        }
    }
}
