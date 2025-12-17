package com.dhurba.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
This Scenario Creates Deadlock: Second thread trying to lock before first thread unlocking it
 Reentrant Lock works as follows:
 When one thread pass the lockObject.lock() statement, other thread with the same lockObject can not lock again, so it waits.
 */
public class DeadLockWithReentrantLock {
    static void main(String[] args) {
        DeadLockWithReentrantLock obj = new DeadLockWithReentrantLock();
        Runnable th = () -> {
            Lock lock1 = new ReentrantLock(false); // non-fair lock -> doesn't maintain order of threads waiting for lock
            Lock lock2 = new ReentrantLock(true);  // fair
            new Thread(() -> obj.acquireLocks(lock1, lock2), "Thread1").start();
            new Thread(() -> obj.acquireLocks(lock2, lock1), "Thread2").start();
        };
        try {
            Future<?> submit = Executors.newSingleThreadExecutor().submit(th);
            submit.get(20000, TimeUnit.MILLISECONDS);
            Thread.sleep(1000);
            submit.cancel(true);
            new Thread(() -> {
                Lock lock1 = new ReentrantLock(false); // non-fair lock -> doesn't maintain order of threads waiting for lock
                Lock lock2 = new ReentrantLock(true);  // fair
                new Thread(() -> obj.acquireLocks(lock1, lock2), "Thread3").start();
                new Thread(() -> obj.acquireLocks(lock1, lock2), "Thread4").start();
            }).start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void acquireLocks(Lock lock1, Lock lock2) {
        IO.println("1. Acquiring locks for thread: " + Thread.currentThread().getName());
        try {
            lock1.lock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IO.println("1. Acquired lock1 for thread: " + Thread.currentThread().getName());
        IO.println("2. Acquiring lock2 for thread: " + Thread.currentThread().getName());

        try {
            lock2.lockInterruptibly();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IO.println("2. Acquired lock2 for thread: " + Thread.currentThread().getName());
        lock1.unlock();
        IO.println("Unlocked lock1 for thread: " + Thread.currentThread().getName());
        lock2.unlock();
        IO.println("Unlocked lock2 for thread: " + Thread.currentThread().getName());

    }
}
