import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lesson 9 - Virtual Threads: Project Loom and Java 21
 *
 * How to run (Java 21+):
 *   javac VirtualThreads.java
 *   java VirtualThreads
 */
public class VirtualThreads {

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static void simulateIO(String label) {
        sleep(100); // simulate blocking I/O
        System.out.println(label + " done on: " + Thread.currentThread());
    }

    public static void main(String[] args) throws Exception {

        // 1. Create a single virtual thread
        System.out.println("=== 1. Single Virtual Thread ===");
        Thread vt = Thread.ofVirtual().name("my-virtual-thread").start(() -> {
            System.out.println("Running in: " + Thread.currentThread());
            System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
        });
        vt.join();
        System.out.println();

        // 2. Virtual thread vs platform thread
        System.out.println("=== 2. Platform vs Virtual Thread ===");
        Thread pt = Thread.ofPlatform().name("platform-thread").start(() -> {
            System.out.println("Platform: " + Thread.currentThread() + " virtual=" + Thread.currentThread().isVirtual());
        });
        Thread vt2 = Thread.ofVirtual().name("virtual-thread").start(() -> {
            System.out.println("Virtual:  " + Thread.currentThread() + " virtual=" + Thread.currentThread().isVirtual());
        });
        pt.join(); vt2.join();
        System.out.println();

        // 3. Scale: 10,000 virtual threads vs platform threads
        System.out.println("=== 3. Scalability: 10,000 virtual threads ===");
        int N = 10_000;
        AtomicInteger done = new AtomicInteger();

        long t1 = System.nanoTime();
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < N; i++) {
                exec.submit(() -> { sleep(10); done.incrementAndGet(); });
            }
        }
        long t2 = System.nanoTime();
        System.out.printf("Virtual threads: %,d tasks in %d ms%n", done.get(), (t2-t1)/1_000_000);

        done.set(0);
        long t3 = System.nanoTime();
        ExecutorService fixed = Executors.newFixedThreadPool(200);
        for (int i = 0; i < N; i++) {
            fixed.submit(() -> { sleep(10); done.incrementAndGet(); });
        }
        fixed.shutdown();
        fixed.awaitTermination(60, TimeUnit.SECONDS);
        long t4 = System.nanoTime();
        System.out.printf("Fixed pool (200): %,d tasks in %d ms%n", done.get(), (t4-t3)/1_000_000);
        System.out.println("(Virtual threads are faster for blocking I/O)");
        System.out.println();

        // 4. Thread.Builder API
        System.out.println("=== 4. Thread.Builder API ===");
        Thread.Builder.OfVirtual builder = Thread.ofVirtual().name("worker-", 0);
        Thread w1 = builder.start(() -> System.out.println("w1 " + Thread.currentThread().getName()));
        Thread w2 = builder.start(() -> System.out.println("w2 " + Thread.currentThread().getName()));
        w1.join(); w2.join();
        System.out.println();

        // 5. Virtual threads are cheap to create
        System.out.println("=== 5. Creating 1 million virtual threads ===");
        long t5 = System.nanoTime();
        AtomicInteger millionDone = new AtomicInteger();
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000_000; i++) {
                exec.submit(millionDone::incrementAndGet);
            }
        }
        long t6 = System.nanoTime();
        System.out.printf("Created and ran 1M virtual threads in %d ms%n", (t6-t5)/1_000_000);
        System.out.println("Done.");
    }
}
