import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                thread1Routine();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                thread2Routine();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        // Join the threads
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Done");
    }

    private static void thread1Routine() throws InterruptedException {
        lock1.lock();
        try {
            System.out.println("T1: lock1 acquired");
            Thread.sleep(1000); // Simulate work
            lock2.lock();
            try {
                System.out.println("T1: lock2 acquired");
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }
    }

    private static void thread2Routine() throws InterruptedException {
        lock2.lock();
        try {
            System.out.println("T2: lock2 acquired");
            Thread.sleep(1000); // Simulate work
            lock1.lock();
            try {
                System.out.println("T2: lock1 acquired");
            } finally {
                lock1.unlock();
            }
        } finally {
            lock2.unlock();
        }
    }
}
