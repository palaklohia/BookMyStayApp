/**
 * BookMyStayApp demonstrates object-oriented modeling using
 * abstraction and inheritance for different room types.
 * It prints predefined room details and their availability.
 *
 * @author YourName
 * @version 1.0
 */

// Abstract class representing a generic Room
abstract class Room {
  private int beds;
  private double size;
  private double price;

  // Constructor
  public Room(int beds, double size, double price) {
    this.beds = beds;
    this.size = size;
    this.price = price;
  }

  // Getters (Encapsulation)
  public int getBeds() {
    return beds;
  }

  public double getSize() {
    return size;
  }

  public double getPrice() {
    return price;
  }

  // Abstract method (forces subclasses to define room type)
  public abstract String getRoomType();

  // Common method to display details
  public void displayRoomDetails() {
    System.out.println("Room Type: " + getRoomType());
    System.out.println("Beds: " + beds);
    System.out.println("Size: " + size + " sq.ft");
    System.out.println("Price: $" + price);
  }
}

// Concrete class: Single Room
class SingleRoom extends Room {

  public SingleRoom() {
    super(1, 200, 50);
  }

  @Override
  public String getRoomType() {
    return "Single Room";
  }
}

// Concrete class: Double Room
class DoubleRoom extends Room {

  public DoubleRoom() {
    super(2, 350, 90);
  }

  @Override
  public String getRoomType() {
    return "Double Room";
  }
}

// Concrete class: Suite Room
class SuiteRoom extends Room {

  public SuiteRoom() {
    super(3, 600, 200);
  }

  @Override
  public String getRoomType() {
    return "Suite Room";
  }
}

// Application Entry Point
public class BookMyStayApp {

  /**
   * Main method - application execution starts here.
   */
  public static void main(String[] args) {

    // Create Room objects (Polymorphism)
    Room singleRoom = new SingleRoom();
    Room doubleRoom = new DoubleRoom();
    Room suiteRoom = new SuiteRoom();

    // Static availability (simple variables)
    int singleAvailability = 5;
    int doubleAvailability = 3;
    int suiteAvailability = 2;

    // Display information
    System.out.println("=== Welcome to BookMyStay ===\n");

    singleRoom.displayRoomDetails();
    System.out.println("Available: " + singleAvailability + "\n");

    doubleRoom.displayRoomDetails();
    System.out.println("Available: " + doubleAvailability + "\n");

    suiteRoom.displayRoomDetails();
    System.out.println("Available: " + suiteAvailability + "\n");

    System.out.println("Application terminated.");
  }
}