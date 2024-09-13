import threading

class BankAccount:
    def __init__(self):
        self.balance = 0
        self.lock = threading.RLock()  # Use ReentrantLock for mutual exclusion

    def deposit(self, amount):
        with self.lock:
            self.balance += amount
            print(f"{threading.current_thread().name} deposited {amount}, new balance: {self.balance}")

    def withdraw(self, amount):
        with self.lock:
            self.balance -= amount
            print(f"{threading.current_thread().name} withdrew {amount}, new balance: {self.balance}")

    def get_balance(self):
        return self.balance

def transfer(from_account, to_account, amount):
    # Acquire locks in a consistent order to prevent deadlock
    first, second = (from_account, to_account) if id(from_account) < id(to_account) else (to_account, from_account)
    
    with first.lock:
        print(f"{threading.current_thread().name} locked {first}")
        # Simulate some delay
        threading.Event().wait(1)
        with second.lock:
            print(f"{threading.current_thread().name} locked {second}")
            from_account.withdraw(amount)
            to_account.deposit(amount)

if __name__ == "__main__":
    account1 = BankAccount()
    account2 = BankAccount()

    # Define tasks to transfer money between accounts
    def task1():
        transfer(account1, account2, 50)

    def task2():
        transfer(account2, account1, 50)

    # Create and start threads
    t1 = threading.Thread(target=task1, name='Thread-1')
    t2 = threading.Thread(target=task2, name='Thread-2')

    t1.start()
    t2.start()

    t1.join()
    t2.join()

    print(f"Final balance of account1: {account1.get_balance()}")
    print(f"Final balance of account2: {account2.get_balance()}")
