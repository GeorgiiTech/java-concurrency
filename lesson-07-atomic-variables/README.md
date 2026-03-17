# Lesson 7: Atomic Variables: Lock-Free Thread Safety

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

When you only need to atomically update a single variable, atomic classes give you thread safety without the overhead of locks. They use hardware-level Compare-And-Swap (CAS) for lock-free operations.

## Key Concepts

| Class | Description |
|-------|-------------|
| AtomicInteger | Integer with atomic get, set, increment, CAS |
| AtomicLong | Long with atomic operations |
| AtomicBoolean | Boolean with atomic get/set/compareAndSet |
| AtomicReference<T> | Reference with atomic CAS operations |
| AtomicStampedReference | Reference + version stamp to prevent ABA problem |
| LongAdder | High-throughput counter optimized for frequent updates |
| Compare-And-Swap (CAS) | Atomic instruction: set value only if it equals expected |

## What you'll learn

- Why atomic classes are faster than synchronized for single variables
- The CAS pattern and how it drives all atomic operations
- AtomicInteger, AtomicLong, AtomicBoolean, AtomicReference
- Detecting the ABA problem and using AtomicStampedReference
- LongAdder vs AtomicLong for counters

## Run the code

```bash
javac AtomicVariables.java
java AtomicVariables
```
