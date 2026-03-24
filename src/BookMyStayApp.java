import java.util.*;

// Main Public Class
public class BookMyStayApp {

  public static void main(String[] args) {

    // ---------------- Room Data ----------------
    Map<String, Room> roomData = new HashMap<>();
    roomData.put("DELUXE", new Room("DELUXE", 2000, "AC, WiFi, TV"));
    roomData.put("SUITE", new Room("SUITE", 5000, "AC, WiFi, TV, Jacuzzi"));
    roomData.put("STANDARD", new Room("STANDARD", 1000, "Fan, TV"));

    RoomRepository roomRepo = new RoomRepository(roomData);

    // ---------------- Inventory Data ----------------
    Map<String, Integer> inventoryData = new HashMap<>();
    inventoryData.put("DELUXE", 3);
    inventoryData.put("SUITE", 0); // Will be filtered out
    inventoryData.put("STANDARD", 5);

    Inventory inventory = new Inventory(inventoryData);

    // ---------------- Search Service ----------------
    SearchService searchService = new SearchService(inventory, roomRepo);

    // ---------------- Execute Search ----------------
    List<RoomResult> results = searchService.searchAvailableRooms();

    // ---------------- Display Results ----------------
    System.out.println("Available Rooms:");
    for (RoomResult result : results) {
      System.out.println(result);
    }
  }
}

// ---------------- Domain Model ----------------
class Room {
  private final String roomType;
  private final double price;
  private final String amenities;

  public Room(String roomType, double price, String amenities) {
    this.roomType = roomType;
    this.price = price;
    this.amenities = amenities;
  }

  public String getRoomType() {
    return roomType;
  }

  public double getPrice() {
    return price;
  }

  public String getAmenities() {
    return amenities;
  }
}

// ---------------- Inventory (Read-Only) ----------------
class Inventory {
  private final Map<String, Integer> availabilityMap;

  public Inventory(Map<String, Integer> availabilityMap) {
    this.availabilityMap = availabilityMap;
  }

  public int getAvailability(String roomType) {
    return availabilityMap.getOrDefault(roomType, 0);
  }

  public Map<String, Integer> getAllAvailability() {
    return availabilityMap; // No mutation performed
  }
}

// ---------------- Repository ----------------
class RoomRepository {
  private final Map<String, Room> roomMap;

  public RoomRepository(Map<String, Room> roomMap) {
    this.roomMap = roomMap;
  }

  public Room getRoomByType(String roomType) {
    return roomMap.get(roomType);
  }
}

// ---------------- DTO ----------------
class RoomResult {
  private final String roomType;
  private final double price;
  private final String amenities;
  private final int availableCount;

  public RoomResult(String roomType, double price, String amenities, int availableCount) {
    this.roomType = roomType;
    this.price = price;
    this.amenities = amenities;
    this.availableCount = availableCount;
  }

  @Override
  public String toString() {
    return "Room Type: " + roomType +
            ", Price: ₹" + price +
            ", Amenities: " + amenities +
            ", Available: " + availableCount;
  }
}

// ---------------- Search Service (Read-Only Logic) ----------------
class SearchService {

  private final Inventory inventory;
  private final RoomRepository roomRepository;

  public SearchService(Inventory inventory, RoomRepository roomRepository) {
    this.inventory = inventory;
    this.roomRepository = roomRepository;
  }

  public List<RoomResult> searchAvailableRooms() {
    List<RoomResult> results = new ArrayList<>();

    Map<String, Integer> availabilityMap = inventory.getAllAvailability();

    for (Map.Entry<String, Integer> entry : availabilityMap.entrySet()) {
      String roomType = entry.getKey();
      int availableCount = entry.getValue();

      // Defensive check: skip unavailable rooms
      if (availableCount <= 0) {
        continue;
      }

      Room room = roomRepository.getRoomByType(roomType);

      // Safety check: skip if room definition missing
      if (room == null) {
        continue;
      }

      results.add(new RoomResult(
              room.getRoomType(),
              room.getPrice(),
              room.getAmenities(),
              availableCount
      ));
    }

    return results;
  }
}