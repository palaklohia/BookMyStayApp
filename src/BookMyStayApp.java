import java.util.*;

// Custom Exception
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
  private boolean isCancelled;

  public Reservation(int reservationId, String customerName, String roomType, String date) {
    this.reservationId = reservationId;
    this.customerName = customerName;
    this.roomType = roomType;
    this.date = date;
    this.isCancelled = false;
  }

  public int getReservationId() {
    return reservationId;
  }

  public String getRoomType() {
    return roomType;
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public void cancel() {
    this.isCancelled = true;
  }

  @Override
  public String toString() {
    return "Reservation ID: " + reservationId +
            ", Name: " + customerName +
            ", Room: " + roomType +
            ", Date: " + date +
            ", Status: " + (isCancelled ? "Cancelled" : "Confirmed");
  }
}

// Booking History
class BookingHistory {
  private List<Reservation> reservations = new ArrayList<>();

  public void addReservation(Reservation reservation) {
    reservations.add(reservation);
  }

  public Reservation findById(int id) {
    for (Reservation r : reservations) {
      if (r.getReservationId() == id) return r;
    }
    return null;
  }

  public List<Reservation> getAllReservations() {
    return new ArrayList<>(reservations);
  }
}

// Inventory Manager
class InventoryManager {
  private Map<String, Integer> roomInventory = new HashMap<>();

  public InventoryManager() {
    roomInventory.put("Standard", 2);
    roomInventory.put("Deluxe", 2);
    roomInventory.put("Suite", 1);
  }

  public void reserveRoom(String roomType) throws InvalidBookingException {
    if (!roomInventory.containsKey(roomType)) {
      throw new InvalidBookingException("Invalid room type: " + roomType);
    }

    int available = roomInventory.get(roomType);
    if (available <= 0) {
      throw new InvalidBookingException("No rooms available for: " + roomType);
    }

    roomInventory.put(roomType, available - 1);
  }

  public void releaseRoom(String roomType) {
    roomInventory.put(roomType, roomInventory.get(roomType) + 1);
  }

  public void printInventory() {
    System.out.println("Current Inventory: " + roomInventory);
  }
}

// Cancellation Service (Core Logic)
class CancellationService {

  // Stack for rollback tracking (LIFO)
  private Stack<String> rollbackStack = new Stack<>();

  public void cancelReservation(int reservationId,
                                BookingHistory history,
                                InventoryManager inventory)
          throws InvalidBookingException {

    // Step 1: Validate existence
    Reservation reservation = history.findById(reservationId);

    if (reservation == null) {
      throw new InvalidBookingException("Reservation not found");
    }

    // Step 2: Prevent duplicate cancellation
    if (reservation.isCancelled()) {
      throw new InvalidBookingException("Reservation already cancelled");
    }

    String roomType = reservation.getRoomType();

    // Step 3: Record rollback info (LIFO)
    rollbackStack.push(roomType);

    // Step 4: Restore inventory
    inventory.releaseRoom(roomType);

    // Step 5: Update booking state
    reservation.cancel();

    System.out.println("Cancellation successful for ID: " + reservationId);
  }

  public void printRollbackStack() {
    System.out.println("Rollback Stack: " + rollbackStack);
  }
}

// Validator
class BookingValidator {
  public static void validate(Reservation r) throws InvalidBookingException {
    if (r == null) throw new InvalidBookingException("Reservation cannot be null");
    if (r.getRoomType() == null || r.getRoomType().isEmpty()) {
      throw new InvalidBookingException("Invalid room type");
    }
  }
}

// Main App
public class BookMyStayApp {

  public static void main(String[] args) {

    BookingHistory history = new BookingHistory();
    InventoryManager inventory = new InventoryManager();
    CancellationService cancelService = new CancellationService();

    try {
      // Confirm bookings
      Reservation r1 = new Reservation(1, "Palak", "Deluxe", "2026-04-20");
      Reservation r2 = new Reservation(2, "Rahul", "Suite", "2026-04-21");

      BookingValidator.validate(r1);
      inventory.reserveRoom(r1.getRoomType());
      history.addReservation(r1);

      BookingValidator.validate(r2);
      inventory.reserveRoom(r2.getRoomType());
      history.addReservation(r2);

    } catch (InvalidBookingException e) {
      System.out.println("Booking Error: " + e.getMessage());
    }

    // Show inventory before cancellation
    System.out.println("\nBefore Cancellation:");
    inventory.printInventory();

    // Perform cancellation
    try {
      cancelService.cancelReservation(2, history, inventory); // valid
      cancelService.cancelReservation(3, history, inventory); // invalid
    } catch (InvalidBookingException e) {
      System.out.println("Cancellation Failed: " + e.getMessage());
    }

    // After cancellation
    System.out.println("\nAfter Cancellation:");
    inventory.printInventory();

    // Show rollback stack
    cancelService.printRollbackStack();

    // Show booking history
    System.out.println("\nBooking History:");
    for (Reservation r : history.getAllReservations()) {
      System.out.println(r);
    }
  }
}