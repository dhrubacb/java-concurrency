package com.dhurba.concurrent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class WithThreadLocal {
    static AtomicInteger atomicInteger = new AtomicInteger(1);
    // Tldr, for every thread access this threadLocal variable, initial(int v) will be call
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(atomicInteger::getAndIncrement);

    static void main() {
        WithThreadLocal withThreadLocal = new WithThreadLocal();
        new Thread(withThreadLocal::process).start();
        new Thread(withThreadLocal::process).start();
        new Thread(withThreadLocal::process).start();
        new Thread(withThreadLocal::process).start();
    }

    @SneakyThrows
    private void process() {
        int i = threadLocal.get();
        log.info("Thread: {}, before val: {}", Thread.currentThread().getName(), i);
        Thread.sleep(1000);
        log.info("Thread: {}, after val: {}", Thread.currentThread().getName(), threadLocal.get());
        threadLocal.remove();
    }

}
