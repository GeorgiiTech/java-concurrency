# Lesson 6: Concurrent Collections: Thread-Safe Data Structures

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Stop wrapping collections in synchronized blocks. Java provides purpose-built concurrent collections that offer better throughput, finer-grained locking, and lock-free algorithms.

## Key Concepts

| Class | Description |
|-------|-------------|
| ConcurrentHashMap | Lock-striped hash map for high-concurrency reads/writes |
| CopyOnWriteArrayList | Thread-safe list: copy-on-write for reads > writes |
| ConcurrentLinkedQueue | Non-blocking FIFO queue using CAS |
| BlockingQueue | Queue with blocking put/take — ideal for producer-consumer |
| ArrayBlockingQueue | Bounded blocking queue backed by an array |
| LinkedBlockingQueue | Optionally bounded blocking queue |
| ConcurrentSkipListMap | Thread-safe sorted map with O(log n) operations |

## What you'll learn

- When to use ConcurrentHashMap vs Collections.synchronizedMap()
- CopyOnWriteArrayList trade-offs
- Producer-consumer with BlockingQueue
- Non-blocking queues and their performance advantages
- Concurrent sorted collections

## Run the code

```bash
javac ConcurrentCollections.java
java ConcurrentCollections
```
