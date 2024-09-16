package com.ik;

import java.util.concurrent.atomic.AtomicInteger;

public class RaceConditionSpinLockDemo {

  private static AtomicInteger count = new AtomicInteger(0);
  
public static void main(String[] args) {
    Runnable incrementCount = () -> {
      for (int i = 0; i < 10000; i++) {
        count.incrementAndGet(); 
      }
    };

    Thread t1 = new Thread(incrementCount);
    Thread t2 = new Thread(incrementCount);

    t1.start();
    t2.start();

    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Print the final value of the counter
    System.out.println("Count: " + count.get());
  }
}
