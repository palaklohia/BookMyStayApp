import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
  private static final long serialVersionUID = 1L;

  private int id;
  private String name;
  private String roomType;

  public Reservation(int id, String name, String roomType) {
    this.id = id;
    this.name = name;
    this.roomType = roomType;
  }

  public String getRoomType() {
    return roomType;
  }

  @Override
  public String toString() {
    return "ID: " + id + ", Name: " + name + ", Room: " + roomType;
  }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  private List<Reservation> reservations = new ArrayList<>();

  public void addReservation(Reservation r) {
    reservations.add(r);
  }

  public List<Reservation> getAllReservations() {
    return reservations;
  }
}

// Inventory Manager (Serializable)
class InventoryManager implements Serializable {
  private static final long serialVersionUID = 1L;

  private Map<String, Integer> inventory = new HashMap<>();

  public InventoryManager() {
    inventory.put("Standard", 2);
    inventory.put("Deluxe", 2);
    inventory.put("Suite", 1);
  }

  public boolean allocateRoom(String roomType) {
    int available = inventory.getOrDefault(roomType, 0);
    if (available > 0) {
      inventory.put(roomType, available - 1);
      return true;
    }
    return false;
  }

  public void printInventory() {
    System.out.println("Inventory: " + inventory);
  }
}

// Wrapper class (entire system state)
class SystemState implements Serializable {
  private static final long serialVersionUID = 1L;

  BookingHistory history;
  InventoryManager inventory;

  public SystemState(BookingHistory history, InventoryManager inventory) {
    this.history = history;
    this.inventory = inventory;
  }
}

// Persistence Service
class PersistenceService {

  private static final String FILE_NAME = "system_state.ser";

  // SAVE
  public static void save(SystemState state) {
    try (ObjectOutputStream oos =
                 new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

      oos.writeObject(state);
      System.out.println("System state saved successfully.");

    } catch (IOException e) {
      System.out.println("Error saving state: " + e.getMessage());
    }
  }

  // LOAD
  public static SystemState load() {
    try (ObjectInputStream ois =
                 new ObjectInputStream(new FileInputStream(FILE_NAME))) {

      SystemState state = (SystemState) ois.readObject();
      System.out.println("System state loaded successfully.");
      return state;

    } catch (FileNotFoundException e) {
      System.out.println("No previous state found. Starting fresh.");
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Corrupted data. Starting with safe defaults.");
    }
    return null;
  }
}

// Main App
public class BookMyStayApp {

  public static void main(String[] args) {

    BookingHistory history;
    InventoryManager inventory;

    // STEP 1: Load previous state
    SystemState state = PersistenceService.load();

    if (state != null) {
      history = state.history;
      inventory = state.inventory;
    } else {
      history = new BookingHistory();
      inventory = new InventoryManager();
    }

    // STEP 2: Simulate bookings
    Reservation r1 = new Reservation(1, "Palak", "Deluxe");
    Reservation r2 = new Reservation(2, "Rahul", "Suite");

    if (inventory.allocateRoom(r1.getRoomType())) {
      history.addReservation(r1);
    }

    if (inventory.allocateRoom(r2.getRoomType())) {
      history.addReservation(r2);
    }

    // STEP 3: Display current state
    System.out.println("\n--- Current Bookings ---");
    for (Reservation r : history.getAllReservations()) {
      System.out.println(r);
    }

    inventory.printInventory();

    // STEP 4: Save state before shutdown
    PersistenceService.save(new SystemState(history, inventory));
  }
}