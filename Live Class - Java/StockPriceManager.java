

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class StockPrice {
    private final double price;
    private final long timestamp; // Timestamp in milliseconds

    public StockPrice(double price, long timestamp) {
        this.price = price;
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

public class StockPriceManager {
    private final ConcurrentHashMap<String, StockPrice> stockPrices = new ConcurrentHashMap<>(); // Registry for stock symbols
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>(); // Locks for each symbol

    // Method to get the lock for a specific stock symbol
    private ReentrantLock getLock(String symbol) {
        return locks.computeIfAbsent(symbol, k -> new ReentrantLock());
    }

    // Method to update the stock price for a symbol if the new price is more recent
    public void updateStockPrice(String symbol, double newPrice, long timestamp) {
        ReentrantLock lock = getLock(symbol); // Get lock for the specific symbol
        lock.lock(); // Acquire the lock
        try {
            StockPrice currentPrice = stockPrices.get(symbol);
            if (currentPrice == null || timestamp > currentPrice.getTimestamp()) {
                stockPrices.put(symbol, new StockPrice(newPrice, timestamp));
                System.out.println("Stock price for " + symbol + " updated to: " + newPrice + " at timestamp: " + timestamp);
            } else {
                System.out.println("Ignored outdated stock price update for " + symbol + ": " + newPrice + " at timestamp: " + timestamp);
            }
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Method to get the current stock price for a symbol
    public StockPrice getStockPrice(String symbol) {
        ReentrantLock lock = getLock(symbol); // Get lock for the specific symbol
        lock.lock(); // Acquire the lock
        try {
            return stockPrices.get(symbol);
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    public static void main(String[] args) {
        StockPriceManager manager = new StockPriceManager();

        // Simulate updating and retrieving stock prices in concurrent threads
        Runnable updateTask = () -> {
            for (int i = 0; i < 5; i++) {
                String symbol = "AAPL"; // Example stock symbol
                manager.updateStockPrice(symbol, Math.random() * 1000, System.currentTimeMillis()); // Update with random price and current timestamp
                try {
                    Thread.sleep(100); // Simulate time delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable retrieveTask = () -> {
            for (int i = 0; i < 10; i++) {
                String symbol = "AAPL"; // Example stock symbol
                StockPrice stockPrice = manager.getStockPrice(symbol);
                if (stockPrice != null) {
                    System.out.println("Current stock price for " + symbol + ": " + stockPrice.getPrice() + " at timestamp: " + stockPrice.getTimestamp());
                } else {
                    System.out.println("No stock price available for " + symbol);
                }
                try {
                    Thread.sleep(200); // Simulate time delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // Create and start threads
        Thread updaterThread = new Thread(updateTask, "UpdaterThread");
        Thread retrieverThread = new Thread(retrieveTask, "RetrieverThread");

        updaterThread.start();
        retrieverThread.start();

        try {
            updaterThread.join();
            retrieverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}