/**
 * Lesson 2 – Creating and Managing Threads
 *
 * How to run:
 *   javac CreatingThreads.java
 *   java CreatingThreads
 *
 * Demonstrates:
 *   - Extending Thread vs implementing Runnable
 *   - Thread lifecycle states
 *   - sleep(), join(), interrupt()
 *   - Daemon threads
 */
public class CreatingThreads {

    // ── Approach 1: extend Thread ─────────────────────────────────────────────
    static class CounterThread extends Thread {
        private final String label;
        private final int count;

        CounterThread(String label, int count) {
            super(label);          // sets the thread name
            this.label = label;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 1; i <= count; i++) {
                System.out.println("[" + label + "] count = " + i);
                try {
                    Thread.sleep(100);  // pause 100 ms
                } catch (InterruptedException e) {
                    System.out.println("[" + label + "] interrupted!");
                    return;            // honour the interruption
                }
            }
        }
    }

    // ── Approach 2: implement Runnable ────────────────────────────────────────
    static class PrintTask implements Runnable {
        private final String message;
        private final int times;

        PrintTask(String message, int times) {
            this.message = message;
            this.times = times;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                System.out.println(Thread.currentThread().getName() + " → " + message);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // ── 1. Extend Thread ─────────────────────────────────────────────────
        System.out.println("=== 1. Extending Thread ===");
        CounterThread t1 = new CounterThread("T1-Counter", 3);
        t1.start();
        t1.join();   // wait for t1 to finish before printing next section
        System.out.println();

        // ── 2. Implement Runnable ────────────────────────────────────────────
        System.out.println("=== 2. Implementing Runnable ===");
        Runnable task = new PrintTask("Hello from Runnable", 3);
        Thread t2 = new Thread(task, "T2-Runnable");
        t2.start();
        t2.join();
        System.out.println();

        // ── 3. Lambda shorthand (Runnable is a functional interface) ─────────
        System.out.println("=== 3. Lambda Runnable ===");
        Thread t3 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running lambda");
        }, "T3-Lambda");
        t3.start();
        t3.join();
        System.out.println();

        // ── 4. Thread lifecycle: check state before and after start ──────────
        System.out.println("=== 4. Thread States ===");
        Thread t4 = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }, "T4-States");
        System.out.println("Before start: " + t4.getState());   // NEW
        t4.start();
        System.out.println("After  start: " + t4.getState());   // RUNNABLE or TIMED_WAITING
        t4.join();
        System.out.println("After  join : " + t4.getState());   // TERMINATED
        System.out.println();

        // ── 5. Interrupt a sleeping thread ───────────────────────────────────
        System.out.println("=== 5. Interrupting a Thread ===");
        CounterThread t5 = new CounterThread("T5-Interruptible", 10);
        t5.start();
        Thread.sleep(250);   // let it run for ~2 counts
        t5.interrupt();      // send interrupt signal
        t5.join();
        System.out.println();

        // ── 6. Daemon thread ─────────────────────────────────────────────────
        System.out.println("=== 6. Daemon Thread ===");
        Thread daemon = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("[Daemon] still running...");
                try { Thread.sleep(150); } catch (InterruptedException e) { break; }
            }
        }, "T6-Daemon");
        daemon.setDaemon(true);   // must be set BEFORE start()
        daemon.start();
        Thread.sleep(400);        // let main thread finish; daemon will stop automatically
        System.out.println("Main thread ending — daemon will be killed by JVM.");
    }
}
