import java.util.concurrent.*;

/**
 * Lesson 5 - CompletableFuture: Async Programming the Modern Way
 *
 * How to run:
 *   javac CompletableFutureDemo.java
 *   java CompletableFutureDemo
 */
public class CompletableFutureDemo {

    // Simulate a remote service call
    static CompletableFuture<String> fetchUser(int id) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(100);
            if (id < 0) throw new RuntimeException("Invalid user id: " + id);
            return "User-" + id;
        });
    }

    static CompletableFuture<String> fetchOrder(String user) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(80);
            return "Order for " + user;
        });
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {

        // 1. supplyAsync + thenApply
        System.out.println("=== 1. supplyAsync + thenApply ===");
        CompletableFuture<String> future1 = CompletableFuture
            .supplyAsync(() -> { sleep(50); return 42; })
            .thenApply(n -> "Result: " + (n * 2));
        System.out.println(future1.get());
        System.out.println();

        // 2. thenCompose (flatMap)
        System.out.println("=== 2. thenCompose (async chain) ===");
        String result2 = fetchUser(1)
            .thenCompose(user -> fetchOrder(user))
            .get();
        System.out.println(result2);
        System.out.println();

        // 3. thenCombine (combine two independent futures)
        System.out.println("=== 3. thenCombine ===");
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> { sleep(60); return "Hello"; });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> { sleep(40); return "World"; });
        String result3 = cf1.thenCombine(cf2, (a, b) -> a + " " + b).get();
        System.out.println(result3);
        System.out.println();

        // 4. allOf — wait for all futures
        System.out.println("=== 4. allOf ===");
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> { sleep(100); return "A"; });
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> { sleep(50);  return "B"; });
        CompletableFuture<String> c = CompletableFuture.supplyAsync(() -> { sleep(80);  return "C"; });
        CompletableFuture.allOf(a, b, c).get();
        System.out.println("All done: " + a.get() + ", " + b.get() + ", " + c.get());
        System.out.println();

        // 5. anyOf — complete when any finishes
        System.out.println("=== 5. anyOf ===");
        CompletableFuture<Object> fastest = CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> { sleep(200); return "Slow"; }),
            CompletableFuture.supplyAsync(() -> { sleep(50);  return "Fast"; }),
            CompletableFuture.supplyAsync(() -> { sleep(100); return "Medium"; })
        );
        System.out.println("Fastest: " + fastest.get());
        System.out.println();

        // 6. Error handling with exceptionally
        System.out.println("=== 6. exceptionally ===");
        String result6 = fetchUser(-1)
            .exceptionally(ex -> "Fallback: " + ex.getMessage())
            .get();
        System.out.println(result6);
        System.out.println();

        // 7. handle (processes both result and exception)
        System.out.println("=== 7. handle ===");
        String result7 = fetchUser(-1)
            .handle((user, ex) -> ex != null ? "Error handled: " + ex.getMessage() : "User: " + user)
            .get();
        System.out.println(result7);
        System.out.println();

        // 8. thenAccept (consume result, return void)
        System.out.println("=== 8. thenAccept ===");
        fetchUser(5)
            .thenAccept(user -> System.out.println("Received: " + user))
            .get();
        System.out.println();

        // 9. orTimeout (Java 9+)
        System.out.println("=== 9. orTimeout ===");
        try {
            CompletableFuture.supplyAsync(() -> { sleep(500); return "late"; })
                .orTimeout(100, TimeUnit.MILLISECONDS)
                .get();
        } catch (ExecutionException e) {
            System.out.println("Timed out as expected: " + e.getCause().getClass().getSimpleName());
        }
        System.out.println("Done.");
    }
}
