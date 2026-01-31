package course.concurrency.m6_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(1, 1,
                0, TimeUnit.MILLISECONDS,
                new LifoBlockingQueue<>());
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(8, 8,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }
}

class LifoBlockingQueue<E> extends LinkedBlockingDeque<E> {

    @Override
    public E take() throws InterruptedException {
        return super.takeLast();
    }
}
