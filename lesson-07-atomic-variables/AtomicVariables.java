import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Lesson 7 - Atomic Variables: Lock-Free Thread Safety
 *
 * How to run:
 *   javac AtomicVariables.java
 *   java AtomicVariables
 */
public class AtomicVariables {

    static void runThreads(int n, Runnable r) throws InterruptedException {
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) threads[i] = new Thread(r);
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        final int THREADS = 5, INC = 100_000;

        // 1. AtomicInteger vs plain int
        System.out.println("=== 1. AtomicInteger ===");
        int[] unsafeCount = {0};
        AtomicInteger safeCount = new AtomicInteger(0);
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) { unsafeCount[0]++; safeCount.incrementAndGet(); } });
        System.out.println("Expected: " + (THREADS * INC));
        System.out.println("Unsafe int:      " + unsafeCount[0] + " (likely wrong)");
        System.out.println("AtomicInteger:   " + safeCount.get() + " (always correct)");
        System.out.println();

        // 2. CAS (compareAndSet)
        System.out.println("=== 2. Compare-And-Swap (CAS) ===");
        AtomicInteger cas = new AtomicInteger(0);
        boolean r1 = cas.compareAndSet(0, 10); // succeeds: 0 == 0
        boolean r2 = cas.compareAndSet(0, 20); // fails: 0 != 10
        System.out.println("CAS(0→10): " + r1 + " value=" + cas.get()); // true, 10
        System.out.println("CAS(0→20): " + r2 + " value=" + cas.get()); // false, 10
        System.out.println();

        // 3. getAndUpdate / updateAndGet
        System.out.println("=== 3. getAndUpdate / updateAndGet ===");
        AtomicInteger ai = new AtomicInteger(5);
        int prev = ai.getAndUpdate(x -> x * 2); // returns old value
        System.out.println("getAndUpdate(*2): prev=" + prev + " now=" + ai.get());
        int next = ai.updateAndGet(x -> x + 3); // returns new value
        System.out.println("updateAndGet(+3): next=" + next);
        System.out.println();

        // 4. AtomicLong for sequence numbers
        System.out.println("=== 4. AtomicLong sequence ===");
        AtomicLong seq = new AtomicLong(0);
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) seq.incrementAndGet(); });
        System.out.println("Sequence: " + seq.get() + " (expected " + (THREADS * INC) + ")");
        System.out.println();

        // 5. AtomicBoolean flag
        System.out.println("=== 5. AtomicBoolean ===");
        AtomicBoolean initialized = new AtomicBoolean(false);
        if (initialized.compareAndSet(false, true)) {
            System.out.println("First thread initializes resource");
        }
        if (!initialized.compareAndSet(false, true)) {
            System.out.println("Second thread sees already initialized (expected)");
        }
        System.out.println();

        // 6. AtomicReference
        System.out.println("=== 6. AtomicReference ===");
        AtomicReference<String> ref = new AtomicReference<>("v1");
        boolean swapped = ref.compareAndSet("v1", "v2");
        System.out.println("CAS(v1→v2): " + swapped + " ref=" + ref.get());
        System.out.println();

        // 7. LongAdder (high-throughput counter)
        System.out.println("=== 7. LongAdder vs AtomicLong ===");
        LongAdder adder = new LongAdder();
        AtomicLong atomicLong = new AtomicLong(0);

        long t1 = System.nanoTime();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) adder.increment(); });
        long t2 = System.nanoTime();
        runThreads(THREADS, () -> { for (int i = 0; i < INC; i++) atomicLong.incrementAndGet(); });
        long t3 = System.nanoTime();

        System.out.printf("LongAdder:   %d ms  result=%d%n", (t2-t1)/1_000_000, adder.sum());
        System.out.printf("AtomicLong:  %d ms  result=%d%n", (t3-t2)/1_000_000, atomicLong.get());
        System.out.println("(LongAdder is faster under high contention)");
        System.out.println("Done.");
    }
}
