import threading

# Shared counter
counter = 0

# Mutex to protect the shared counter
mutex = threading.Lock()

def increment_counter_with_mutex():
    global counter
    with mutex:
        counter += 1

# Create multiple threads that will increment the counter once
threads = [threading.Thread(target=increment_counter_with_mutex) for _ in range(5)]

# Start threads
for t in threads:
    t.start()

# Wait for all threads to complete
for t in threads:
    t.join()

# Print the final value of the counter
print(f"Final counter value with mutex: {counter}")
