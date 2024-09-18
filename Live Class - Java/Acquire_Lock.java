import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Acquire_Lock {
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(Acquire_Lock::thread1Routine);
        Thread t2 = new Thread(Acquire_Lock::thread2Routine);

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

    private static void thread1Routine() {
        try {
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void thread2Routine() {
        try {
            lock1.lock();
            try {
                System.out.println("T2: lock1 acquired");
                Thread.sleep(1000); // Simulate work
                lock2.lock();
                try {
                    System.out.println("T2: lock2 acquired");
                } finally {
                    lock2.unlock();
                }
            } finally {
                lock1.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
