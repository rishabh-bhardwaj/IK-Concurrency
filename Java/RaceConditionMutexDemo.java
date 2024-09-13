package com.ik;

class Counter {
    private int counter = 0;
    public synchronized void increment() {
        counter++;
    }
    public int getValue() {
        return counter;
    }
}

public class RaceConditionMutexDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter(); // Create the counter object
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(counter::increment);         }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Final counter value: " + counter.getValue());
    }
}
