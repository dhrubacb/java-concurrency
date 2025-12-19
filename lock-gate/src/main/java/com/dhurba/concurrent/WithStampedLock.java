package com.dhurba.concurrent;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WithStampedLock {
    private int balance;
    private final StampedLock lock;

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

        log.info("--Closing Ledger with balance: {}", withStampedLock.balance);
    }

    private void deposit(int amt) {
        long l = lock.writeLock();
        balance += amt;
        log.info("Done Depositing, balance: {}", balance);
        lock.unlock(l);
    }

    private void inquireBalance() {
        long l = lock.tryOptimisticRead();
        if (!lock.validate(l)) {
            l = lock.readLock();
        }
        log.info("Current Balance: " + balance);

        lock.unlock(l);
    }

    private void withdraw(int amt) {
        long l = lock.readLock();
        log.info("Got read lock, l: {}", l);
        log.info("Withdrawing from Current Balance: {}", balance);
        if (balTooLow(amt)) {
            lock.unlockRead(l);
            return;
        }

        long wls = lock.tryConvertToWriteLock(l);
        log.info("Tried to convert to writeLockStamp : {}, from readLockStamp: {}", wls, l);
        if (wls == 0) {
            boolean tryUnlockRead = lock.tryUnlockRead(); // gives prev state
            log.info("WasReadLock: {} couldn't be converted to Write", tryUnlockRead);
            wls = lock.writeLock();
        }

        log.info("Now got the write lock : {}", wls);
        if (balTooLow(amt)) { // Double-checking after acquiring the write lock
            lock.unlockWrite(wls);
            return;
        }
        balance -= amt;
        log.info("Done Withdrawing, balance: {}", balance);
        if (StampedLock.isWriteLockStamp(wls)) {
            log.info("Unlocking write : {}", wls);
            lock.unlockWrite(wls);
        }
    }

    private boolean balTooLow(int amt) {
        if (balance - amt < 0) {
            log.info("Balance too low to withdraw");
            return true;
        }
        return false;
    }

}
