package org.fluentjava.volundr.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ExecutorServiceFactoryTest {

    @Test
    public void once() throws InterruptedException {
        final ExecutorService newFixedThreadPool = new ExecutorServiceFactory()
                .newFixedThreadPool(1, "my-thread");
        final AtomicInteger i = new AtomicInteger(0);
        newFixedThreadPool.execute(i::getAndIncrement);
        Thread.sleep(10);
        assertEquals(1, i.get());
    }
}
