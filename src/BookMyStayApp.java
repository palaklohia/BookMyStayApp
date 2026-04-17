import java.util.*;

// Reservation
class Reservation {
  private int id;
  private String customerName;
  private String roomType;

  public Reservation(int id, String customerName, String roomType) {
    this.id = id;
    this.customerName = customerName;
    this.roomType = roomType;
  }

  public String getRoomType() {
    return roomType;
  }

  @Override
  public String toString() {
    return "Reservation ID: " + id + ", Name: " + customerName + ", Room: " + roomType;
  }
}

// Thread-safe Inventory Manager
class InventoryManager {
  private Map<String, Integer> inventory = new HashMap<>();

  public InventoryManager() {
    inventory.put("Standard", 1);
    inventory.put("Deluxe", 1);
    inventory.put("Suite", 1);
  }

  // CRITICAL SECTION (synchronized)
  public synchronized boolean allocateRoom(String roomType) {

    int available = inventory.getOrDefault(roomType, 0);

    if (available > 0) {
      System.out.println(Thread.currentThread().getName() +
              " allocating " + roomType);

      // simulate delay → increases race condition chances
      try { Thread.sleep(100); } catch (InterruptedException e) {}

      inventory.put(roomType, available - 1);
      return true;
    } else {
      return false;
    }
  }

  public synchronized void printInventory() {
    System.out.println("Final Inventory: " + inventory);
  }
}

// Shared Booking Queue
class BookingQueue {
  private Queue<Reservation> queue = new LinkedList<>();

  public synchronized void addBooking(Reservation r) {
    queue.add(r);
  }

  public synchronized Reservation getBooking() {
    return queue.poll();
  }
}

// Worker Thread (simulates concurrent users)
class BookingProcessor extends Thread {

  private BookingQueue queue;
  private InventoryManager inventory;

  public BookingProcessor(BookingQueue queue, InventoryManager inventory, String name) {
    super(name);
    this.queue = queue;
    this.inventory = inventory;
  }

  @Override
  public void run() {
    while (true) {
      Reservation r;

      // synchronized access to queue
      synchronized (queue) {
        r = queue.getBooking();
      }

      if (r == null) break;

      boolean success = inventory.allocateRoom(r.getRoomType());

      if (success) {
        System.out.println(getName() + " SUCCESS → " + r);
      } else {
        System.out.println(getName() + " FAILED → No room for " + r.getRoomType());
      }
    }
  }
}

// Main App
public class BookMyStayApp {

  public static void main(String[] args) {

    BookingQueue queue = new BookingQueue();
    InventoryManager inventory = new InventoryManager();

    // Simulating concurrent requests (same room type → conflict)
    queue.addBooking(new Reservation(1, "A", "Deluxe"));
    queue.addBooking(new Reservation(2, "B", "Deluxe"));
    queue.addBooking(new Reservation(3, "C", "Deluxe"));

    // Multiple threads (guests)
    Thread t1 = new BookingProcessor(queue, inventory, "Thread-1");
    Thread t2 = new BookingProcessor(queue, inventory, "Thread-2");
    Thread t3 = new BookingProcessor(queue, inventory, "Thread-3");

    // Start concurrent execution
    t1.start();
    t2.start();
    t3.start();

    try {
      t1.join();
      t2.join();
      t3.join();
    } catch (InterruptedException e) {}

    // Final state
    inventory.printInventory();
  }
}