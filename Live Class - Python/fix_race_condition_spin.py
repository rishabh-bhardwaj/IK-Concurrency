import threading

# Shared counter
counter_spin = 0

class SpinLock:
    def __init__(self):
        self.locked = threading.Event()

    def acquire(self):
        while self.locked.is_set():  # Spin until the lock is free
            pass
        self.locked.set()  # Acquire the lock

    def release(self):
        self.locked.clear()  # Release the lock

# Spin lock to protect the shared counter
spin_lock = SpinLock()

def increment_counter_with_spinlock():
    global counter_spin
    spin_lock.acquire()  # Acquire spin lock
    counter_spin += 1  # Increment the counter
    spin_lock.release()  # Release spin lock

# Create multiple threads that will increment the counter once
threads_spin = [threading.Thread(target=increment_counter_with_spinlock) for _ in range(5)]

# Start threads
for t in threads_spin:
    t.start()

# Wait for all threads to complete
for t in threads_spin:
    t.join()

# Print the final value of the counter
print(f"Final counter value with spin lock: {counter_spin}")
