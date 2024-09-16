
import java.util.ArrayList;

import java.util.LinkedList;

import java.util.List;

import java.util.Queue;

import java.util.concurrent.BrokenBarrierException;

import java.util.concurrent.CyclicBarrier;

import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;




public class FairThreadScheduling {




  private static class LegalReentrantLock {

    private final Lock mutex = new ReentrantLock();

    private final Queue<Condition> lockQueue = new LinkedList<>();

    private boolean isLocked = false;




    public void lock() throws InterruptedException {

      mutex.lock();




      try {

        while (isLocked) {

          Condition condition = mutex.newCondition();

          lockQueue.add(condition);

          condition.await();

        }

        isLocked = true;

      } finally {

        mutex.unlock();

      }

    }




    public void unlock() {

      mutex.lock();




      isLocked = false;

      if (lockQueue.size() > 0) {

        Condition condition = lockQueue.remove();

        condition.signal();

      }

      mutex.unlock();

    }

  }




  public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

    LegalReentrantLock legalReentrantLock = new LegalReentrantLock();

    List<Thread> threadList = new ArrayList<>();




    for (int i = 0; i < 10; i++) {

      int finalI = i;

      Thread t = new Thread(() -> {

        try {

          legalReentrantLock.lock();

        } catch (InterruptedException e) {

          throw new RuntimeException(e);

        }

        legalReentrantLock.unlock();

        System.out.println("Thread " + finalI + " released lock");

      });




      threadList.add(t);

    }







    for (Thread t : threadList) {

      t.start();

    }




    for (Thread t : threadList) {

      t.join();

    }

  }

}



