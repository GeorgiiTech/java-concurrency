# Lesson 8: The Fork/Join Framework and Parallel Streams

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Divide-and-conquer at scale. The Fork/Join framework splits large tasks into subtasks that run in parallel on all CPU cores, then joins results back together. Parallel Streams use the same pool transparently.

## Key Concepts

| Concept | Description |
|---------|-------------|
| ForkJoinPool | Thread pool optimized for recursive divide-and-conquer tasks |
| RecursiveTask<T> | ForkJoinTask that returns a result |
| RecursiveAction | ForkJoinTask with no result (side-effect only) |
| fork() | Submits a subtask to the pool asynchronously |
| join() | Waits for a subtask and returns its result |
| Work Stealing | Idle threads steal tasks from busy threads' queues |
| Parallel Streams | Stream API backed by the common ForkJoinPool |

## What you'll learn

- Implementing RecursiveTask for parallel sum and merge sort
- The fork/join pattern and work-stealing algorithm
- Using parallel() streams and when they help vs hurt
- Controlling parallelism level
- Common pitfalls: thread-safety, ordering, I/O in parallel streams

## Run the code

```bash
javac ForkJoinParallelStreams.java
java ForkJoinParallelStreams
```
