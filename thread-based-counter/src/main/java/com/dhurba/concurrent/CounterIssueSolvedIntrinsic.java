package com.dhurba.concurrent;

public class CounterIssueSolvedIntrinsic {
    int count = 0;

    static void main() throws InterruptedException {
        CounterIssueSolvedIntrinsic counter = new CounterIssueSolvedIntrinsic();
        for (int i = 0; i < 10; i++) {
            Runnable runnable = counter::increment;
            new Thread(runnable).start();
        }
        Thread.sleep(100);
        System.out.printf("Final count: %d, expected: %d\n", counter.getCount(), 10000);
    }

    public synchronized void increment() {
        for (int i = 0; i < 1000; i++)
            count++;
        System.out.printf("Count: %d\n", count);
    }

    public int getCount() {
        return count;
    }

}
