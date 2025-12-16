package com.dhurba.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WithBlockingQueue {

    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(1);
        WithBlockingQueue obj = new WithBlockingQueue();
        new Thread(obj.new Producer(blockingQueue)).start();
        new Thread(obj.new Consumer(blockingQueue)).start();

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
                    System.out.println("Produced");
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
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    blockingQueue.take();
                    System.out.println("Consumed");
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
