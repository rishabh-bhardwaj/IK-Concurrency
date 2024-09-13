import threading

count = 0
count_lock = threading.Lock()

def increment_count():
    global count
    for _ in range(10000):
        count += 1


t1 = threading.Thread(target=increment_count)
t2 = threading.Thread(target=increment_count)

t1.start()
t2.start()

t1.join()
t2.join()

print("Count:", count)