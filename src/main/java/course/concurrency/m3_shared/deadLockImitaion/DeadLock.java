package course.concurrency.m3_shared.deadLockImitaion;

import java.util.concurrent.ConcurrentHashMap;

public class DeadLock {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        Thread first = new Thread(() -> map.computeIfAbsent("First", func -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return map.computeIfAbsent("Second", value -> 2);
        })

        );

        Thread second = new Thread(() -> map.computeIfAbsent("Second", func -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return map.computeIfAbsent("First", value -> 2);
        })

        );

        first.start();
        second.start();
        first.join();
        second.join();
    }
}
