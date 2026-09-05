package com.search_service.util.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalCacheManager {
    private static final Map<Class<?>, LocalCacheContext<?>> CACHE = new ConcurrentHashMap<>();

    private GlobalCacheManager() {
    }

    @SuppressWarnings("unchecked")
    public static <T> LocalCacheContext<T> getContext(Class<T> targetClass) {
        return (LocalCacheContext<T>) CACHE.computeIfAbsent(targetClass, LocalCacheContext::create);
    }
}