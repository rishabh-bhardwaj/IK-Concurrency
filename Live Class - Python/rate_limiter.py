#Slide 50 : Rate Limit Implementation
import time
import threading
from functools import wraps

class RateLimiter:
  def __init__(self, max_calls, period):
    self.max_calls = max_calls
    self.period = period
    self.counter = 0
    self.lock = threading.Lock()
    self.last_reset_time = time.time()

  def is_allowed(self):
    current_time = time.time()

    with self.lock:
      # Check if the time period has passed since the last reset
      if current_time - self.last_reset_time > self.period:
        self.counter = 0
        self.last_reset_time = current_time

      if self.counter < self.max_calls:
        self.counter += 1
        return True
      else:
        return False

  def limit(self, func):
    @wraps(func)
    def wrapper(*args, **kwargs):
      if self.is_allowed():
        return func(*args, **kwargs)
      else:
        print(f"Rate limit exceeded. Try again later.")
    return wrapper
    
rate_limiter = RateLimiter(max_calls=5, period=1)

@rate_limiter.limit
def my_function():
  print(f"Function executed by {threading.current_thread().name}")

if __name__ == "__main__":
  def worker():
    for i in range(10):
      my_function()
      time.sleep(0.2)  # Simulate work being done in the function

  # Create multiple threads to simulate concurrent function calls
  threads = [threading.Thread(target=worker, name=f"Thread-{i}") for i in range(3)]

  # Start all threads
  for thread in threads:
    thread.start()

  # Wait for all threads to finish
  for thread in threads:
    thread.join()
