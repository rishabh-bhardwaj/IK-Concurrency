

import java.util.concurrent.Semaphore;


public class LockOrderDeadlock {

  static Semaphore semaphoreA = new Semaphore(1);

  static Semaphore semaphoreB = new Semaphore(1);


  public static void thread1() throws InterruptedException {

    semaphoreA.acquire();

    Thread.sleep(2000); // cause the thread to sleep to ensure both threads get into deadlock

    semaphoreB.acquire();

    // do some work requiring both locks.

    semaphoreA.release();

    semaphoreB.release();

  }

  public static void thread2() throws InterruptedException {

    semaphoreB.acquire();

    Thread.sleep(2000); // cause the thread to sleep to ensure both threads get into deadlock

    semaphoreA.acquire();

    // do some work requiring both locks.

    semaphoreB.release();

    semaphoreA.release();

  }

  public static void main(String[] args) throws InterruptedException {

    Runnable r1 = () -> {

      try {

        thread1();

      } catch (InterruptedException e) {

        throw new RuntimeException(e);

      }

    };


    Runnable r2 = () -> {

      try {

        thread2();

      } catch (InterruptedException e) {

        throw new RuntimeException(e);

      }

    };




    Thread t1 = new Thread(r1);

    Thread t2 = new Thread(r2);

    t1.start();

    t2.start();




    // Threads will never join as they are in deadlock.

    // Program will hang indefinitely.




    t1.join();

    t2.join();

  }

}
