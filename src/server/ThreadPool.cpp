#include "ThreadPool.h"

ThreadPool::ThreadPool(size_t numThreads) : stop(false)
{
    // Create a thread pool with a given num of threads
    for (int threadInd = 0; threadInd < numThreads; ++threadInd)
    {
        workers.emplace_back([this]() {
             while (true)
            {
                std::function<void()> task;
                {
                    // Locking the queue - we want to pop task
                    std::unique_lock<std::mutex> lock(queueMutex);

                    // Wait until there's a task in the queue or stop flag is true
                    condition.wait(lock, [this]() { return stop || !tasks.empty(); });

                    // If stop flag is true and there's no more tasks, break the loop and kill the thread
                    if (stop && tasks.empty())
                        return;

                    // Pull task from the queue
                    task = std::move(tasks.front());
                    tasks.pop();
                }

                // Execute the task
                task();
            }
        });

    }
}
void ThreadPool::enqueue(std::function<void()> task) 
{
    {
        // Lock because we enter a task to the queue
        std::unique_lock<std::mutex> lock(queueMutex);
        tasks.emplace(std::move(task));
    }
    condition.notify_one();
}

ThreadPool::~ThreadPool() 
{
    {
        // Change stop to true
        std::unique_lock<std::mutex> lock(queueMutex);
        stop = true;
    }
        // Wake up the threads
        condition.notify_all();
        for (std::thread &worker : workers) {
            if (worker.joinable()) {
                worker.join();
            }
        }
}