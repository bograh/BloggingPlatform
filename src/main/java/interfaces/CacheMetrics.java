package interfaces;

public interface CacheMetrics {
    long getCacheHits();

    long getCacheMisses();

    double getHitRatio();

    String getStatistics();
}
