import threading
import time
from collections import deque
from concurrent.futures import ThreadPoolExecutor

class ConnectionPool:
    def __init__(self, min_idle, max_idle, validation_interval_seconds):
        self.pool = deque()
        self.min_idle = min_idle
        self.max_idle = max_idle
        self.validation_interval_seconds = validation_interval_seconds
        self.lock = threading.Lock()

        self.initialize(min_idle)
        self.executor = ThreadPoolExecutor(max_workers=1)
        self.schedule_validation()

    def schedule_validation(self):
        self.executor.submit(self.validation_task)

    def validation_task(self):
        while True:
            with self.lock:
                size = len(self.pool)

                if size < self.min_idle:
                    size_to_be_added = self.min_idle - size
                    for _ in range(size_to_be_added):
                        self.pool.append(self.create_object())
                elif size > self.max_idle:
                    size_to_be_removed = size - self.max_idle
                    for _ in range(size_to_be_removed):
                        if self.pool:
                            self.pool.popleft()
            time.sleep(self.validation_interval_seconds)

    def borrow_object(self):
        with self.lock:
            if self.pool:
                obj = self.pool.popleft()
                print("Reusing existing connection")
            else:
                print("Creating new connection as non available for reuse")
                obj = self.create_object()
        return obj

    def return_object(self, obj):
        if obj is not None:
            with self.lock:
                print("Returning connection to pool")
                self.pool.append(obj)

    def shutdown(self):
        self.executor.shutdown(wait=True)

    def initialize(self, min_idle):
        for _ in range(min_idle):
            self.pool.append(self.create_object())

    def create_object(self):
        raise NotImplementedError("Subclasses should implement this method.")

class MockConnectionPool(ConnectionPool):
    def create_object(self):
        return "Mock Connection using String"

if __name__ == "__main__":
    mock_connection_pool = MockConnectionPool(0, 5, 5)

    connection1 = mock_connection_pool.borrow_object()
    connection2 = mock_connection_pool.borrow_object()
    connection3 = mock_connection_pool.borrow_object()

    mock_connection_pool.return_object(connection1)
    mock_connection_pool.return_object(connection2)

    mock_connection_pool.borrow_object()

    time.sleep(10)  # Allow some time for the validation task to run

    mock_connection_pool.shutdown()
