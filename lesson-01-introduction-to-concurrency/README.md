# Lesson 1: Introduction to Concurrency — Why It Matters

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Understand what concurrency is, why it's essential for modern software, and the core challenges it introduces: race conditions, deadlocks, and visibility problems.

## Key Concepts

| Concept | Description |
|---------|-------------|
| Process | Independent program with its own memory space |
| Thread | Lightweight unit of execution within a process |
| Race Condition | Two threads read/write shared data in undefined order |
| Deadlock | Two threads each wait for a lock the other holds |
| Visibility | CPU caches can hide writes from other threads |
| Atomicity | An operation that completes without interruption |

## What you'll learn

- What concurrency and parallelism mean (and the difference)
- Why multi-core CPUs require concurrent programming
- Race conditions and how they corrupt shared state
- Deadlocks: what causes them and how to avoid them
- Memory visibility problems and the Java Memory Model basics

## Run the code

```bash
javac ConcurrencyIntro.java
java ConcurrencyIntro
```
