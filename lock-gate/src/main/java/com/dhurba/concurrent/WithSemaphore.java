package com.dhurba.concurrent;

import java.util.concurrent.Semaphore;

public class WithSemaphore {
    static void main() throws InterruptedException {
        Semaphore totalAvailablePermits = new Semaphore(2, false); // At a time two concurrent thread can access
        for (int i = 1; i <= 5; i++) {
            final int k = i;
            Thread thread = new Thread(() -> gateKeeping(k, totalAvailablePermits));
            thread.start();
        }

    }

    private static void gateKeeping(int i, Semaphore totalAvailablePermits) {
        IO.println("Initiate Gate: " + i);
        try {
            while (!totalAvailablePermits.tryAcquire()) {
                Thread.sleep(1200);
            }
            IO.println("Inside Gate: " + i);
            Thread.sleep(1000);
            totalAvailablePermits.release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IO.println("Crossed Gate: " + i);
        }
    }
}
