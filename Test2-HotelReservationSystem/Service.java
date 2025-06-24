import java.util.*;

enum RoomType {
    STANDARD, JUNIOR, SUITE
}

class Room {
    int id;
    RoomType type;
    int pricePerNight;

    Room(int id, RoomType type, int pricePerNight) {
        this.id = id;
        this.type = type;
        this.pricePerNight = pricePerNight;
    }
}

class User {
    int id;
    int balance;

    User(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }
}

class Booking {
    int roomId;
    RoomType roomType;
    int roomPrice;
    int userId;
    int userBalanceAtBooking;
    Date checkIn;
    Date checkOut;

    Booking(Room room, User user, Date checkIn, Date checkOut) {
        this.roomId = room.id;
        this.roomType = room.type;
        this.roomPrice = room.pricePerNight;
        this.userId = user.id;
        this.userBalanceAtBooking = user.balance;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}

public class Service {
    List<Room> rooms = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        for (Room room : rooms) {
            if (room.id == roomNumber) {
                room.type = roomType;
                room.pricePerNight = roomPricePerNight;
                return;
            }
        }
        rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
    }

    public void setUser(int userId, int balance) {
        for (User user : users) {
            if (user.id == userId) {
                user.balance = balance;
                return;
            }
        }
        users.add(new User(userId, balance));
    }

    private boolean isAvailable(int roomId, Date checkIn, Date checkOut) {
        for (Booking b : bookings) {
            if (b.roomId == roomId) {
                if (!(checkOut.compareTo(b.checkIn) <= 0 || checkIn.compareTo(b.checkOut) >= 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        if (checkOut.compareTo(checkIn) <= 0) {
            System.out.println("Invalid date range");
            return;
        }

        Room room = rooms.stream().filter(r -> r.id == roomNumber).findFirst().orElse(null);
        User user = users.stream().filter(u -> u.id == userId).findFirst().orElse(null);

        if (room == null || user == null) {
            System.out.println("User or room not found");
            return;
        }

        long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
        int totalCost = (int) nights * room.pricePerNight;

        if (user.balance < totalCost) {
            System.out.println("Insufficient balance");
            return;
        }

        if (!isAvailable(roomNumber, checkIn, checkOut)) {
            System.out.println("Room not available");
            return;
        }

        user.balance -= totalCost;
        bookings.add(0, new Booking(room, user, checkIn, checkOut));
    }

    public void printAll() {
        System.out.println("=== ROOMS ===");
        ListIterator<Room> roomIt = rooms.listIterator(rooms.size());
        while (roomIt.hasPrevious()) {
            Room r = roomIt.previous();
            System.out.printf("Room %d | Type: %s | Price/night: %d\n", r.id, r.type, r.pricePerNight);
        }

        System.out.println("\n=== BOOKINGS ===");
        for (Booking b : bookings) {
            System.out.printf("User %d | Room %d (%s) | %tF to %tF | Balance: %d | Price/night: %d\n",
                    b.userId, b.roomId, b.roomType, b.checkIn, b.checkOut, b.userBalanceAtBooking, b.roomPrice);
        }
    }

    public void printAllUsers() {
        System.out.println("=== USERS ===");
        ListIterator<User> it = users.listIterator(users.size());
        while (it.hasPrevious()) {
            User u = it.previous();
            System.out.printf("User %d | Balance: %d\n", u.id, u.balance);
        }
    }

    public static void main(String[] args) {
        Service service = new Service();
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(2, RoomType.JUNIOR, 2000);
        service.setRoom(3, RoomType.SUITE, 3000);
        service.setUser(1, 5000);
        service.setUser(2, 10000);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        // Booking 1: success
        c1.set(2026, Calendar.JUNE, 30);
        c2.set(2026, Calendar.JULY, 7);
        service.bookRoom(1, 2, c1.getTime(), c2.getTime());

        // Booking 2: invalid date
        c1.set(2026, Calendar.JULY, 7);
        c2.set(2026, Calendar.JUNE, 30);
        service.bookRoom(1, 2, c1.getTime(), c2.getTime());

        // Booking 3: success
        c1.set(2026, Calendar.JULY, 7);
        c2.set(2026, Calendar.JULY, 8);
        service.bookRoom(1, 1, c1.getTime(), c2.getTime());

        // Booking 4: room not available
        c1.set(2026, Calendar.JULY, 7);
        c2.set(2026, Calendar.JULY, 9);
        service.bookRoom(2, 1, c1.getTime(), c2.getTime());

        // Booking 5: success
        c1.set(2026, Calendar.JULY, 7);
        c2.set(2026, Calendar.JULY, 8);
        service.bookRoom(2, 3, c1.getTime(), c2.getTime());

        // Change room 1 without affecting previous bookings
        service.setRoom(1, RoomType.SUITE, 10000);

        // Final prints
        service.printAll();
        service.printAllUsers();
    }
}
