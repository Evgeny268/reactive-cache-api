# reactive-cache-api

`reactive-cache-api` is a lightweight, flexible API designed for implementing reactive, non-blocking caching solutions using [Project Reactor](https://projectreactor.io/). The library provides an abstraction over reactive caching operations such as retrieving, storing, and evicting data, allowing developers to easily integrate reactive caching mechanisms into their applications without worrying about low-level implementation details.

## Key Features

- **Non-blocking API**: Built on top of Project Reactor's `Mono` and `Flux`, providing fully asynchronous and non-blocking operations.
- **Reactive CRUD operations**: Methods for getting, putting, evicting, and clearing cached entries in a reactive manner.
- **Flux and Mono support**: Retrieve and store cache entries using both single (`Mono`) and stream-based (`Flux`) operations, making it suitable for a wide variety of use cases.
- **Extendable and flexible**: The library is designed to provide a standard interface for caching, allowing easy customization and extension depending on the underlying cache technology (e.g., Redis, in-memory caches, etc.).

## API Overview

The core interface is `ReactiveCacheApi`, which defines the following operations:

- `get(key)`: Retrieves a cached value as a `Mono`.
- `put(key, value)`: Stores a value in the cache, returning a `Mono` to signal completion.
- `evict(key)`: Removes a cached entry, if present.
- `clear()`: Clears all cache entries.
- `getFlux(key)`: Retrieves a stream of cached values as a `Flux`.
- `putFlux(key, dataStream)`: Stores a stream of values into the cache.

## Example Usage

```java
ReactiveCacheApi cache = new InMemoryReactiveCache(); // Hypothetical implementation

// Storing a value in the cache
cache.put("key1", "value1").subscribe();

// Retrieving a value
cache.get("key1").subscribe(value -> System.out.println("Cached value: " + value));

// Working with Flux
Flux<String> dataStream = Flux.just("item1", "item2", "item3");
cache.putFlux("key2", dataStream).subscribe();
```
## Installation
Add the following Maven dependency to your project:
```xml
<dependency>
    <groupId>io.github.nullhater</groupId>
    <artifactId>reactive-cache-api</artifactId>
    <version>0.1.1</version>
</dependency>
```

## Custom Implementations
You can implement [ReactiveCacheApi](src/main/java/io/github/nullhater/reactive/cache/api/ReactiveCacheApi.java) with any caching technology you prefer. The API is designed to be extendable, so you can integrate with Redis, Hazelcast, or in-memory caching mechanisms easily.

For example, an in-memory cache could be implemented using a ConcurrentHashMap:
```java
public class InMemoryReactiveCache implements ReactiveCacheApi {
    private final Map<Object, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Mono<Object> get(Object key) {
        return Mono.justOrEmpty(cache.get(key));
    }

    @Override
    public Mono<Void> put(Object key, Object value) {
        cache.put(key, value);
        return Mono.empty();
    }

    @Override
    public Mono<Void> evict(Object key) {
        cache.remove(key);
        return Mono.empty();
    }

    @Override
    public Mono<Void> clear() {
        cache.clear();
        return Mono.empty();
    }
}
```

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.