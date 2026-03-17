# Lesson 5: CompletableFuture — Async Programming the Modern Way

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Go beyond blocking Future.get(). CompletableFuture lets you compose asynchronous pipelines, chain transformations, handle errors, and combine multiple async operations elegantly.

## Key Concepts

| Concept | Description |
|---------|-------------|
| CompletableFuture | A Future that can be explicitly completed and chained |
| supplyAsync | Runs a Supplier asynchronously, returns CompletableFuture<T> |
| thenApply | Transforms result (sync, same thread as previous stage) |
| thenCompose | Chains another async operation (flatMap for futures) |
| thenCombine | Combines two independent futures when both complete |
| exceptionally | Handles errors and provides a fallback value |
| allOf / anyOf | Waits for all/any of a list of futures |

## What you'll learn

- Creating async computations with supplyAsync and runAsync
- Building pipelines with thenApply, thenAccept, thenCompose
- Combining futures: thenCombine, allOf, anyOf
- Error handling with exceptionally and handle
- Timeout and cancellation

## Run the code

```bash
javac CompletableFutureDemo.java
java CompletableFutureDemo
```
