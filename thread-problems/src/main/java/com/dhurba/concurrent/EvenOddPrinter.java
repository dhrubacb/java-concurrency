package com.dhurba.concurrent;

import lombok.SneakyThrows;

public class EvenOddPrinter {
    private int counter = 1;
    private final int limit = 10;
    private final Object lock = new Object();

    public void printNumbers() {
        Thread oddThread = new Thread(() -> print(1), "Odd-Thread");
        Thread evenThread = new Thread(() -> print(0), "Even-Thread");

        oddThread.start();
        evenThread.start();
    }

    @SneakyThrows
    private void print(int remainder) {
        lock.wait();
        while (counter <= limit) {
            synchronized (lock) {
                // If it's not my turn, wait
                while (counter % 2 != remainder && counter <= limit) {
                    try { lock.wait(); } catch (InterruptedException e) {}
                }
                
                if (counter <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + counter);
                    counter++;
                    lock.notifyAll(); // Wake up the other thread
                }
            }
        }
    }

    public static void main(String[] args) {
        new EvenOddPrinter().printNumbers();
    }
}