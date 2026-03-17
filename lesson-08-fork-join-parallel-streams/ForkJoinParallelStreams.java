import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Lesson 8 - The Fork/Join Framework and Parallel Streams
 *
 * How to run:
 *   javac ForkJoinParallelStreams.java
 *   java ForkJoinParallelStreams
 */
public class ForkJoinParallelStreams {

    // RecursiveTask: parallel array sum
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 1000;
        private final long[] array;
        private final int from, to;

        SumTask(long[] array, int from, int to) {
            this.array = array; this.from = from; this.to = to;
        }

        @Override
        protected Long compute() {
            if (to - from <= THRESHOLD) {
                long sum = 0;
                for (int i = from; i < to; i++) sum += array[i];
                return sum;
            }
            int mid = (from + to) / 2;
            SumTask left  = new SumTask(array, from, mid);
            SumTask right = new SumTask(array, mid, to);
            left.fork();                   // submit left subtask
            long rightResult = right.compute();  // compute right inline
            long leftResult  = left.join();      // wait for left
            return leftResult + rightResult;
        }
    }

    // RecursiveAction: parallel array fill
    static class FillAction extends RecursiveAction {
        private static final int THRESHOLD = 2000;
        private final int[] array;
        private final int from, to;

        FillAction(int[] array, int from, int to) {
            this.array = array; this.from = from; this.to = to;
        }

        @Override
        protected void compute() {
            if (to - from <= THRESHOLD) {
                for (int i = from; i < to; i++) array[i] = i * 2;
                return;
            }
            int mid = (from + to) / 2;
            invokeAll(new FillAction(array, from, mid),
                      new FillAction(array, mid, to));
        }
    }

    public static void main(String[] args) throws Exception {
        int N = 10_000_000;

        // 1. Sequential sum
        System.out.println("=== 1. Sequential vs Fork/Join Sum ===");
        long[] data = LongStream.rangeClosed(1, N).toArray();

        long t1 = System.nanoTime();
        long seqSum = 0;
        for (long v : data) seqSum += v;
        long t2 = System.nanoTime();

        ForkJoinPool pool = ForkJoinPool.commonPool();
        long t3 = System.nanoTime();
        long fjSum = pool.invoke(new SumTask(data, 0, data.length));
        long t4 = System.nanoTime();

        System.out.printf("Sequential:  %d ms  sum=%d%n", (t2-t1)/1_000_000, seqSum);
        System.out.printf("Fork/Join:   %d ms  sum=%d%n", (t4-t3)/1_000_000, fjSum);
        System.out.println("Common pool parallelism: " + pool.getParallelism());
        System.out.println();

        // 2. RecursiveAction
        System.out.println("=== 2. RecursiveAction (parallel fill) ===");
        int[] arr = new int[N];
        pool.invoke(new FillAction(arr, 0, arr.length));
        System.out.println("arr[0]=" + arr[0] + " arr[100]=" + arr[100] + " arr[999]=" + arr[999]);
        System.out.println();

        // 3. Parallel Streams
        System.out.println("=== 3. Parallel Stream ===");
        long t5 = System.nanoTime();
        long parSum = LongStream.rangeClosed(1, N).parallel().sum();
        long t6 = System.nanoTime();
        long t7 = System.nanoTime();
        long seqSum2 = LongStream.rangeClosed(1, N).sum();
        long t8 = System.nanoTime();

        System.out.printf("Parallel stream: %d ms  sum=%d%n", (t6-t5)/1_000_000, parSum);
        System.out.printf("Sequential stream: %d ms  sum=%d%n", (t8-t7)/1_000_000, seqSum2);
        System.out.println();

        // 4. Parallel stream map/filter
        System.out.println("=== 4. Parallel stream map/filter ===");
        long evenSquareSum = LongStream.rangeClosed(1, 100_000)
            .parallel()
            .filter(i -> i % 2 == 0)
            .map(i -> i * i)
            .sum();
        System.out.println("Sum of squares of evens 1..100000 = " + evenSquareSum);
        System.out.println();

        // 5. Custom ForkJoinPool for parallel streams
        System.out.println("=== 5. Custom ForkJoinPool ===");
        ForkJoinPool custom = new ForkJoinPool(2); // only 2 threads
        long result = custom.submit(() ->
            LongStream.rangeClosed(1, N).parallel().sum()
        ).get();
        System.out.println("Result with 2-thread pool: " + result);
        custom.shutdown();
        System.out.println("Done.");
    }
}
