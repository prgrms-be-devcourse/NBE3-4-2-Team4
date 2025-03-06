package com.NBE3_4_2_Team4.standard.util.test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.function.Supplier

object ConcurrencyTestUtil {
    @JvmStatic
    fun <T> execute(threadCount: Int, task: Supplier<T>) {
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val barrier = CyclicBarrier(threadCount)

        for (i in 0..<threadCount) {
            executorService.submit {
                try {
                    barrier.await()
                    task.get()
                } catch (e: Exception) {
                    throw RuntimeException(e)
                } finally {
                    latch.countDown()
                }
            }
        }

        try {
            latch.await()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        executorService.shutdown()
    }
}
