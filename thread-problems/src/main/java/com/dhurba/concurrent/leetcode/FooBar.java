package com.dhurba.concurrent.leetcode;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class FooBar {
    private int n;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    boolean b = false;

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            lock.lock();
            // printFoo.run() outputs "foo". Do not change or remove this line.
            while (b) condition.await();
            printFoo.run();
            b = true;
            condition.signal();
            lock.unlock();
        }

    }

    public void bar(Runnable printBar) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            lock.lock();
            while (!b) {
                condition.await();
            }
            // printBar.run() outputs "bar". Do not change or remove this line.
            printBar.run();
            b = false;
            condition.signal();
            lock.unlock();
        }
    }

    // Problem: Print FooBar n times
    static void main() {
        FooBar fooBar = new FooBar(3);
        Runnable printFoo = () -> IO.print("Foo");
        Runnable printBar = () -> IO.print("Bar");
        new Thread(() -> {
            try {
                fooBar.foo(printFoo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                fooBar.bar(printBar);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}