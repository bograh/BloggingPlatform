package interfaces;

public interface CacheLifecycle {
    void startCleanupTask();

    void shutdown();
}
