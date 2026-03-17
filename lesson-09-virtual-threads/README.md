# Lesson 9: Virtual Threads: Project Loom and Java 21

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Virtual threads (Project Loom, GA in Java 21) are lightweight threads managed by the JVM, not the OS. You can run millions of them concurrently without tuning thread pools, making blocking code scale like async code.

## Key Concepts

| Concept | Description |
|---------|-------------|
| Virtual Thread | JVM-managed thread, extremely cheap to create and block |
| Platform Thread | Traditional OS thread (1:1 with kernel thread) |
| Carrier Thread | Platform thread that runs virtual threads |
| Pinning | Virtual thread stuck to carrier during synchronized/native |
| Thread.ofVirtual() | Builder API for creating virtual threads |
| newVirtualThreadPerTaskExecutor | ExecutorService that creates a virtual thread per task |
| Structured Concurrency | Composing virtual threads safely (Java 21+) |

## What you'll learn

- How virtual threads differ from platform threads
- Creating virtual threads with Thread.ofVirtual() and executors
- Why virtual threads excel at blocking I/O workloads
- Pinning: what it is and how to avoid it
- Migrating thread pool code to virtual threads

## Run the code

```bash
javac --enable-preview --release 21 VirtualThreads.java
java --enable-preview VirtualThreads
```

> Requires Java 21+
