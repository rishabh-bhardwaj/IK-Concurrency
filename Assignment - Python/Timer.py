import heapq
import threading
import time
import random
from concurrent.futures import ThreadPoolExecutor
from typing import List, Tuple

class TimerTask:
    def __init__(self, delay_ms: float, task_id: int):
        self.task_id = task_id
        self.schedule_time_epoch_ms = int(time.time() * 1000 + delay_ms)

    def get_schedule_time_epoch_ms(self) -> int:
        return self.schedule_time_epoch_ms

    def run(self):
        print(f"Running some task. Task Id: {self.task_id}")

class TimerScheduler:
    def __init__(self, num_threads: int):
        self.tasks = []
        self.lock = threading.Lock()
        self.empty_condition = threading.Condition(self.lock)
        self.is_shutting_down = False
        self.thread_pool_executor = ThreadPoolExecutor(max_workers=num_threads)
        self.scheduler_thread = threading.Thread(target=self.scheduler_loop)
        self.scheduler_thread.start()

    def enqueue_task(self, task: TimerTask) -> bool:
        with self.lock:
            if self.is_shutting_down:
                return False
            heapq.heappush(self.tasks, (task.get_schedule_time_epoch_ms(), task))
            self.empty_condition.notify()
            return True

    def shut_down(self):
        with self.lock:
            self.is_shutting_down = True
            self.empty_condition.notify_all()
        self.scheduler_thread.join()
        self.thread_pool_executor.shutdown()

    def scheduler_loop(self):
        while True:
            with self.lock:
                while not self.tasks and not self.is_shutting_down:
                    self.empty_condition.wait()

                if self.is_shutting_down and not self.tasks:
                    break

                current_time_ms = int(time.time() * 1000)

                if self.tasks and self.tasks[0][0] <= current_time_ms:
                    _, task = heapq.heappop(self.tasks)
                    self.thread_pool_executor.submit(task.run)
                else:
                    next_wake_up_time_ms = self.tasks[0][0] if self.tasks else None

                    if next_wake_up_time_ms:
                        wait_time_ms = max(0, next_wake_up_time_ms - current_time_ms)
                        self.empty_condition.wait(timeout=wait_time_ms / 1000.0)

def main():
    timed_task_scheduler = TimerScheduler(num_threads=5)
    timer_task_list: List[TimerTask] = []
    rand = random.Random()

    for i in range(10):
        task_start_delay_time_ms = rand.random() * 5000
        print(f"Timer task id: {i} will start at epoch time of {task_start_delay_time_ms:.2f}")
        timer_task_list.append(TimerTask(task_start_delay_time_ms, i))

    for task in timer_task_list:
        timed_task_scheduler.enqueue_task(task)

    timed_task_scheduler.shut_down()

if __name__ == "__main__":
    main()
