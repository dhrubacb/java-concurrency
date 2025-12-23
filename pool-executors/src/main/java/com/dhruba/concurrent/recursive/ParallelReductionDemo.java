package com.dhruba.concurrent.recursive;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class ParallelReductionDemo {

    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();

    static void main() {
        int length = 1000000000; // after this threshold the difference between seq and parallel is visible
        int[] array = new int[length];
        Arrays.fill(array, 15);
        long startMilli = System.currentTimeMillis();
        long sum = 0L;
        for (int i = 0; i < length; i++) {
            sum += array[i];
        }
        long endMilli = System.currentTimeMillis();
        log.info("Result: {}, Total time taken in seq: {}", sum, endMilli - startMilli);


        startMilli = System.currentTimeMillis();

        ParallelSum parallelSum = new ParallelSum(0, length, array);
        Long res = forkJoinPool.invoke(parallelSum);
        endMilli = System.currentTimeMillis();
        log.info("Result: {}, Total time taken in parallel: {}", res, endMilli - startMilli);
    }
}
