import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class RateLimiter {
    private final int maxCalls;
    private final long period;
    private int counter;
    private long lastResetTime;
    private final Lock lock = new ReentrantLock();

    public RateLimiter(int maxCalls, long period) {
        this.maxCalls = maxCalls;
        this.period = period * 1000; // Convert seconds to milliseconds
        this.counter = 0;
        this.lastResetTime = System.currentTimeMillis();
    }

    public boolean isAllowed() {
        long currentTime = System.currentTimeMillis();

        lock.lock();
        try {
            // Check if the time period has passed since the last reset
            if (currentTime - lastResetTime > period) {
                counter = 0;
                lastResetTime = currentTime;
            }

            if (counter < maxCalls) {
                counter++;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public void limit(Runnable task) {
        if (isAllowed()) {
            task.run();
        } else {
            System.out.println("Rate limit exceeded. Try again later.");
        }
    }
}

public class Rate_Limiter {
    private static final RateLimiter rateLimiter = new RateLimiter(5, 1);

    public static void myFunction() {
        System.out.println("Function executed by " + Thread.currentThread().getName());
    }

    public static void worker() {
        for (int i = 0; i < 10; i++) {
            rateLimiter.limit(Rate_Limiter::myFunction);
            try {
                Thread.sleep(200); // Simulate work being done in the function
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        // Create multiple threads to simulate concurrent function calls
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(Rate_Limiter::worker, "Thread-" + i);
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
