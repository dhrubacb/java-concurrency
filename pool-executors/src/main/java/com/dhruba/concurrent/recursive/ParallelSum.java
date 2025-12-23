package com.dhruba.concurrent.recursive;

import java.util.concurrent.RecursiveTask;

public class ParallelSum extends RecursiveTask<Long> {
    private final int start;
    private final int end;
    private final int[] array;

    public ParallelSum(int start, int end, int[] array) {
        this.start = start;
        this.end = end;
        this.array = array;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        long THRESHOLD = 100000L;
        if (length < THRESHOLD) {
            long sum = 0L;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }

        int mid = start + (length >> 2);
        ParallelSum left = new ParallelSum(start, mid, array);
        ParallelSum right = new ParallelSum(mid, end, array);
        left.fork();
        Long rightSum = right.compute();
        Long leftSum = left.join();
        return leftSum + rightSum;
    }
}
