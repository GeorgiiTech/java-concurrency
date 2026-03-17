import java.util.List;
import java.util.concurrent.*;

/**
 * Lesson 4 - The Executor Framework and Thread Pools
 *
 * How to run:
 *   javac ExecutorFramework.java
 *   java ExecutorFramework
 */
public class ExecutorFramework {

    static Callable<Integer> sumTask(int from, int to) {
        return () -> {
            int sum = 0;
            for (int i = from; i <= to; i++) sum += i;
            Thread.sleep(50);
            System.out.println(Thread.currentThread().getName() + " sum(" + from + "," + to + ")=" + sum);
            return sum;
        };
    }

    public static void main(String[] args) throws Exception {

        // 1. Fixed thread pool
        System.out.println("=== 1. Fixed Thread Pool ===");
        ExecutorService fixed = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 6; i++) {
            final int t = i;
            fixed.execute(() -> System.out.println(Thread.currentThread().getName() + " task-" + t));
        }
        fixed.shutdown();
        fixed.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println();

        // 2. Cached thread pool
        System.out.println("=== 2. Cached Thread Pool ===");
        ExecutorService cached = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            final int t = i;
            cached.submit(() -> { System.out.println(Thread.currentThread().getName() + " cached-" + t); return null; });
        }
        cached.shutdown();
        cached.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println();

        // 3. Callable + Future
        System.out.println("=== 3. Callable and Future ===");
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Future<Integer>> futures = new java.util.ArrayList<>();
        futures.add(pool.submit(sumTask(1, 25)));
        futures.add(pool.submit(sumTask(26, 50)));
        futures.add(pool.submit(sumTask(51, 75)));
        futures.add(pool.submit(sumTask(76, 100)));
        int total = 0;
        for (Future<Integer> f : futures) total += f.get();
        System.out.println("Total sum(1..100) = " + total);
        pool.shutdown();
        System.out.println();

        // 4. invokeAll
        System.out.println("=== 4. invokeAll ===");
        ExecutorService pool2 = Executors.newFixedThreadPool(3);
        List<Callable<String>> tasks = List.of(
            () -> { Thread.sleep(100); return "Task A done"; },
            () -> { Thread.sleep(50);  return "Task B done"; },
            () -> { Thread.sleep(150); return "Task C done"; }
        );
        for (Future<String> r : pool2.invokeAll(tasks)) System.out.println(r.get());
        pool2.shutdown();
        System.out.println();

        // 5. ScheduledExecutorService
        System.out.println("=== 5. Scheduled Executor ===");
        ScheduledExecutorService sched = Executors.newScheduledThreadPool(2);
        sched.schedule(() -> System.out.println("Delayed 100ms"), 100, TimeUnit.MILLISECONDS);
        sched.scheduleAtFixedRate(() -> System.out.println("Fixed-rate at " + System.currentTimeMillis()),
                                   0, 80, TimeUnit.MILLISECONDS);
        Thread.sleep(300);
        sched.shutdown();
        System.out.println();

        // 6. Virtual threads (Java 21+)
        System.out.println("=== 6. Virtual Thread Executor ===");
        try (ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                final int id = i;
                vt.submit(() -> System.out.println("Virtual task-" + id + " on " + Thread.currentThread()));
            }
        }
        System.out.println("Done.");
    }
}
