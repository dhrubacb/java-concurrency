package com.dhurba.concurrent;

import java.util.LinkedList;
import java.util.List;

public class WithNotifyWait {
    private final Object LOCK = new Object();
    private final List<Integer> list = new LinkedList<>();
    private static final int SIZE = 2;

    public static void main(String[] args) {
        WithNotifyWait obj = new WithNotifyWait();
        new Thread(obj.new Producer()).start();
        new Thread(obj.new Consumer()).start();
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (LOCK) {
                    while (list.isEmpty()) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.removeFirst();
                    System.out.println("Consumed");
                    LOCK.notify();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Producer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (LOCK) {
                    while (list.size() == SIZE) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(1);
                    System.out.println("Produced");
                    LOCK.notify();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
