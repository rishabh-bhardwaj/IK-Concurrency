import java.util.LinkedList;
import java.util.Random;

class Buffer {
    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final int bufferSize;

    public Buffer(int size) {
        this.bufferSize = size;
    }

    public synchronized void produce(int item) throws InterruptedException {
        while (buffer.size() == bufferSize) {
            wait(); // Wait until there's space in the buffer
        }
        buffer.add(item);
        System.out.println("Produced: " + item);
        notifyAll(); // Notify consumers that an item has been produced
    }

    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait(); // Wait until there's an item to consume
        }
        int item = buffer.removeFirst();
        System.out.println("Consumed: " + item);
        notifyAll(); // Notify producers that there's space in the buffer
        return item;
    }
}

class Producer extends Thread {
    private final Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            while (true) {
                int item = random.nextInt(100) + 1;
                buffer.produce(item);
                Thread.sleep((long) (random.nextDouble() * 900) + 100); // Sleep between 0.1 to 1 second
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer extends Thread {
    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                buffer.consume();
                Thread.sleep((long) (Math.random() * 900) + 100); // Sleep between 0.1 to 1 second
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Producer_Consumer_Problem {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(10);
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        producer.start();
        consumer.start();
    }
}
