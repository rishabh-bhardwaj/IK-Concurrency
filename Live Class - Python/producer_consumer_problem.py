#Producer Consumer Problem : Slide 47
import threading
import time
import random

# Shared resource
buffer = []
buffer_size = 10

class Producer(threading.Thread):
    def run(self):
        global buffer
        while True:
            item = random.randint(1, 100)
            if len(buffer) < buffer_size:
                buffer.append(item)
                print(f"Produced: {item}")
            time.sleep(random.uniform(0.1, 1))

class Consumer(threading.Thread):
    def run(self):
        global buffer
        while True:
            if buffer:
                item = buffer.pop(0)
                print(f"Consumed: {item}")
            time.sleep(random.uniform(0.1, 1))
if __name__ == "__main__":
    producer = Producer()
    consumer = Consumer()
    
    producer.start()
    consumer.start()
    
    producer.join()
    consumer.join()
