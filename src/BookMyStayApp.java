import java.util.*;

// Main Public Class
public class BookMyStayApp {

  public static void main(String[] args) {

    // ---------------- Booking Request Queue ----------------
    BookingRequestQueue requestQueue = new BookingRequestQueue();

    // ---------------- Simulating Guest Requests ----------------
    requestQueue.addRequest(new Reservation("User1", "DELUXE"));
    requestQueue.addRequest(new Reservation("User2", "SUITE"));
    requestQueue.addRequest(new Reservation("User3", "DELUXE"));
    requestQueue.addRequest(new Reservation("User4", "STANDARD"));

    // ---------------- Display Queue ----------------
    System.out.println("\n--- Booking Requests in FIFO Order ---");
    requestQueue.displayQueue();

    // NOTE:
    // No allocation or inventory update happens here
    // This stage ONLY collects and orders requests
  }
}

// ---------------- Reservation (Actor Model) ----------------
class Reservation {
  private final String userId;
  private final String roomType;

  public Reservation(String userId, String roomType) {
    this.userId = userId;
    this.roomType = roomType;
  }

  public String getUserId() {
    return userId;
  }

  public String getRoomType() {
    return roomType;
  }

  @Override
  public String toString() {
    return "User: " + userId + ", Room Type: " + roomType;
  }
}

// ---------------- Booking Request Queue ----------------
class BookingRequestQueue {

  private final Queue<Reservation> queue;

  public BookingRequestQueue() {
    this.queue = new LinkedList<>();
  }

  // Add request (FIFO insertion)
  public void addRequest(Reservation reservation) {
    queue.offer(reservation);
    System.out.println("Request added: " + reservation);
  }

  // Retrieve next request (used later by allocation system)
  public Reservation getNextRequest() {
    return queue.poll();
  }

  // View all queued requests (without removing)
  public void displayQueue() {
    for (Reservation r : queue) {
      System.out.println(r);
    }
  }

  // Check if queue is empty
  public boolean isEmpty() {
    return queue.isEmpty();
  }
}