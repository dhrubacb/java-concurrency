package com.dhurba.concurrent;

import java.util.concurrent.CountDownLatch;

public class WithCountDownLatch {
    static void main() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Producer(countDownLatch, 1)).start();
        new Thread(new Producer(countDownLatch, 2)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IO.println("Done");

    }

    public static class Producer implements Runnable {
        CountDownLatch countDownLatch;
        int i;

        public Producer(CountDownLatch countDownLatch, int i) {
            this.countDownLatch = countDownLatch;
            this.i = i;
        }

        @Override
        public void run() {
            try {
                IO.println("Produced: " + i);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

}
