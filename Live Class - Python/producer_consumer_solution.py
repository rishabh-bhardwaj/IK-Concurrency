#Producer Consumer Solution : Slide 50
import threading
import time
import random

# Shared resource
buffer = []
buffer_size = 10

# Condition object for synchronization
condition = threading.Condition()

class Producer(threading.Thread):
    def run(self):
        global buffer
        while True:
            item = random.randint(1, 100)
            condition.acquire()
            while len(buffer) == buffer_size:
                condition.wait()
            buffer.append(item)
            print(f"Produced: {item}")
            condition.notify()
            condition.release()
            time.sleep(random.uniform(0.1, 1))
class Consumer(threading.Thread):
    def run(self):
        global buffer
        while True:
            condition.acquire()
            while not buffer:
                condition.wait()
            item = buffer.pop(0)
            print(f"Consumed: {item}")
            condition.notify()
            condition.release()
            time.sleep(random.uniform(0.1, 1))
if __name__ == "__main__":
    producer = Producer()
    consumer = Consumer()
    
    producer.start()
    consumer.start()
    
    producer.join()
    consumer.join()


