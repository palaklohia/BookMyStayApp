import java.util.*;

// Main Public Class
public class BookMyStayApp {

  public static void main(String[] args) {

    // ---------------- Inventory Setup ----------------
    Map<String, Integer> inventoryData = new HashMap<>();
    inventoryData.put("DELUXE", 2);
    inventoryData.put("SUITE", 1);

    InventoryService inventoryService = new InventoryService(inventoryData);

    // ---------------- Booking Queue ----------------
    Queue<BookingRequest> requestQueue = new LinkedList<>();

    requestQueue.add(new BookingRequest("User1", "DELUXE"));
    requestQueue.add(new BookingRequest("User2", "DELUXE"));
    requestQueue.add(new BookingRequest("User3", "DELUXE")); // Should fail
    requestQueue.add(new BookingRequest("User4", "SUITE"));

    // ---------------- Booking Service ----------------
    BookingService bookingService = new BookingService(inventoryService);

    // ---------------- Process Requests ----------------
    bookingService.processBookings(requestQueue);
  }
}

// ---------------- Booking Request ----------------
class BookingRequest {
  private final String userId;
  private final String roomType;

  public BookingRequest(String userId, String roomType) {
    this.userId = userId;
    this.roomType = roomType;
  }

  public String getUserId() {
    return userId;
  }

  public String getRoomType() {
    return roomType;
  }
}

// ---------------- Inventory Service ----------------
class InventoryService {
  private final Map<String, Integer> availabilityMap;

  public InventoryService(Map<String, Integer> availabilityMap) {
    this.availabilityMap = availabilityMap;
  }

  public int getAvailability(String roomType) {
    return availabilityMap.getOrDefault(roomType, 0);
  }

  public void decrement(String roomType) {
    availabilityMap.put(roomType, availabilityMap.get(roomType) - 1);
  }
}

// ---------------- Booking Service ----------------
class BookingService {

  private final InventoryService inventoryService;

  // Map<RoomType, Set<RoomIds>>
  private final Map<String, Set<String>> allocatedRooms = new HashMap<>();

  public BookingService(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  public void processBookings(Queue<BookingRequest> queue) {

    while (!queue.isEmpty()) {

      BookingRequest request = queue.poll(); // FIFO

      String roomType = request.getRoomType();
      String userId = request.getUserId();

      System.out.println("\nProcessing booking for " + userId + " (" + roomType + ")");

      // Step 1: Check availability
      int available = inventoryService.getAvailability(roomType);

      if (available <= 0) {
        System.out.println("Booking FAILED: No rooms available");
        continue;
      }

      // Step 2: Generate Unique Room ID
      String roomId = generateRoomId(roomType);

      // Step 3: Ensure uniqueness using Set
      allocatedRooms.putIfAbsent(roomType, new HashSet<>());

      Set<String> assignedSet = allocatedRooms.get(roomType);

      if (assignedSet.contains(roomId)) {
        // Extremely rare case, regenerate
        roomId = generateRoomId(roomType);
      }

      // Step 4: Assign room (Atomic logical step begins)
      assignedSet.add(roomId);

      // Step 5: Update inventory immediately
      inventoryService.decrement(roomType);

      // Step 6: Confirm booking
      System.out.println("Booking CONFIRMED");
      System.out.println("User: " + userId);
      System.out.println("Room Type: " + roomType);
      System.out.println("Assigned Room ID: " + roomId);
    }
  }

  // Unique Room ID Generator
  private String generateRoomId(String roomType) {
    return roomType + "-" + UUID.randomUUID().toString().substring(0, 8);
  }
}