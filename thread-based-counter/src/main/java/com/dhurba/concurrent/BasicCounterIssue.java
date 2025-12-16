package com.dhurba.concurrent;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class BasicCounterIssue {
    int count = 0;

    static void main() throws InterruptedException {
        BasicCounterIssue counter = new BasicCounterIssue();
        for (int i = 0; i < 10; i++) {
            Runnable runnable = counter::increment;
            new Thread(runnable).start();
        }
        Thread.sleep(100);
        // Unpredictable output
        System.out.printf("Final count: %d, expected: %d\n", counter.getCount(), 10000);
    }

    public void increment() {
        for (int i = 0; i < 1000; i++)
            count++;
        System.out.printf("Count: %d\n", count);
    }

    public int getCount() {
        return count;
    }
}
