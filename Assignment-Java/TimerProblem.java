import java.util.ArrayList;

import java.util.Comparator;

import java.util.List;

import java.util.PriorityQueue;

import java.util.Random;

import java.util.concurrent.LinkedBlockingQueue;

import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.Lock;

import java.util.concurrent.locks.ReentrantLock;




public class TimerProblem {




  /**

   * Tracks when a task needs to be executed

   */

  static class TimerTask implements Runnable {

    private final int taskId;

    private long scheduleTimeEpochMs;




    TimerTask(double delayMs, int taskId) {

      this.taskId = taskId;

      long nowMs = System.currentTimeMillis();

      this.scheduleTimeEpochMs = (long) (nowMs + delayMs);

    }




    long getScheduleTimeEpochMs() {

      return scheduleTimeEpochMs;

    }




    @Override

    public void run() {

      System.out.println("Running some task. Task Id: " + taskId);

    }

  }




  static class TimerScheduler {

    Thread schedulerThread;

    ThreadPoolExecutor threadPoolExecutor;

    PriorityQueue<TimerTask> tasks;

    Lock mutex = new ReentrantLock();

    Condition emptyCondition = mutex.newCondition();

    Condition newEarlyTaskCondition = mutex.newCondition();




    boolean isShuttingDown = false;




    TimerScheduler(int numThreads) {

      tasks = new PriorityQueue<>(Comparator.comparingLong(a -> a.scheduleTimeEpochMs));




      schedulerThread = new Thread(() -> {

        try {

          schedulerLoop();

        } catch (InterruptedException e) {

          throw new RuntimeException(e);

        }

      });

      schedulerThread.start();




      threadPoolExecutor = new ThreadPoolExecutor(numThreads, numThreads, 5, TimeUnit.SECONDS,

          new LinkedBlockingQueue<>());




    }







    boolean enqueueTask(TimerTask task) {

      mutex.lock();




      if (isShuttingDown) {

        mutex.unlock();

        return false;

      }




      tasks.add(task);

      emptyCondition.signal();




      long curMinTaskScheduleTime = tasks.peek().getScheduleTimeEpochMs();




      if (task.getScheduleTimeEpochMs() < curMinTaskScheduleTime) {

        newEarlyTaskCondition.signal();

      }

      mutex.unlock();

      return true;

    }




    void shutDown() throws InterruptedException {

      mutex.lock();

      if (isShuttingDown) {

        mutex.unlock();

        return;

      }




      isShuttingDown = true;

      emptyCondition.signal();//???????

      mutex.unlock();

      schedulerThread.join();

      threadPoolExecutor.shutdown();

    }




    private void schedulerLoop() throws InterruptedException {

      mutex.lock();

      while(true) {

        while (tasks.size() == 0 && !isShuttingDown) {

          emptyCondition.await();

        }




        if (tasks.size() == 0 && isShuttingDown) {

          mutex.unlock();

          break;

        }




        while(System.currentTimeMillis() > tasks.peek().getScheduleTimeEpochMs()) {

          long nextWakeUpTimeMs = tasks.peek().getScheduleTimeEpochMs();

          newEarlyTaskCondition.await(nextWakeUpTimeMs, TimeUnit.MILLISECONDS);

        }




        TimerTask task = tasks.poll();

        threadPoolExecutor.execute(task);

      }

    }

  }




  public static void main(String[] args) throws InterruptedException {

    TimerScheduler timedTaskScheduler = new TimerScheduler(5);




    List<TimerTask> timerTaskList = new ArrayList<>();




    Random rand = new Random();

    for (int i = 0; i < 10; i++) {

      double taskStartDelayTimeMs = rand.nextDouble() * 5000;




      System.out.println("Timer task id: "+ i + " will start at epoch time of " + taskStartDelayTimeMs);

      timerTaskList.add(new TimerTask(taskStartDelayTimeMs, i));

    }




    for (TimerTask t : timerTaskList) {

      timedTaskScheduler.enqueueTask(t);

    }




    timedTaskScheduler.shutDown();

  }

}

