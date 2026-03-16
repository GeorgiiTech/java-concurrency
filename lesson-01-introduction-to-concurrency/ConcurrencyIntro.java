/**
 * Lesson 1: Introduction to Concurrency — Why It Matters
 * Course: Java Concurrency & Multithreading: Modern Java from Threads to Virtual Threads
 * https://georgii.tech/courses/java-concurrency-multithreading-modern-java-from-threads-to-virtual-threads/lessons/introduction-to-concurrency-why-it-matters/
 */
public class ConcurrencyIntro {

    // ─────────────────────────────────────────────
    // Race Condition Demo
    // Without synchronization, counter is corrupted
    // ─────────────────────────────────────────────
    static int unsafeCounter = 0;

    static void raceConditionDemo() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) unsafeCounter++;
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) unsafeCounter++;
        });
        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("Expected: 20000, Got: " + unsafeCounter);
        // Result is usually LESS than 20000 — race condition!
    }

    // ─────────────────────────────────────────────
    // Visibility Problem Demo
    // Without volatile, loop may run forever
    // ─────────────────────────────────────────────
    static volatile boolean stop = false; // volatile ensures visibility

    static void visibilityDemo() throws InterruptedException {
        Thread worker = new Thread(() -> {
            long count = 0;
            while (!stop) count++;  // reads 'stop' from main memory (volatile)
            System.out.println("Worker stopped after " + count + " iterations");
        });
        worker.start();
        Thread.sleep(100);
        stop = true; // visible to worker because of volatile
        worker.join();
    }

    // ─────────────────────────────────────────────
    // Deadlock Demo
    // Thread 1 holds lock1, wants lock2
    // Thread 2 holds lock2, wants lock1 → DEADLOCK
    // ─────────────────────────────────────────────
    static final Object lock1 = new Object();
    static final Object lock2 = new Object();

    static void deadlockDemo() {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("T1 holds lock1, waiting for lock2...");
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                synchronized (lock2) { // will wait forever if T2 holds lock2
                    System.out.println("T1 acquired both locks!");
                }
            }
        }, "T1");

        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("T2 holds lock2, waiting for lock1...");
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                synchronized (lock1) { // will wait forever if T1 holds lock1
                    System.out.println("T2 acquired both locks!");
                }
            }
        }, "T2");

        t1.start(); t2.start();
        // These threads will DEADLOCK — both waiting forever
        // Fix: always acquire locks in the SAME ORDER in both threads
    }

    // ─────────────────────────────────────────────
    // Concurrency vs Parallelism
    // ─────────────────────────────────────────────
    static void concurrencyVsParallelism() {
        System.out.println("\n=== Concurrency vs Parallelism ===");
        System.out.println("Available CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Concurrency: multiple tasks making PROGRESS (may interleave on 1 CPU)");
        System.out.println("Parallelism:  multiple tasks running SIMULTANEOUSLY (needs multiple CPUs)");
        System.out.println("Java threads: OS schedules them — can be concurrent OR parallel");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Introduction to Concurrency ===\n");

        concurrencyVsParallelism();

        System.out.println("\n--- Race Condition Demo ---");
        raceConditionDemo();

        System.out.println("\n--- Visibility Demo (volatile) ---");
        visibilityDemo();

        // Note: deadlockDemo() is commented out to avoid hanging the program
        // Uncomment to observe deadlock:
        // System.out.println("\n--- Deadlock Demo (will hang!) ---");
        // deadlockDemo();

        System.out.println("\n--- Key Takeaways ---");
        System.out.println("1. Shared mutable state + multiple threads = race conditions");
        System.out.println("2. CPU caches can hide writes — use volatile for visibility");
        System.out.println("3. Acquiring locks in different orders causes deadlocks");
        System.out.println("4. Solutions: synchronization, volatile, atomic classes, immutability");
    }
}
