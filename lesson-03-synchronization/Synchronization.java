import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lesson 3 - Synchronization: synchronized, volatile, and Locks
 *
 * How to run:
 *   javac Synchronization.java
 *   java Synchronization
 */
public class Synchronization {

    // 1. Unsynchronized counter (demonstrates race condition)
    static class UnsafeCounter {
        int count = 0;
        void increment() { count++; }
    }

    // 2. Synchronized counter
    static class SyncCounter {
        private int count = 0;
        synchronized void increment() { count++; }
        synchronized int get() { return count; }
    }

    // 3. Synchronized block
    static class BlockCounter {
        private int count = 0;
        private final Object lock = new Object();
        void increment() {
            synchronized (lock) { count++; }
        }
        int get() { synchronized (lock) { return count; } }
    }

    // 4. Volatile flag
    static volatile boolean running = true;

    // 5. ReentrantLock
    static class LockCounter {
        private int count = 0;
        private final Lock lock = new ReentrantLock();
        void increment() {
            lock.lock();
            try { count++; }
            finally { lock.unlock(); }
        }
        int get() { lock.lock(); try { return count; } finally { lock.unlock(); } }
    }

    // 6. ReadWriteLock
    static class Cache {
        private String data = "initial";
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        String read() {
            rwLock.readLock().lock();
            try { return data; }
            finally { rwLock.readLock().unlock(); }
        }
        void write(String newData) {
            rwLock.writeLock().lock();
            try { data = newData; }
            finally { rwLock.writeLock().unlock(); }
        }
    }

    static void runThreads(int n, Runnable task) throws InterruptedException {
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) threads[i] = new Thread(task);
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        final int THREADS = 5, INC = 10_000;

        System.out.println("=== 1. Unsynchronized (race condition) ===");
        UnsafeCounter unsafe = new UnsafeCounter();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) unsafe.increment(); });
        System.out.println("Expected: " + (THREADS * INC) + "  Got: " + unsafe.count);
        System.out.println();

        System.out.println("=== 2. synchronized method ===");
        SyncCounter sync = new SyncCounter();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) sync.increment(); });
        System.out.println("Expected: " + (THREADS * INC) + "  Got: " + sync.get());
        System.out.println();

        System.out.println("=== 3. synchronized block ===");
        BlockCounter block = new BlockCounter();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) block.increment(); });
        System.out.println("Expected: " + (THREADS * INC) + "  Got: " + block.get());
        System.out.println();

        System.out.println("=== 4. volatile flag ===");
        Thread worker = new Thread(() -> {
            int loops = 0;
            while (running) loops++;
            System.out.println("Worker stopped after " + loops + " loops");
        });
        worker.start();
        Thread.sleep(20);
        running = false;
        worker.join();
        System.out.println();

        System.out.println("=== 5. ReentrantLock ===");
        LockCounter lc = new LockCounter();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) lc.increment(); });
        System.out.println("Expected: " + (THREADS * INC) + "  Got: " + lc.get());
        System.out.println();

        System.out.println("=== 6. ReadWriteLock ===");
        Cache cache = new Cache();
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                cache.write("value-" + i);
                System.out.println("[Writer] wrote value-" + i);
                try { Thread.sleep(50); } catch (InterruptedException e) { break; }
            }
        });
        Thread reader1 = new Thread(() -> {
            for (int i = 0; i < 6; i++) {
                System.out.println("[Reader1] read: " + cache.read());
                try { Thread.sleep(25); } catch (InterruptedException e) { break; }
            }
        });
        writer.start(); reader1.start();
        writer.join(); reader1.join();
    }
}
