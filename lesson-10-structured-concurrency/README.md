# Lesson 10: Structured Concurrency: Safe and Clean Multi-Threading (Java 21+)

> 📚 [Back to Course](https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads) | [Course Repo](../../README.md)

Structured Concurrency (finalized in Java 21) treats a group of related concurrent tasks as a single unit of work. If one task fails, the rest are cancelled automatically, making concurrent code as readable and safe as sequential code.

## Key Concepts

| Concept | Description |
|---------|-------------|
| StructuredTaskScope | The core class: opens a scope that owns child threads |
| ShutdownOnFailure | Cancels all subtasks if any one fails |
| ShutdownOnSuccess | Cancels all subtasks when the first one succeeds |
| scope.fork() | Spawns a subtask as a virtual thread |
| scope.join() | Waits for all subtasks to complete |
| scope.throwIfFailed() | Rethrows any subtask exception |
| Subtask<T> | Handle to a forked task; call .get() after join |

## What you'll learn

- How unstructured concurrency leaks threads and hides errors
- Using StructuredTaskScope for fan-out patterns
- ShutdownOnFailure: fail-fast with clean cancellation
- ShutdownOnSuccess: racing tasks, take the first result
- Nesting scopes for complex concurrent workflows

## Run the code

```bash
javac StructuredConcurrency.java
java StructuredConcurrency
```

> Requires Java 21+
