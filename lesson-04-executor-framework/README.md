# Lesson 4: The Executor Framework and Thread Pools

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Stop managing threads manually. The Executor Framework separates task submission from execution, letting thread pools handle lifecycle, reuse, and scaling automatically.

## Key Concepts

| Concept | Description |
|---------|-------------|
| Executor | Interface with a single execute(Runnable) method |
| ExecutorService | Extends Executor with lifecycle management and Future support |
| Thread Pool | A group of pre-created threads that execute submitted tasks |
| Future<T> | A handle to a pending asynchronous result |
| Callable<T> | Like Runnable but returns a value and can throw |
| ScheduledExecutorService | Schedules tasks with delay or fixed-rate repetition |

## What you'll learn

- Why raw threads don't scale and how pools fix that
- Fixed, cached, single, and work-stealing pools
- Submitting Callable tasks and retrieving results with Future
- Scheduling tasks with ScheduledExecutorService
- Proper shutdown: shutdown() vs shutdownNow()

## Run the code

```bash
javac ExecutorFramework.java
java ExecutorFramework
```
