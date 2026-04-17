import java.util.*;

// Reservation class (basic structure)
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

  public int getReservationId() {
    return reservationId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getRoomType() {
    return roomType;
  }

  public String getDate() {
    return date;
  }

  @Override
  public String toString() {
    return "Reservation ID: " + reservationId +
            ", Name: " + customerName +
            ", Room: " + roomType +
            ", Date: " + date;
  }
}

// Booking History (stores confirmed bookings)
class BookingHistory {
  private List<Reservation> reservations;

  public BookingHistory() {
    reservations = new ArrayList<>();
  }

  // Add confirmed booking
  public void addReservation(Reservation reservation) {
    reservations.add(reservation); // preserves insertion order
  }

  // Retrieve all bookings
  public List<Reservation> getAllReservations() {
    return new ArrayList<>(reservations); // return copy (no modification)
  }
}

// Reporting Service (separate concern)
class BookingReportService {

  // Generate simple summary report
  public void generateReport(List<Reservation> reservations) {
    System.out.println("\n--- Booking Report ---");
    System.out.println("Total Bookings: " + reservations.size());

    for (Reservation r : reservations) {
      System.out.println(r);
    }
  }
}

// Main Application
public class BookMyStayApp {

  public static void main(String[] args) {

    BookingHistory history = new BookingHistory();
    BookingReportService reportService = new BookingReportService();

    // Simulating confirmed bookings
    Reservation r1 = new Reservation(1, "Palak", "Deluxe", "2026-04-20");
    Reservation r2 = new Reservation(2, "Rahul", "Suite", "2026-04-21");
    Reservation r3 = new Reservation(3, "Ananya", "Standard", "2026-04-22");

    // Flow: Confirm booking → Add to history
    history.addReservation(r1);
    history.addReservation(r2);
    history.addReservation(r3);

    // Admin retrieves booking history
    List<Reservation> storedBookings = history.getAllReservations();

    System.out.println("=== Booking History ===");
    for (Reservation r : storedBookings) {
      System.out.println(r);
    }

    // Admin requests report
    reportService.generateReport(storedBookings);
  }
}