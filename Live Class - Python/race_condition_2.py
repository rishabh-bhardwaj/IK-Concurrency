#Race condition 2 : Slide 26
import threading

counter = 0
# Function to increment the counter
def increment_counter():
  global counter
  temp = counter
  temp += 1
  counter = temp

t1 = threading.Thread(target=increment_counter)
t2 = threading.Thread(target=increment_counter)

t1.start()
t2.start()

t1.join()
t2.join()

print("Final counter value:", counter)

