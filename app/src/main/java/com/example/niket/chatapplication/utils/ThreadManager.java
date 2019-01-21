package com.example.niket.chatapplication.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vishnuprasad on 23/10/17.
 */

public class ThreadManager {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 5;
    private static ThreadManager sInstance;
    private static Handler uiHandler;

    static {
        sInstance = new ThreadManager();
    }

    public final BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingDeque<>();
    public ThreadPoolExecutor threadPoolExecutor;

    private ThreadManager() {
        uiHandler = new Handler(Looper.getMainLooper());
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                mWorkQueue, new CustomThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    }

    public static ThreadManager getInstance() {

        return sInstance;
    }

    public Future<?> doWork(Runnable runnable) {
        return sInstance.threadPoolExecutor.submit(runnable);
    }

    public static abstract class CustomRunnable implements Runnable {

        public abstract void onBackground();

        public void onUi() {
        }

        @Override
        public final void run() {
            onBackground();
            if (uiHandler != null) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onUi();
                    }
                });
            }

        }
    }

    private static class CustomThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "QurekaPool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(final Runnable r) {
            Runnable wrapperRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    } catch (Throwable t) {

                    }
                    r.run();
                }
            };
            return new Thread(group, wrapperRunnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        }
    }
}