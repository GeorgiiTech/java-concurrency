# Lesson 2: Creating and Managing Threads

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Master the two fundamental ways to create threads in Java — extending `Thread` and implementing `Runnable` — and understand the thread lifecycle, daemon threads, and how to control execution with `sleep`, `join`, and `interrupt`.

## Key Concepts

| Concept | Description |
|---------|-------------|
| Thread | A lightweight unit of execution managed by the JVM |
| Runnable | Functional interface representing a task to run in a thread |
| Thread Lifecycle | NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED |
| Daemon thread | Background thread that dies when all user threads finish |
| join() | Waits for a thread to complete before continuing |
| interrupt() | Signals a thread to stop what it's doing |

## What you'll learn

- Two ways to create threads: `extends Thread` vs `implements Runnable`
- The full thread lifecycle and state transitions
- How to use `sleep()`, `join()`, and `interrupt()`
- Daemon vs user threads
- Thread naming and priority basics

## Run the code

```bash
javac CreatingThreads.java
java CreatingThreads
```
