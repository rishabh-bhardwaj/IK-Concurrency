package com.ik;

public class RaceConditionDemo {
  private static int count = 0;

  public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
      count++;
    });
    Thread t2 = new Thread(() -> {
      count++;
    });

    t1.start();
    t2.start();

    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Count: " + count);
  }
}