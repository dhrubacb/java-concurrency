package com.dhurba.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

public class WithStampedLock {
    int balance;
    final StampedLock lock;

    public WithStampedLock(int balance, StampedLock stampedLock) {
        this.balance = balance;
        this.lock = stampedLock;
    }

    static void main() {
        WithStampedLock withStampedLock;
        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
            withStampedLock = new WithStampedLock(10, new StampedLock());
            Runnable deposit = () -> withStampedLock.deposit(10);
            Runnable inquiry = withStampedLock::inquireBalance;
            Runnable withdraw = () -> withStampedLock.withdraw(10);

            executorService.submit(deposit);
            executorService.submit(withdraw);
            executorService.submit(withdraw);
            executorService.submit(withdraw);
            executorService.submit(inquiry);
        }
        System.out.println("--Closing Ledger with balance: " + withStampedLock.balance);
    }

    private void deposit(int amt) {
        long l = lock.writeLock();
        balance += amt;
        System.out.println("Done Depositing, balance: " + balance);
        lock.unlock(l);
    }

    private void inquireBalance() {
        long l = lock.tryOptimisticRead();
        if (!lock.validate(l)) {
            l = lock.readLock();
        }
        System.out.println("Current Balance: " + balance);

        lock.unlock(l);
    }

    private void withdraw(int amt) {
        long l = lock.readLock();
        System.out.printf("Got read lock, l: %s\n", l);
        System.out.println("Withdrawing from Current Balance: " + balance);
        if (balTooLow(amt)) {
            lock.unlockRead(l);
            return;
        }

        long wls = lock.tryConvertToWriteLock(l);
        System.out.printf("Tried to convert to writeLockStamp : %s, from readLockStamp: %s\n", wls, l);
        if (wls == 0) {
            boolean tryUnlockRead = lock.tryUnlockRead(); // gives prev state
            System.out.println("Read Lock: " + tryUnlockRead + " couldn't be converted to Write");
            wls = lock.writeLock();
        }

        System.out.println("Now got the write lock :" + wls);
        if (balTooLow(amt)) { // Double-checking after acquiring the write lock
            lock.unlockWrite(wls);
            return;
        }
        balance -= amt;
        System.out.println("Done Withdrawing, balance: " + balance);
        if (StampedLock.isWriteLockStamp(wls)) {
            System.out.println("Unlocking write : " + wls);
            lock.unlockWrite(wls);
        }
    }

    private boolean balTooLow(int amt) {
        if (balance - amt < 0) {
            System.out.println("Balance too low to withdraw");
            return true;
        }
        return false;
    }

}
