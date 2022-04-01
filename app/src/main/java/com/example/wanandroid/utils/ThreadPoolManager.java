package com.example.wanandroid.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public  class ThreadPoolManager {

    private volatile boolean RUNNING = true;// 是否正在运行
    private final ReentrantLock mLock = new ReentrantLock();// 并发锁
    private final HashSet<Worker> mWorkers = new HashSet<>();// 不重复的工作集
    private static BlockingQueue<Runnable> mQueue = null;// 任务阻塞队列
    private final ArrayList<Thread> mThreads = new ArrayList<>();// 线程工厂
    private final int mPoolSize = 5;// 线程池的核心线程数
    private volatile int mCoreSize;// 当前线程池中的线程数
    private volatile boolean isShutdown = false;// 是否停止工作
    private static ThreadPoolManager mThreadPool = null;

    private ThreadPoolManager() {
        mQueue = new ArrayBlockingQueue<>(mPoolSize);
    }

    public static ThreadPoolManager getInstance(){
        if(mThreadPool == null){
            mThreadPool = new ThreadPoolManager();
        }
        return mThreadPool;
    }

    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }

        if (mCoreSize < mPoolSize) {
            addThread(command);
        } else {
            try {
                mQueue.put(command);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addThread(Runnable task) {
        mLock.lock();
        try {
            mCoreSize++;
            Worker worker = new Worker(task);
            mWorkers.add(worker);
            Thread thread = new Thread(worker);
            mThreads.add(thread);
            thread.start();
        } finally {
            mLock.unlock();
        }
    }

    public void shutdown() {
        RUNNING = false;
        if (!mWorkers.isEmpty()) {
            for (Worker worker : mWorkers) {
                worker.interruptIfIdle();
            }
        }
        isShutdown = true;
        Thread.currentThread().interrupt();
    }

    private final class Worker implements Runnable {

        public Worker(Runnable task) {
            mQueue.offer(task);
        }

        public Runnable getTask() throws InterruptedException {
            return mQueue.take();
        }

        @Override
        public void run() {
            while (true && RUNNING) {
                if (isShutdown) {
                    Thread.interrupted();
                }
                Runnable task = null;
                try {
                    task = getTask();
                    task.run();
                } catch (InterruptedException e) {

                }
            }
        }

        public void interruptIfIdle() {
            for (Thread thread : mThreads) {
                Log.d("bugCatch", "interruptIfIdle: "+thread.getName() + " interrupt");
                thread.interrupt();
            }
        }
    }
}
