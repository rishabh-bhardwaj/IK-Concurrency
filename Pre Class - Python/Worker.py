from threading import Thread
class Worker(Thread):
    def __init__(self, id):
        super(Worker, self).__init__()
        self._id = id
    def run(self):
        print("I am worker %d" % self._id)
t1 = Worker(1)
t2 = Worker(2)
t1.start(); 
t2.start()

# using function could be more flexible
def Worker(worker_id):
    print("I am worker %d" % worker_id)

from threading import Thread
t1 = Thread(target=Worker, args=(1,))
t2 = Thread(target=Worker, args=(2,))
t1.start()
