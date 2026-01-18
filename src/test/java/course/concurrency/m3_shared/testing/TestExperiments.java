package course.concurrency.m3_shared.testing;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExperiments {

    // Don't change this class
    public static class Counter {
        private volatile int counter = 0;

        public void increment() {
            counter++;
        }

        public int get() {
            return counter;
        }
    }

    @RepeatedTest(100)
    public void counterShouldFail() {

        int threadCount = Runtime.getRuntime().availableProcessors();
        int iterations = 200;

        Counter counter = new Counter();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < iterations; i++) {

            executorService.submit(() -> {
                try {
                    latch.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                counter.increment();
            });
        }

        latch.countDown();
        executorService.shutdownNow();
        assertEquals(iterations, counter.get());
    }
}