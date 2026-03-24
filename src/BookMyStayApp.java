import java.util.HashMap;
import java.util.Map;

// Actor: RoomInventory
class RoomInventory {

  // Centralized storage (Single Source of Truth)
  private Map<String, Integer> inventory;

  // Constructor: Initialize inventory
  public RoomInventory() {
    inventory = new HashMap<>();
  }

  // Register room type with count
  public void addRoomType(String roomType, int count) {
    inventory.put(roomType, count);
  }

  // Get availability for a specific room type
  public int getAvailability(String roomType) {
    return inventory.getOrDefault(roomType, 0);
  }

  // Update availability (controlled method)
  public void updateAvailability(String roomType, int change) {
    int current = inventory.getOrDefault(roomType, 0);
    int updated = current + change;

    if (updated < 0) {
      System.out.println("Not enough rooms available for: " + roomType);
      return;
    }

    inventory.put(roomType, updated);
  }

  // Display full inventory state
  public void displayInventory() {
    System.out.println("Current Room Inventory:");
    for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
      System.out.println(entry.getKey() + " -> " + entry.getValue());
    }
  }
}

// Main Application
public class BookMyStayApp {

  public static void main(String[] args) {

    // Step 1: Initialize inventory system
    RoomInventory inventory = new RoomInventory();

    // Step 2: Register room types
    inventory.addRoomType("Single", 10);
    inventory.addRoomType("Double", 5);
    inventory.addRoomType("Suite", 2);

    // Step 3: Display initial state
    inventory.displayInventory();

    // Step 4: Perform updates
    System.out.println("\nBooking 2 Single rooms...");
    inventory.updateAvailability("Single", -2);

    System.out.println("Adding 1 Suite room...");
    inventory.updateAvailability("Suite", 1);

    // Step 5: Retrieve availability
    System.out.println("\nAvailable Single Rooms: " + inventory.getAvailability("Single"));

    // Step 6: Display updated state
    System.out.println();
    inventory.displayInventory();
  }
}