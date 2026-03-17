# Lesson 11: Final Project – Concurrent Task Processor

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Put everything together! In this final project you build a real-world **Concurrent Task Processor** that combines thread pools, CompletableFuture pipelines, concurrent collections, atomic counters, virtual threads, and structured error handling into one cohesive application.

## Key Concepts

| Concept | Description |
|---------|-------------|
| ExecutorService | Thread-pool that drives the processing pipeline |
| CompletableFuture | Async pipeline: fetch → process → store |
| ConcurrentHashMap | Thread-safe result registry |
| AtomicLong | Lock-free counters for metrics |
| Virtual Threads | Lightweight I/O workers for simulated network calls |
| Structured Error Handling | Graceful degradation and retry logic |

## What you'll learn

- How to design a multi-stage concurrent pipeline from scratch
- How to combine CompletableFuture chaining with thread pools
- How to track metrics safely across hundreds of concurrent tasks
- How to apply graceful shutdown and error recovery patterns
- How everything from Lessons 01–10 fits together in production-style code

## Run the code

```bash
javac ConcurrentTaskProcessor.java
java ConcurrentTaskProcessor
```
