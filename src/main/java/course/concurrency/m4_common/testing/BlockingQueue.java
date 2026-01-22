package course.concurrency.m4_common.testing;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

    private final int capacity;
    private final LinkedList<T> src = new LinkedList<>();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(T value) {
        lock.lock();
        try {
            while (src.size() == capacity) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            src.addLast(value);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T dequeue() {
        lock.lock();
        try {
            while (src.isEmpty()) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T res = src.removeFirst();
            condition.signalAll();
            return res;
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        lock.lock();
        try {
            return src.size();
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }
}
