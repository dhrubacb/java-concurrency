package com.dhurba.concurrent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class WithBlockingQueue {

    @SneakyThrows
    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(1);
        WithBlockingQueue obj = new WithBlockingQueue();
        Thread thread1 = new Thread(obj.new Producer(blockingQueue));
        Thread thread2 = new Thread(obj.new Consumer(blockingQueue));
        thread2.start();
        thread1.start();
        thread1.join();
        thread2.interrupt();
        log.info("Blocking Queue Final Size: {}", blockingQueue.size());
    }

    private class Producer implements Runnable {
        private final BlockingQueue<Integer> blockingQueue;

        Producer(BlockingQueue<Integer> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 6; i++) {
                    blockingQueue.put(i);
                    log.info("Produced {}", i);
                    Thread.sleep(550);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Consumer implements Runnable {
        private final BlockingQueue<Integer> blockingQueue;

        Consumer(BlockingQueue<Integer> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() ) {
                if (blockingQueue.isEmpty()) continue;
                try {
                    log.info("Consumed {}", blockingQueue.take());
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
