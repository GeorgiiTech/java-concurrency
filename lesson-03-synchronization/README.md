# Lesson 3: Synchronization: synchronized, volatile, and Locks

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Understand how to protect shared mutable state using `synchronized`, `volatile`, and explicit `Lock` objects. Learn when to use each tool and how they interact with the Java Memory Model.

## Key Concepts

| Concept | Description |
|---------|-------------|
| synchronized | Keyword that ensures mutual exclusion on a monitor |
| volatile | Guarantees visibility of writes across threads |
| ReentrantLock | Explicit lock with tryLock, timed locking, and fairness |
| ReadWriteLock | Allows concurrent reads but exclusive writes |
| happens-before | JMM rule: write is visible to all subsequent reads |
| Monitor | Object-level lock used by synchronized |

## What you'll learn

- Why unsynchronized access causes race conditions
- How `synchronized` methods and blocks work
- When `volatile` is enough (and when it's not)
- Using `ReentrantLock` for finer-grained control
- Read/write locks for read-heavy workloads

## Run the code

```bash
javac Synchronization.java
java Synchronization
```
