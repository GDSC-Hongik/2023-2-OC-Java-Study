# [Ch5] Building Blocks

## 5.2 Concurrent collections

- designed for concurrent access from multiple threads
    - scalability↑ risk↓
- `ConcurrentHashMap` : replacement for synchronized hash-based Map
- `CopyOnWriteArrayList` : replacement for synchronized List
- `Queue` : hold elements while await processing
    - do not block : if empty → returns `null`
    - `ConcurrentLinkedQueue` : traditional FIFO queue
    - `PriorityQueue` : (non concurrent) priority ordered queue
- `BlockingQueue`
    - blocking insertion
        - if empty → retrieval blocks until available
        - if full → insertion blocks until space available
    - useful in producer-consumer designs

### 5.2.1 ConcurrentHashMap

- hash-based Map like `HashMap`
- `lock striping` : allow a greater degree of shared access
    - throughput↑
    - performance penalty↓ for single-thread access
- provide `iterators` that do not throw ConcurrentModificationException
    - eliminate the need to lock collection during iteration
    - `weakly consistent` : tolerate concurrent modification
- semantics of methods that operate on entire Map → slightly `weakened`
    - size → could be out of date
    - moving targets
    - enable performance optimizations for important operations
        - get, put, containsKey, remove
- `cannot be locked` for exclusive access
    - only offered by the synchronized Map
    - reasonable tradeoff → expected to change contents continuously

### 5.2.2 Additional atomic Map operations

- cannot use client-side locking
    - create new `atomic` operations : put-if-absent
- `ConcurrentMap` interface offers common compound operations
    - `put-if-absent`
    - `remove-if-equal`
    - `replace-if-equal`

### 5.2.3 CopyOnWriteArrayList

- concurrent replacement for synchronized `List`
- concurrency↑
- eliminate need to lock or copy collection during iteration
- implement mutability ← create & republish `new copy` of collection every time when modified

```java
public interface ConcurrentMap<K,V> extends Map<K,V> {
	// Insert into map only if no value is mapped from K
	V putIfAbsent(K key, V value);

	// Remove only if K is mapped to V
	boolean remove(K key, V value);

	// Replace value only if K is mapped to oldValue
	boolean replace(K key, V oldValue, V newValue);

	// Replace value only if K is mapped to some value
	V replace (K key, V newValue);
}
```

- multiple threads iterate collection without interference from one another
    - iterators do **not** throw `ConcurrentModificationException`
    - return elements exactly as they were at the time
- **cost** to copying the backing array every time
    - when collection is large
- reasonable to use only when `iteration` is common than modification