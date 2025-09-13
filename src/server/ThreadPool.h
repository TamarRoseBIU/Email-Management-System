// ThreadPool.h
#pragma once
#include <vector>
#include <queue>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <atomic>

class ThreadPool {
public:
    // Create a thread pool with a given num of threads
    ThreadPool(size_t numThreads);
    // Delete thread pool and release of resources
    ~ThreadPool();
    // add a task to the queue
    void enqueue(std::function<void()> task);

private:
    // threads that do the work
    std::vector<std::thread> workers;
    // tasks in queue - waiting to run
    std::queue<std::function<void()>> tasks;
    // mutex for safe access to the queue
    std::mutex queueMutex;
    // condition variable to notify threads
    std::condition_variable condition;
    // flag to stop all threads
    std::atomic<bool> stop;
};
