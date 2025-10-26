package com.chugyoyo.db.trace;

import java.util.concurrent.*;

public class TraceableExecutorService extends ThreadPoolExecutor {

    public TraceableExecutorService(int corePoolSize, int maximumPoolSize,
                                    long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new TraceableRunnable(command));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new TraceableCallable<>(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new TraceableRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(new TraceableRunnable(task), result);
    }

    public static class TraceableCallable<V> implements Callable<V> {
        private final Callable<V> task;
        private final String traceId;

        public TraceableCallable(Callable<V> task) {
            this.task = task;
            // 父线程传递到子线程
            this.traceId = TraceContext.get();
        }

        @Override
        public V call() throws Exception {
            try {
                TraceContext.set(traceId);
                return task.call();
            } finally {
                TraceContext.clear();
            }
        }
    }


    public static class TraceableRunnable implements Runnable {
        private final Runnable task;
        private final String traceId;

        public TraceableRunnable(Runnable task) {
            this.task = task;
            this.traceId = TraceContext.get();
        }

        @Override
        public void run() {
            try {
                TraceContext.set(traceId);
                task.run();
            } finally {
                TraceContext.clear();
            }
        }
    }
}
