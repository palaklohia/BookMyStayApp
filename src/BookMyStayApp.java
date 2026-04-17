import java.util.*;

// Custom Exception for invalid bookings
class InvalidBookingException extends Exception {
  public InvalidBookingException(String message) {
    super(message);
  }
}

// Reservation class
class Reservation {
  private int reservationId;
  private String customerName;
  private String roomType;
  private String date;

  public Reservation(int reservationId, String customerName, String roomType, String date) {
    this.reservationId = reservationId;
    this.customerName = customerName;
    this.roomType = roomType;
    this.date = date;
  }

  public String getRoomType() {
    return roomType;
  }

  @Override
  public String toString() {
    return "Reservation ID: " + reservationId +
            ", Name: " + customerName +
            ", Room: " + roomType +
            ", Date: " + date;
  }
}

// Booking History
class BookingHistory {
  private List<Reservation> reservations = new ArrayList<>();

  public void addReservation(Reservation reservation) {
    reservations.add(reservation);
  }

  public List<Reservation> getAllReservations() {
    return new ArrayList<>(reservations); // safe copy
  }
}

// Inventory Manager (to guard system state)
class InventoryManager {
  private Map<String, Integer> roomInventory = new HashMap<>();

  public InventoryManager() {
    roomInventory.put("Standard", 2);
    roomInventory.put("Deluxe", 2);
    roomInventory.put("Suite", 1);
  }

  public void validateAndReserve(String roomType) throws InvalidBookingException {

    // Validate room type
    if (!roomInventory.containsKey(roomType)) {
      throw new InvalidBookingException("Invalid room type: " + roomType);
    }

    // Check availability
    int available = roomInventory.get(roomType);
    if (available <= 0) {
      throw new InvalidBookingException("No rooms available for: " + roomType);
    }

    // Update inventory safely
    roomInventory.put(roomType, available - 1);
  }
}

// Validator class (fail-fast)
class BookingValidator {

  public static void validate(Reservation reservation) throws InvalidBookingException {

    if (reservation == null) {
      throw new InvalidBookingException("Reservation cannot be null");
    }

    if (reservation.getRoomType() == null || reservation.getRoomType().isEmpty()) {
      throw new InvalidBookingException("Room type cannot be empty");
    }
  }
}

// Main Application
public class BookMyStayApp {

  public static void main(String[] args) {

    BookingHistory history = new BookingHistory();
    InventoryManager inventory = new InventoryManager();

    // Simulated bookings (some invalid)
    List<Reservation> bookings = Arrays.asList(
            new Reservation(1, "Palak", "Deluxe", "2026-04-20"),
            new Reservation(2, "Rahul", "Suite", "2026-04-21"),
            new Reservation(3, "Ananya", "InvalidType", "2026-04-22"), // invalid
            new Reservation(4, "Amit", "Suite", "2026-04-23") // exceeds inventory
    );

    for (Reservation r : bookings) {
      try {
        // Step 1: Validate input (fail-fast)
        BookingValidator.validate(r);

        // Step 2: Validate system state & reserve
        inventory.validateAndReserve(r.getRoomType());

        // Step 3: Add to history if all valid
        history.addReservation(r);

        System.out.println("Booking Confirmed: " + r);

      } catch (InvalidBookingException e) {
        // Graceful failure handling
        System.out.println("Booking Failed: " + e.getMessage());
      }
    }

    // Display valid bookings stored
    System.out.println("\n=== Valid Booking History ===");
    for (Reservation r : history.getAllReservations()) {
      System.out.println(r);
    }
  }
}