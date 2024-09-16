package com.ik;

import java.util.concurrent.locks.ReentrantLock;

import static com.ik.BankAccount.transfer;

class BankAccount {
    private int balance = 0;
    private final ReentrantLock lock = new ReentrantLock(); // ReentrantLock for thread safety

    // Method to deposit an amount into the account
    public void deposit(int amount) {
        lock.lock(); // Acquire the lock
        try {
            balance += amount;
            System.out.println(Thread.currentThread().getName() + " deposited " + amount + ", new balance: " + balance);
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Method to withdraw an amount from the account
    public void withdraw(int amount) {
        lock.lock(); // Acquire the lock
        try {
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ", new balance: " + balance);
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Method to get the current balance of the account
    public int getBalance() {
        return balance;
    }

    // Method to transfer money from one account to another
    public static void transfer(BankAccount fromAccount, BankAccount toAccount, int amount) {
        // Determine lock order based on account hash codes to avoid deadlock
        BankAccount firstLock = fromAccount.hashCode() < toAccount.hashCode() ? fromAccount : toAccount;
        BankAccount secondLock = fromAccount.hashCode() < toAccount.hashCode() ? toAccount : fromAccount;

        // Acquire locks in the determined order
        firstLock.lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " locked " + firstLock);
            secondLock.lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " locked " + secondLock);
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
            } finally {
                secondLock.lock.unlock(); // Release the second lock
            }
        } finally {
            firstLock.lock.unlock(); // Release the first lock
        }
    }

}

public class ReentrantLockBankTransactionDemo{

    public static void main(String[] args) {
        BankAccount account1 = new BankAccount();
        BankAccount account2 = new BankAccount();

        // Define tasks to transfer money between accounts
        Runnable task1 = () -> transfer(account1, account2, 100);
        Runnable task2 = () -> transfer(account2, account1, 50);

        // Create and start threads
        Thread t1 = new Thread(task1, "Thread-1");
        Thread t2 = new Thread(task2, "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final balance of account1: " + account1.getBalance());
        System.out.println("Final balance of account2: " + account2.getBalance());
    }
}
