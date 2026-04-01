import java.util.*;

// Main class name kept as requested
public class BookMyStayApp {

    // ==============================
    // Room Class
    // ==============================
    static class Room {
        private String roomId;
        private String type;
        private double pricePerNight;
        private boolean available;

        public Room(String roomId, String type, double pricePerNight) {
            this.roomId = roomId;
            this.type = type;
            this.pricePerNight = pricePerNight;
            this.available = true;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getType() {
            return type;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public boolean isAvailable() {
            return available;
        }

        public void bookRoom() {
            this.available = false;
        }

        public void releaseRoom() {
            this.available = true;
        }

        @Override
        public String toString() {
            return "Room ID: " + roomId +
                    ", Type: " + type +
                    ", Price/Night: " + pricePerNight +
                    ", Available: " + available;
        }
    }

    // ==============================
    // Reservation Class
    // ==============================
    static class Reservation {
        private String reservationId;
        private String guestName;
        private Room room;
        private int numberOfNights;

        public Reservation(String reservationId, String guestName, Room room, int numberOfNights) {
            this.reservationId = reservationId;
            this.guestName = guestName;
            this.room = room;
            this.numberOfNights = numberOfNights;
        }

        public String getReservationId() {
            return reservationId;
        }

        public String getGuestName() {
            return guestName;
        }

        public Room getRoom() {
            return room;
        }

        public int getNumberOfNights() {
            return numberOfNights;
        }

        public double getBaseCost() {
            return room.getPricePerNight() * numberOfNights;
        }

        @Override
        public String toString() {
            return "Reservation ID: " + reservationId +
                    ", Guest: " + guestName +
                    ", Room Type: " + room.getType() +
                    ", Nights: " + numberOfNights +
                    ", Base Cost: " + getBaseCost();
        }
    }

    // ==============================
    // Add-On Service Class
    // ==============================
    static class AddOnService {
        private String serviceName;
        private double serviceCost;

        public AddOnService(String serviceName, double serviceCost) {
            this.serviceName = serviceName;
            this.serviceCost = serviceCost;
        }

        public String getServiceName() {
            return serviceName;
        }

        public double getServiceCost() {
            return serviceCost;
        }

        @Override
        public String toString() {
            return serviceName + " (Rs. " + serviceCost + ")";
        }
    }

    // ==============================
    // Booking Manager
    // Core booking / inventory logic
    // ==============================
    static class BookingManager {
        private List<Room> rooms = new ArrayList<>();
        private Map<String, Reservation> reservations = new HashMap<>();

        public void addRoom(Room room) {
            rooms.add(room);
        }

        public Reservation createReservation(String reservationId, String guestName, String roomType, int nights) {
            for (Room room : rooms) {
                if (room.isAvailable() && room.getType().equalsIgnoreCase(roomType)) {
                    room.bookRoom(); // core inventory change happens only here
                    Reservation reservation = new Reservation(reservationId, guestName, room, nights);
                    reservations.put(reservationId, reservation);
                    return reservation;
                }
            }
            return null;
        }

        public Reservation getReservation(String reservationId) {
            return reservations.get(reservationId);
        }

        public void showAllRooms() {
            System.out.println("\n----- Room Status -----");
            for (Room room : rooms) {
                System.out.println(room);
            }
        }

        public void showAllReservations() {
            System.out.println("\n----- Reservations -----");
            for (Reservation reservation : reservations.values()) {
                System.out.println(reservation);
            }
        }
    }

    // ==============================
    // Add-On Service Manager
    // Handles only optional services
    // Does NOT modify booking/inventory
    // ==============================
    static class AddOnServiceManager {
        private Map<String, List<AddOnService>> reservationServicesMap = new HashMap<>();

        public void addServiceToReservation(String reservationId, AddOnService service) {
            reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());
            reservationServicesMap.get(reservationId).add(service);
        }

        public List<AddOnService> getServicesForReservation(String reservationId) {
            return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
        }

        public double calculateAdditionalCost(String reservationId) {
            double total = 0;
            List<AddOnService> services = reservationServicesMap.get(reservationId);

            if (services != null) {
                for (AddOnService service : services) {
                    total += service.getServiceCost();
                }
            }
            return total;
        }

        public void displayServicesForReservation(String reservationId) {
            List<AddOnService> services = getServicesForReservation(reservationId);

            System.out.println("\n----- Add-On Services for Reservation " + reservationId + " -----");
            if (services.isEmpty()) {
                System.out.println("No add-on services selected.");
                return;
            }

            for (AddOnService service : services) {
                System.out.println(service);
            }

            System.out.println("Total Additional Cost: Rs. " + calculateAdditionalCost(reservationId));
        }
    }

    // ==============================
    // Main Method
    // ==============================
    public static void main(String[] args) {
        BookingManager bookingManager = new BookingManager();
        AddOnServiceManager addOnServiceManager = new AddOnServiceManager();

        // Add rooms
        bookingManager.addRoom(new Room("R101", "Standard", 2000));
        bookingManager.addRoom(new Room("R102", "Deluxe", 3500));
        bookingManager.addRoom(new Room("R103", "Suite", 5000));

        // Show initial room status
        bookingManager.showAllRooms();

        // Create reservation
        Reservation reservation = bookingManager.createReservation("RES101", "Aarav", "Deluxe", 3);

        if (reservation != null) {
            System.out.println("\nReservation created successfully:");
            System.out.println(reservation);

            // Guest selects add-on services
            AddOnService breakfast = new AddOnService("Breakfast", 500);
            AddOnService airportPickup = new AddOnService("Airport Pickup", 1200);
            AddOnService spaAccess = new AddOnService("Spa Access", 1500);

            // Attach multiple services to same reservation
            addOnServiceManager.addServiceToReservation(reservation.getReservationId(), breakfast);
            addOnServiceManager.addServiceToReservation(reservation.getReservationId(), airportPickup);
            addOnServiceManager.addServiceToReservation(reservation.getReservationId(), spaAccess);

            // Display services
            addOnServiceManager.displayServicesForReservation(reservation.getReservationId());

            // Cost aggregation
            double baseCost = reservation.getBaseCost();
            double addOnCost = addOnServiceManager.calculateAdditionalCost(reservation.getReservationId());
            double finalCost = baseCost + addOnCost;

            System.out.println("\n----- Final Bill -----");
            System.out.println("Base Cost: Rs. " + baseCost);
            System.out.println("Add-On Cost: Rs. " + addOnCost);
            System.out.println("Final Total Cost: Rs. " + finalCost);
        } else {
            System.out.println("\nNo available room found for requested type.");
        }

        // Show that booking/inventory logic is separate and unchanged
        bookingManager.showAllReservations();
        bookingManager.showAllRooms();
    }
}