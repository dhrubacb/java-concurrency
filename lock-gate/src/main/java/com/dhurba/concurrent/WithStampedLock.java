package com.dhurba.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class WithStampedLock {
    static int balance = 10;

    static void main() throws InterruptedException {
        StampedLock lock = new StampedLock();
        ExecutorService executorService = Executors.newCachedThreadPool();
        Runnable deposit = () -> {
            deposit(lock);
        };
        Runnable inquiry = () -> {
            inquireBalance(lock);
        };
        Runnable withdraw = () -> {
            withdraw(lock);
        };

        executorService.submit(deposit);
        executorService.submit(withdraw);
        executorService.submit(withdraw);
        executorService.submit(withdraw);
        executorService.submit(deposit);
        executorService.submit(inquiry);
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        executorService.submit(inquiry);
        executorService.shutdown();
    }

    private static void deposit(StampedLock lock) {
        long l = lock.writeLock();
        balance += 10;
        System.out.println("Done Depositing, balance: " + balance);
        lock.unlock(l);
    }

    private static void inquireBalance(StampedLock lock) {
        long l = lock.tryOptimisticRead();
        if (!lock.validate(l)) {
            l = lock.readLock();
        }
        System.out.println("Current Balance: " + balance);
        lock.unlock(l);
    }

    private static void withdraw(StampedLock lock) {
        long l = lock.tryOptimisticRead();
        if (!lock.validate(l)) {
            l = lock.readLock();
        }
        System.out.println("Withdrawing from Current Balance: " + balance);
        if (balance - 10 < 0) {
            System.out.println("Balance too low to withdraw");
            return;
        }
        long wls = lock.writeLock();
        if (wls == 0) {
            sleep(10 + (int) (Math.random() * 10));
            lock.unlockRead(l);
            System.out.println("Read Lock couldn't be converted to Write");
            wls = lock.writeLock();
        }
        System.out.println("Now got the write lock");
        balance -= 10;
        System.out.println("Done Withdrawing, balance: " + balance);
        lock.unlockRead(l);
        lock.unlock(wls);
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
