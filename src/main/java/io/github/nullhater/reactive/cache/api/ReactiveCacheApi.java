package io.github.nullhater.reactive.cache.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Interface for a reactive cache API based on Project Reactor.
 * Provides reactive methods for cache operations, such as retrieving,
 * storing, and evicting cache entries in a non-blocking manner.
 */
public interface ReactiveCacheApi {


    /**
     * Retrieves a cached value associated with the given key.
     *
     * @param key the key whose associated value is to be returned
     * @return a {@link Mono} emitting the cached value or empty if no value is present
     */
    Mono<Object> get(Object key);

    /**
     * Puts a value into the cache, associated with the given key.
     *
     * @param key   the key to associate with the value
     * @param value the value to cache
     * @return a {@link Mono} signaling when the cache operation is complete
     */
    Mono<Void> put(Object key, Object value);


    /**
     * Evicts a cached value associated with the given key.
     *
     * @param key the key whose associated cache entry is to be removed
     * @return a {@link Mono} signaling when the eviction is complete
     */
    Mono<Void> evict(Object key);

    /**
     * Clears all entries from the cache.
     *
     * @return a {@link Mono} signaling when the cache is cleared
     */
    Mono<Void> clear();

    /**
     * Invalidate the cache through removing all mappings, expecting
     * all entries to be immediately invisible for subsequent lookups.
     *
     * @return a {@link Mono} signaling when the cache is invalidated
     */
    default Mono<Void> invalidate() {
        return clear();
    }

    /**
     * Convenience method to retrieve a cached value as a {@link Mono}.
     *
     * @param key the key whose associated value is to be returned
     * @return a {@link Mono} emitting the cached value or empty if no value is present
     */
    default Mono<Object> getMono(Object key) {
        return get(key);
    }

    /**
     * Retrieves a cached value as a {@link Mono} and casts it to the specified type.
     *
     * @param key   the key whose associated value is to be returned
     * @param clazz the class to cast the cached value to
     * @param <T>   the type of the cached value
     * @return a {@link Mono} emitting the cached value cast to the specified type
     */
    default <T> Mono<T> getMono(Object key, Class<T> clazz) {
        return getMono(key).cast(clazz);
    }

    /**
     * Puts a value into the cache as a {@link Mono} and returns the original value.
     *
     * @param key   the key to associate with the value
     * @param value the value to cache, wrapped in a {@link Mono}
     * @param <T>   the type of the value to cache
     * @return a {@link Mono} emitting the cached value after the operation completes
     */
    default <T> Mono<T> putMono(Object key, Mono<T> value) {
        return value.flatMap(data -> put(key, data).thenReturn(data));
    }

    /**
     * Retrieves a cached value as a {@link Flux} of objects.
     * <p>
     * A well-implemented cache should stream cached data progressively
     * without accumulating objects in memory before sending them to the native cache.
     *
     * @param key the key whose associated value is to be returned
     * @return a {@link Flux} emitting the cached elements, streamed as they are retrieved
     */
    @SuppressWarnings("unchecked")
    default Flux<Object> getFlux(Object key) {
        return get(key).cast(ConcurrentLinkedQueue.class).flatMapIterable(queue -> queue);
    }

    /**
     * Retrieves a cached value as a {@link Flux} and casts its elements to the specified type.
     * <p>
     * A well-implemented cache should stream cached data progressively
     * without accumulating objects in memory before sending them to the native cache.
     *
     * @param key   the key whose associated value is to be returned
     * @param clazz the class to cast the cached elements to
     * @param <T>   the type of the cached elements
     * @return a {@link Flux} emitting the cached elements cast to the specified type
     */
    default <T> Flux<T> getFlux(Object key, Class<T> clazz) {
        return getFlux(key).cast(clazz);
    }

    /**
     * Puts a stream of values into the cache as a {@link Flux}.
     * <p>
     * A well-implemented cache should progressively store data into the cache
     * as it is received from the stream, rather than accumulating all objects
     * before caching them.
     *
     * @param key        the key to associate with the values
     * @param dataStream the stream of values to cache
     * @param <T>        the type of the values to cache
     * @return a {@link Flux} emitting the cached values as they are stored
     */
    default <T> Flux<T> putFlux(Object key, Flux<T> dataStream) {
        Queue<T> queue = new ConcurrentLinkedQueue<>();
        return dataStream.doOnNext(queue::add).concatWith(put(key, queue).thenMany(Flux.empty()));
    }
}
