package com.gruffins.birch.utils

import java.util.concurrent.*

class TestExecutorService: AbstractExecutorService() {
    private var shutdown: Boolean = false

    override fun execute(p0: Runnable?) {
        p0?.run()
    }

    override fun shutdown() {
        shutdown = true
    }

    override fun shutdownNow(): MutableList<Runnable> {
        return mutableListOf()
    }

    override fun isShutdown(): Boolean {
        return shutdown
    }

    override fun isTerminated(): Boolean {
        return false
    }

    override fun awaitTermination(p0: Long, p1: TimeUnit?): Boolean {
        return false
    }
}