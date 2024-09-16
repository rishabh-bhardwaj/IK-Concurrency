import threading
import time

# Initialize semaphores
semaphoreA = threading.Semaphore(1)
semaphoreB = threading.Semaphore(1)

def thread1():
    global semaphoreA, semaphoreB
    with semaphoreA:
        time.sleep(2)  # Cause the thread to sleep to increase the chance of deadlock
        with semaphoreB:
            # Do some work requiring both locks
            print("Thread 1 acquired both semaphores")

def thread2():
    global semaphoreA, semaphoreB
    with semaphoreB:
        time.sleep(2)  # Cause the thread to sleep to increase the chance of deadlock
        with semaphoreA:
            # Do some work requiring both locks
            print("Thread 2 acquired both semaphores")

def main():
    # Create threads
    t1 = threading.Thread(target=thread1)
    t2 = threading.Thread(target=thread2)

    # Start threads
    t1.start()
    t2.start()

    # Join threads (this will hang indefinitely if there's a deadlock)
    t1.join()
    t2.join()

if __name__ == "__main__":
    main()
