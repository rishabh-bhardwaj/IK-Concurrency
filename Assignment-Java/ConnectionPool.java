

import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;




public abstract class ConnectionPool<T> {

  private ConcurrentLinkedQueue<T> pool;

  private ScheduledExecutorService executorService;




  public ConnectionPool(final int minIdle, final int maxIdle, final long validationIntervalSeconds) {

    initialize(minIdle);




    executorService = Executors.newSingleThreadScheduledExecutor();

    executorService.scheduleWithFixedDelay(() -> {

      int size = pool.size();

      if (size < minIdle) {

        int sizeToBeAdded = minIdle - size;

        for (int i = 0; i < sizeToBeAdded; i++) {

          pool.add(createObject());

        }

      } else if (size > maxIdle) {

        int sizeToBeRemoved = size - maxIdle;

        for (int i = 0; i < sizeToBeRemoved; i++) {

          pool.poll();

        }

      }

    }, validationIntervalSeconds, validationIntervalSeconds, TimeUnit.SECONDS);

  }







  public T borrowObject() {

    T object;

    if ((object = pool.poll()) == null) {

      System.out.println("Creating new connection as non available for reuse");

      object = createObject();

    } else {

      System.out.println("Reusing existing connection");

    }




    return object;

  }







  public void returnObject(T object) {

    if (object == null) {

      return;

    }




    System.out.println("Returning connection to pool");




    this.pool.offer(object);

  }







  public void shutdown() {

    if (executorService != null) {

      executorService.shutdown();

    }

  }




  protected abstract T createObject();




  private void initialize(final int minIdle) {

    pool = new ConcurrentLinkedQueue<>();




    for (int i = 0; i < minIdle; i++) {

      pool.add(createObject());

    }

  }




  public static void main(String[] args) {

    class MockConnectionPool extends ConnectionPool<String> {

      public MockConnectionPool(int minIdle, int maxIdle, long validationInterval) {

        super(minIdle, maxIdle, validationInterval);

      }




      @Override

      protected String createObject() {

        return "Mock Connection using String";

      }

    }




    MockConnectionPool mockConnectionPool = new MockConnectionPool(0,5, 5);




    String connection1 = mockConnectionPool.borrowObject();

    String connection2 = mockConnectionPool.borrowObject();

    String connection3 = mockConnectionPool.borrowObject();

    mockConnectionPool.returnObject(connection1);

    mockConnectionPool.returnObject(connection2);

    mockConnectionPool.borrowObject();







    mockConnectionPool.shutdown();




  }

}

