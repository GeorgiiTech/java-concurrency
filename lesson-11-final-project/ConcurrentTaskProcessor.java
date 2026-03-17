import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * Lesson 11: Final Project - Concurrent Task Processor
 *
 * Combines ExecutorService, CompletableFuture, ConcurrentHashMap,
 * AtomicLong, and virtual threads into a single cohesive application.
 *
 * How to run:
 *   javac ConcurrentTaskProcessor.java
 *   java ConcurrentTaskProcessor
 *
 * Requires Java 21+.
 */
public class ConcurrentTaskProcessor {

    enum TaskType { COMPUTE, IO, VALIDATE }
    record Task(int id, TaskType type, String payload) {}
    record TaskResult(int taskId, String output, boolean success, long durationMs) {}

    static final AtomicLong totalProcessed  = new AtomicLong();
    static final AtomicLong totalFailed     = new AtomicLong();
    static final AtomicLong totalDurationMs = new AtomicLong();
    static final ConcurrentHashMap<Integer, TaskResult> registry = new ConcurrentHashMap<>();

    // === 1. Task Simulation ===

    static String fetchData(Task t) throws InterruptedException {
        Thread.sleep(10 + (t.id() % 5) * 5);
        return "raw[" + t.payload() + "]";
    }

    static String processData(Task t, String raw) throws InterruptedException {
        Thread.sleep(5);
        if (t.type() == TaskType.VALIDATE && t.id() % 7 == 0)
            throw new IllegalArgumentException("Validation failed: task " + t.id());
        return "ok[" + raw.toUpperCase() + "]";
    }

    static void storeResult(Task t, String out) throws InterruptedException {
        Thread.sleep(5);
    }

    // === 2. Async Pipeline ===

    static CompletableFuture<TaskResult> processAsync(Task t, Executor io, Executor cpu) {
        long s = System.currentTimeMillis();
        return CompletableFuture
            .supplyAsync(() -> { try { return fetchData(t); }
                catch (InterruptedException e) { throw new CompletionException(e); } }, io)
            .thenApplyAsync(raw -> { try { return processData(t, raw); }
                catch (InterruptedException e) { throw new CompletionException(e); } }, cpu)
            .thenApplyAsync(out -> { try { storeResult(t, out); return out; }
                catch (InterruptedException e) { throw new CompletionException(e); } }, io)
            .handle((out, ex) -> {
                long d = System.currentTimeMillis() - s;
                boolean ok = ex == null;
                return new TaskResult(t.id(), ok ? out : "ERR: " + ex.getCause().getMessage(), ok, d);
            });
    }

    // === 3. Batch Runner ===

    static void runBatch(List<Task> tasks) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService io  = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService cpu = Executors.newFixedThreadPool(cores);
        try {
            List<CompletableFuture<TaskResult>> futures =
                tasks.stream().map(t -> processAsync(t, io, cpu)).toList();
            for (var f : futures) {
                TaskResult r = f.get();
                registry.put(r.taskId(), r);
                totalProcessed.incrementAndGet();
                totalDurationMs.addAndGet(r.durationMs());
                if (!r.success()) totalFailed.incrementAndGet();
            }
        } finally {
            io.shutdown(); cpu.shutdown();
            io.awaitTermination(30, TimeUnit.SECONDS);
            cpu.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    // === 4. Retry Failed Tasks ===

    static void retryFailed(List<Task> orig) throws Exception {
        List<Task> toRetry = orig.stream()
            .filter(t -> { TaskResult r = registry.get(t.id()); return r != null && !r.success(); })
            .toList();
        if (toRetry.isEmpty()) return;
        System.out.println("  Retrying " + toRetry.size() + " task(s)...");
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for (var f : toRetry.stream().map(t -> processAsync(t, pool, pool)).toList()) {
                TaskResult r = f.get();
                TaskResult prev = registry.put(r.taskId(), r);
                if (prev != null && !prev.success() && r.success()) {
                    totalFailed.decrementAndGet();
                    System.out.println("  Task " + r.taskId() + " recovered.");
                }
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    // === 5. Metrics Report ===

    static void printMetrics(long wallMs) {
        long p = totalProcessed.get(), fail = totalFailed.get(), tot = totalDurationMs.get();
        System.out.println("  Total tasks    : " + p);
        System.out.println("  Successful     : " + (p - fail));
        System.out.println("  Failed         : " + fail);
        System.out.println("  Wall-clock ms  : " + wallMs);
        System.out.printf( "  Avg task ms    : %.1f%n", p > 0 ? (double) tot / p : 0.0);
        System.out.printf( "  Throughput     : %.0f tasks/sec%n",
            wallMs > 0 ? p * 1000.0 / wallMs : 0);
    }

    // === Main ===

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Setting Up 50 Tasks ===");
        var tasks = IntStream.rangeClosed(1, 50)
            .mapToObj(i -> new Task(i, TaskType.values()[i % 3], "task-" + i))
            .toList();
        System.out.println("  Created " + tasks.size() + " tasks (COMPUTE/IO/VALIDATE mix)");

        System.out.println("\n=== 2. Async Pipeline: fetch -> process -> store ===");
        long t0 = System.currentTimeMillis();
        runBatch(tasks);
        long elapsed = System.currentTimeMillis() - t0;
        System.out.println("  Batch done in " + elapsed + " ms");

        System.out.println("\n=== 3. Retry Failed Tasks ===");
        retryFailed(tasks);
        System.out.println("  Retry pass complete.");

        System.out.println("\n=== 4. Sample Results (tasks 1-5) ===");
        IntStream.rangeClosed(1, 5).forEach(i -> {
            TaskResult r = registry.get(i);
            if (r != null) System.out.printf("  Task %2d [%s] %dms: %s%n",
                r.taskId(), r.success() ? "OK  " : "FAIL", r.durationMs(),
                r.output().substring(0, Math.min(35, r.output().length())));
        });

        System.out.println("\n=== 5. Final Metrics ===");
        printMetrics(elapsed);

        System.out.println("\n=== 6. Registry Stats ===");
        long ok = registry.values().stream().filter(TaskResult::success).count();
        System.out.println("  Registry size  : " + registry.size());
        System.out.println("  Success entries: " + ok);
        System.out.println("  Failure entries: " + (registry.size() - ok));

        System.out.println("\n=== Done! Lessons 01-11 combined in one concurrent application. ===");
    }
}
