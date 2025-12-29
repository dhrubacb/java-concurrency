package com.dhurba.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DeadlockDetector {
    char k;

    static void main() {
        detectDeadlocks();
    }

    public static Integer a(int i) {
        return 20;
    }

    public static Integer a(int i, int k) {
        return 10;
    }

    public static Integer a(int... i) {
        return 2;
    }

    public static void detectDeadlocks() {
        System.out.println((int) new DeadlockDetector().k);
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();

        if (deadlockedThreads != null) {
            ThreadInfo[] threadInfos = threadBean.getThreadInfo(deadlockedThreads);

            System.err.println("DEADLOCK DETECTED!");
            for (ThreadInfo info : threadInfos) {
                System.err.println("Thread: " + info.getThreadName());
                System.err.println("Locked on: " + info.getLockName());
                System.err.println("Waiting for: " + info.getLockOwnerName());
                System.err.println("Stack trace:");
                for (StackTraceElement element : info.getStackTrace()) {
                    System.err.println("  " + element);
                }
            }

            // Alert monitoring system
            IO.println("Deadlock detected!");
        }
    }
}