package course.concurrency.m4_common.testing;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

    @Test
    public void elementsShouldBeOrderedSingleThread() {
        int count = 5;
        BlockingQueue<Integer> queue = new BlockingQueue<>(count);

        for (int i = 0; i < count; i++) {
            queue.enqueue(i);
        }

        for (int i = 0; i < count; i++) {
            Integer res = queue.dequeue();
            assertEquals(i, res);
        }
    }

    @Test
    public void elementsShouldBeRetrieved() throws InterruptedException {
        int count = 500;
        BlockingQueue<Integer> queue = new BlockingQueue<>(count);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(count*3);

        for (int i = 0; i < count; i++) {
            final Integer element = i;
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                queue.enqueue(element);
            });
        }

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Integer res = queue.dequeue();
                resultQueue.add(res);
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    public void shouldBlockOnPut() throws InterruptedException {
        int count = 100;
        int capacity = 2;
        BlockingQueue<Integer> queue = new BlockingQueue<>(capacity);

        int poolSize = capacity*3;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < count; i++) {
            final Integer element = i;
            executor.submit(() -> queue.enqueue(element));
        }

        assertEquals(capacity, queue.getCapacity());
        assertEquals(capacity, queue.getSize());
        assertEquals(count, executor.getTaskCount());

        assertEquals(capacity, executor.getCompletedTaskCount());

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue();
        for (int i = 0; i < count; i++) {
            Integer res = queue.dequeue();
            resultQueue.add(res);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    public void shouldBlockOnEmpty() throws InterruptedException {
        int count = 100;
        int capacity = 2;
        BlockingQueue<Integer> queue = new BlockingQueue<>(capacity);

        int poolSize = capacity*3;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                Integer res = queue.dequeue();
                resultQueue.add(res);
            });
        }

        assertEquals(capacity, queue.getCapacity());
        assertEquals(0, queue.getSize());
        assertEquals(0, executor.getCompletedTaskCount());
        assertEquals(count, executor.getTaskCount());

        for (int i = 0; i < count; i++) {
            final Integer element = i;
            queue.enqueue(element);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

}