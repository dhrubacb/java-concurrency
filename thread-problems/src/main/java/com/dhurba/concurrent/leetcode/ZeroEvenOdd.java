package com.dhurba.concurrent.leetcode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class ZeroEvenOdd {
    private final int n;
    int x = 1;
    Lock lock = new ReentrantLock();
    boolean b = true;
    Condition even = lock.newCondition();
    Condition zero = lock.newCondition();

    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (x <= n) {
            lock.lock();
            while (!b) zero.await();
            if (x <= n) {
                printNumber.accept(0);
                b = false;
                even.signal();
            }
            lock.unlock();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        while (x <= n) {
            lock.lock();
            while (x % 2 != 0 && b) even.await();
            if (x <= n && x %2 == 0 && !b) {
                printNumber.accept(x);
                x++;
                b = true;
                zero.signal();
            }
            lock.unlock();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        while (x <= n) {
            lock.lock();
            while (x % 2 != 1 && b) even.await();
            if (x <= n && x %2 == 1 && !b) {
                printNumber.accept(x);
                x++;
                b = true;
                zero.signal();
            }
            lock.unlock();
        }
    }

    static void main() {
        ZeroEvenOdd o = new ZeroEvenOdd(21);
        new Thread(() -> {
            try {
                o.odd(IO::print);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                o.even(IO::print);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                o.zero(IO::print);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

}