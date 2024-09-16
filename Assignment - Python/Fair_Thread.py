import threading
from collections import deque
import random
import time

class LegalReentrantLock:
    def __init__(self):
        self._mutex = threading.Lock()
        self._lock_queue = deque()
        self._is_locked = False

    def lock(self):
        with self._mutex:
            while self._is_locked:
                condition = threading.Condition(self._mutex)
                self._lock_queue.append(condition)
                condition.wait()
            self._is_locked = True

    def unlock(self):
        with self._mutex:
            self._is_locked = False
            if self._lock_queue:
                condition = self._lock_queue.popleft()
                condition.notify()

def thread_function(lock, thread_id):
    try:
        lock.lock()
    finally:
        lock.unlock()
    print(f"Thread {thread_id} released lock")

def main():
    legal_reentrant_lock = LegalReentrantLock()
    threads = []

    for i in range(10):
        thread = threading.Thread(target=thread_function, args=(legal_reentrant_lock, i))
        threads.append(thread)
        thread.start()

    for thread in threads:
        thread.join()

if __name__ == "__main__":
    main()
