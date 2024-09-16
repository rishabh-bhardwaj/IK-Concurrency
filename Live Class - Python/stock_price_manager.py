import threading
import time
from collections import defaultdict

class StockPrice:
    def __init__(self, price, timestamp):
        self.price = price
        self.timestamp = timestamp

    def __repr__(self):
        return f"StockPrice(price={self.price}, timestamp={self.timestamp})"


class StockPriceManager:
    def __init__(self):
        self.stock_prices = {}
        self.locks = defaultdict(threading.Lock)  # Lock for each symbol

    def update_stock_price(self, symbol, new_price, timestamp):
        lock = self.locks[symbol]
        with lock:  # Acquire the lock for the specific symbol
            current_price = self.stock_prices.get(symbol)
            if current_price is None or timestamp > current_price.timestamp:
                self.stock_prices[symbol] = StockPrice(new_price, timestamp)
                print(f"Stock price for {symbol} updated to: {new_price} at timestamp: {timestamp}")
            else:
                print(f"Ignored outdated stock price update for {symbol}: {new_price} at timestamp: {timestamp}")

    def get_stock_price(self, symbol):
        lock = self.locks[symbol]
        with lock:  # Acquire the lock for the specific symbol
            return self.stock_prices.get(symbol)

def update_task(manager):
    for _ in range(5):
        symbol = "AAPL"  # Example stock symbol
        manager.update_stock_price(symbol, random.random() * 1000, int(time.time() * 1000))  # Update with random price and current timestamp
        time.sleep(0.1)  # Simulate time delay

def retrieve_task(manager):
    for _ in range(10):
        symbol = "AAPL"  # Example stock symbol
        stock_price = manager.get_stock_price(symbol)
        if stock_price:
            print(f"Current stock price for {symbol}: {stock_price.price} at timestamp: {stock_price.timestamp}")
        else:
            print(f"No stock price available for {symbol}")
        time.sleep(0.2)  # Simulate time delay

if __name__ == "__main__":
    import random

    manager = StockPriceManager()

    # Create and start threads
    updater_thread = threading.Thread(target=update_task, args=(manager,), name="UpdaterThread")
    retriever_thread = threading.Thread(target=retrieve_task, args=(manager,), name="RetrieverThread")

    updater_thread.start()
    retriever_thread.start()

    updater_thread.join()
    retriever_thread.join()
