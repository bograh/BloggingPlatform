package services;

import interfaces.Cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory cache implementation using ConcurrentHashMap.
 * Tracks cache hits and misses for performance monitoring.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class CacheService<K, V> implements Cache<K, V> {

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, Long> expirationTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final long ttlMs;
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicBoolean cleanupStarted = new AtomicBoolean(false);


    public CacheService(long ttlMs) {
        this.ttlMs = ttlMs;
    }

    @Override
    public boolean set(K key, V value) {
        if (key == null || value == null)
            return false;

        cache.put(key, value);
        expirationTimes.put(key, System.currentTimeMillis() + ttlMs);
        return true;
    }

    @Override
    public Optional<V> get(K key) {
        if (key == null) {
            cacheMisses.incrementAndGet();
            return Optional.empty();
        }

        if (isExpired(key)) {
            cache.remove(key);
            expirationTimes.remove(key);
            cacheMisses.incrementAndGet();
            return Optional.empty();
        }

        V value = cache.get(key);
        if (value != null) {
            cacheHits.incrementAndGet();
            return Optional.of(value);
        }

        cacheMisses.incrementAndGet();
        return Optional.empty();
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
        expirationTimes.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public void clear() {
        cache.clear();
        expirationTimes.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
    }

    public long getCacheHits() {
        return cacheHits.get();
    }

    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public double getHitRatio() {
        long total = cacheHits.get() + cacheMisses.get();
        return total == 0 ? 0.0 : (double) cacheHits.get() / total;
    }

    public String getStatistics() {
        return String.format(
                "Cache Statistics: Size=%d, Hits=%d, Misses=%d, Hit Ratio=%.2f%%",
                size(), cacheHits.get(), cacheMisses.get(), getHitRatio() * 100);
    }

    private boolean isExpired(K key) {
        Long expirationTime = expirationTimes.get(key);
        return expirationTime == null || System.currentTimeMillis() > expirationTime;
    }

    private void cleanupExpiredEntries() {
        for (K key : cache.keySet()) {
            if (isExpired(key)) {
                remove(key);
            }
        }
    }

    public void startCleanupTask() {
        if (cleanupStarted.compareAndSet(false, true)) {
            executor.scheduleAtFixedRate(
                    this::cleanupExpiredEntries,
                    ttlMs,
                    Math.max(1, ttlMs / 2),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

}