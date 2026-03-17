import java.util.concurrent.*;
import java.util.concurrent.StructuredTaskScope.*;

/**
 * Lesson 10 - Structured Concurrency: Safe and Clean Multi-Threading (Java 21+)
 *
 * How to run (Java 21+):
 *   javac StructuredConcurrency.java
 *   java StructuredConcurrency
 *
 * Note: StructuredTaskScope is in java.util.concurrent (Java 21 GA)
 */
public class StructuredConcurrency {

    // Simulate remote service calls
    static String fetchUser(int id) throws InterruptedException {
        Thread.sleep(100);
        if (id < 0) throw new RuntimeException("Bad user id: " + id);
        return "User-" + id;
    }

    static String fetchOrder(String user) throws InterruptedException {
        Thread.sleep(80);
        return "Order[" + user + "]";
    }

    static int fetchScore(String user) throws InterruptedException {
        Thread.sleep(60);
        return user.hashCode() % 100;
    }

    static String searchServer1(String query) throws InterruptedException {
        Thread.sleep(200);
        return "Server1 result for: " + query;
    }

    static String searchServer2(String query) throws InterruptedException {
        Thread.sleep(80); // faster
        return "Server2 result for: " + query;
    }

    public static void main(String[] args) throws Exception {

        // 1. ShutdownOnFailure: all tasks must succeed
        System.out.println("=== 1. ShutdownOnFailure (all tasks succeed) ===");
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String> userTask  = scope.fork(() -> fetchUser(42));
            Subtask<String> orderTask = scope.fork(() -> fetchOrder("pending"));
            Subtask<Integer> scoreTask = scope.fork(() -> fetchScore("User-42"));

            scope.join().throwIfFailed();  // wait + rethrow any failure

            System.out.println("User:  " + userTask.get());
            System.out.println("Order: " + orderTask.get());
            System.out.println("Score: " + scoreTask.get());
        }
        System.out.println();

        // 2. ShutdownOnFailure: one task fails, others cancelled
        System.out.println("=== 2. ShutdownOnFailure (one task fails) ===");
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String> good = scope.fork(() -> fetchUser(1));
            Subtask<String> bad  = scope.fork(() -> fetchUser(-1)); // throws

            scope.join();
            try {
                scope.throwIfFailed();
            } catch (ExecutionException e) {
                System.out.println("Caught: " + e.getCause().getMessage());
            }
        }
        System.out.println();

        // 3. ShutdownOnSuccess: take first result (race)
        System.out.println("=== 3. ShutdownOnSuccess (race pattern) ===");
        String query = "Java concurrency";
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> searchServer1(query));
            scope.fork(() -> searchServer2(query)); // faster

            scope.join();
            System.out.println("First result: " + scope.result());
        }
        System.out.println();

        // 4. Fan-out: parallel calls then combine
        System.out.println("=== 4. Fan-out and combine ===");
        record UserProfile(String user, String order, int score) {}

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String>  u = scope.fork(() -> fetchUser(7));
            Subtask<String>  o = scope.fork(() -> fetchOrder("cart"));
            Subtask<Integer> s = scope.fork(() -> fetchScore("User-7"));

            scope.join().throwIfFailed();

            UserProfile profile = new UserProfile(u.get(), o.get(), s.get());
            System.out.println("Profile: " + profile);
        }
        System.out.println();

        System.out.println("All structured concurrency demos complete.");
    }
}
