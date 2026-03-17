import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lesson 6 - Concurrent Collections: Thread-Safe Data Structures
 *
 * How to run:
 *   javac ConcurrentCollections.java
 *   java ConcurrentCollections
 */
public class ConcurrentCollections {

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws InterruptedException {

        // 1. ConcurrentHashMap
        System.out.println("=== 1. ConcurrentHashMap ===");
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            final int n = i;
            pool.execute(() -> map.merge("key-" + (n % 10), 1, Integer::sum));
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Map size: " + map.size() + " (expected 10)");
        System.out.println("Sum of all values: " + map.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println();

        // 2. CopyOnWriteArrayList
        System.out.println("=== 2. CopyOnWriteArrayList ===");
        CopyOnWriteArrayList<Integer> cowal = new CopyOnWriteArrayList<>();
        ExecutorService writers = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 9; i++) {
            final int val = i;
            writers.execute(() -> cowal.add(val));
        }
        writers.shutdown();
        writers.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("CopyOnWriteArrayList: " + cowal.size() + " elements (expected 9)");
        System.out.println();

        // 3. BlockingQueue producer-consumer
        System.out.println("=== 3. BlockingQueue Producer-Consumer ===");
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        AtomicInteger consumed = new AtomicInteger(0);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 8; i++) {
                try {
                    queue.put(i);
                    System.out.println("[Producer] put " + i);
                } catch (InterruptedException e) { break; }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 8; i++) {
                try {
                    int val = queue.take();
                    System.out.println("[Consumer] took " + val);
                    consumed.incrementAndGet();
                    sleep(30);
                } catch (InterruptedException e) { break; }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Consumed: " + consumed.get() + " items");
        System.out.println();

        // 4. ConcurrentLinkedQueue (non-blocking)
        System.out.println("=== 4. ConcurrentLinkedQueue ===");
        ConcurrentLinkedQueue<String> clq = new ConcurrentLinkedQueue<>();
        ExecutorService adders = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 6; i++) {
            final int n = i;
            adders.execute(() -> clq.offer("msg-" + n));
        }
        adders.shutdown();
        adders.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("Queue size: " + clq.size());
        System.out.println();

        // 5. ConcurrentSkipListMap (sorted, thread-safe)
        System.out.println("=== 5. ConcurrentSkipListMap ===");
        ConcurrentSkipListMap<Integer, String> skipMap = new ConcurrentSkipListMap<>();
        skipMap.put(3, "three");
        skipMap.put(1, "one");
        skipMap.put(4, "four");
        skipMap.put(2, "two");
        System.out.println("Sorted keys: " + skipMap.keySet());
        System.out.println("First: " + skipMap.firstKey() + " Last: " + skipMap.lastKey());
        System.out.println();

        System.out.println("All concurrent collection demos complete.");
    }
}
