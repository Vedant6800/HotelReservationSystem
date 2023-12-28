import java.io.*;
import java.util.Scanner;

public class HotelReservationSystem {

  private static final String ROOMS_FILE = "rooms.txt";
  private static final String USERS_FILE = "users.txt";

  // ANSI escape codes for colors
  private static final String RESET = "\u001B[0m";
  private static final String RED = "\u001B[31m";
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String BLUE = "\u001B[34m";
  private static final String CYAN = "\u001B[36m";

  public static void main(String[] args) {
    // Check if rooms.txt and users.txt exist and create them if not
    checkAndCreateFile(ROOMS_FILE);
    checkAndCreateFile(USERS_FILE);

    Scanner scanner = new Scanner(System.in);

    // Login or Register loop
    while (true) {
      printTitle();
      System.out.println(
        CYAN + "| Welcome to the Enchanted Oasis Hotel!            |" + RESET
      );
      System.out.println(
        CYAN + "| 1. Login                                         |" + RESET
      );
      System.out.println(
        CYAN + "| 2. Register                                      |" + RESET
      );

      System.out.print(CYAN + "Enter your choice: " + RESET);
      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          login(scanner);
          break;
        case 2:
          register(scanner);
          break;
        default:
          System.out.println(
            RED + "Invalid choice. Please enter 1 or 2." + RESET
          );
      }
    }
  }

  private static void login(Scanner scanner) {
    while (true) {
      printTitle();
      System.out.println(
        CYAN + "| Login:                                           |" + RESET
      );

      System.out.print(CYAN + "Enter your username: " + RESET);
      String username = scanner.nextLine();

      System.out.print(CYAN + "Enter your password: " + RESET);
      String password = scanner.nextLine();

      if (authenticateUser(username, password)) {
        if (username.equals("admin")) {
          adminMenu(scanner);
        } else {
          userMenu(username, scanner);
        }
      } else {
        System.out.println(
          RED + "Invalid username or password. Please try again." + RESET
        );
      }
    }
  }

  private static void register(Scanner scanner) {
    while (true) {
      printTitle();
      System.out.println(
        CYAN + "| Register:                                        |" + RESET
      );

      System.out.print(CYAN + "Enter your username: " + RESET);
      String username = scanner.nextLine();

      if (checkUsernameExists(username)) {
        System.out.println(
          RED +
          "Username already exists. Please choose a different username." +
          RESET
        );
        continue;
      }

      System.out.print(CYAN + "Enter your password: " + RESET);
      String password = scanner.nextLine();

      System.out.print(CYAN + "Enter your full name: " + RESET);
      String fullName = scanner.nextLine();

      // Save user details to the file
      try (
        BufferedWriter writer = new BufferedWriter(
          new FileWriter(USERS_FILE, true)
        )
      ) {
        writer.write(username + "\t" + password + "\t" + fullName);
        writer.newLine();
        System.out.println(
          GREEN + "Registration successful! You can now log in." + RESET
        );
        return; // Exit registration loop
      } catch (IOException e) {
        System.err.println(
          RED + "Error registering user: " + e.getMessage() + RESET
        );
      }
    }
  }

  private static void adminMenu(Scanner scanner) {
    while (true) {
      printTitle();
      System.out.println(
        YELLOW + "| Admin Menu:                                      |" + RESET
      );
      printBoxTop();
      System.out.println(
        "| " +
        GREEN +
        "1. Add Room" +
        RESET +
        "                                  |"
      );
      System.out.println(
        "| " +
        GREEN +
        "2. Delete Room" +
        RESET +
        "                               |"
      );
      System.out.println(
        "| " +
        GREEN +
        "3. Allocate Room" +
        RESET +
        "                             |"
      );
      System.out.println(
        "| " +
        GREEN +
        "4. Deallocate Room" +
        RESET +
        "                           |"
      );
      System.out.println(
        "| " +
        GREEN +
        "5. Rooms status" +
        RESET +
        "                           |"
      );
      System.out.println(
        "| " +
        RED +
        "6. Logout" +
        RESET +
        "                                   |"
      );
      printBoxBottom();

      System.out.print(CYAN + "Enter your choice: " + RESET);
      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          addRoom(scanner);
          break;
        case 2:
          deleteRoom(scanner);
          break;
        case 3:
          allocateRoom(scanner);
          break;
        case 4:
          deallocateRoom(scanner);
          break;
        case 5:
          viewRoomStatus();
          break;
        case 6:
          return;
        default:
          System.out.println(
            RED + "Invalid choice. Please enter a valid option." + RESET
          );
      }
    }
  }

  private static void addRoom(Scanner scanner) {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Admin - Add Room:                                |" + RESET
    );
    printBoxBottom();

    System.out.print(YELLOW + "Enter the room number to add: " + RESET);
    int roomNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    try (
      BufferedWriter writer = new BufferedWriter(
        new FileWriter(ROOMS_FILE, true)
      )
    ) {
      writer.write("Available\t" + roomNumber);
      writer.newLine();
      System.out.println(GREEN + "Room added successfully!" + RESET);
    } catch (IOException e) {
      System.err.println(RED + "Error adding room: " + e.getMessage() + RESET);
    }
    pressEnterToContinue(scanner);
  }

  private static void deleteRoom(Scanner scanner) {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Admin - Delete Room:                             |" + RESET
    );
    printBoxBottom();

    System.out.print(YELLOW + "Enter the room number to delete: " + RESET);
    int roomNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    try (
      BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE));
      BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))
    ) {
      String line;
      boolean deleted = false;
      while ((line = reader.readLine()) != null) {
        String[] roomData = line.split("\t");
        if (
          roomData.length == 2 && Integer.parseInt(roomData[1]) == roomNumber
        ) {
          deleted = true;
        } else {
          writer.write(line);
          writer.newLine();
        }
      }

      if (deleted) {
        System.out.println(GREEN + "Room deleted successfully!" + RESET);
      } else {
        System.out.println(RED + "Room not found." + RESET);
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error deleting room: " + e.getMessage() + RESET
      );
    }

    // Rename the temp file to rooms.txt
    File tempFile = new File("temp.txt");
    File originalFile = new File(ROOMS_FILE);
    if (tempFile.renameTo(originalFile)) {
      System.out.println(GREEN + "File updated successfully!" + RESET);
    } else {
      System.err.println(RED + "Error updating file." + RESET);
    }

    pressEnterToContinue(scanner);
  }

  private static void allocateRoom(Scanner scanner) {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Admin - Allocate Room:                           |" + RESET
    );
    printBoxBottom();

    System.out.print(YELLOW + "Enter the room number to allocate: " + RESET);
    int roomNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    try (
      BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE));
      BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))
    ) {
      String line;
      boolean allocated = false;
      while ((line = reader.readLine()) != null) {
        String[] roomData = line.split("\t");
        if (
          roomData.length == 2 &&
          Integer.parseInt(roomData[1]) == roomNumber &&
          roomData[0].equalsIgnoreCase("Available")
        ) {
          allocated = true;
          writer.write("Occupied\t" + roomNumber);
        } else {
          writer.write(line);
        }
        writer.newLine();
      }

      if (allocated) {
        System.out.println(GREEN + "Room allocated successfully!" + RESET);
      } else {
        System.out.println(RED + "Room not available for allocation." + RESET);
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error allocating room: " + e.getMessage() + RESET
      );
    }

    // Rename the temp file to rooms.txt
    File tempFile = new File("temp.txt");
    File originalFile = new File(ROOMS_FILE);
    if (tempFile.renameTo(originalFile)) {
      System.out.println(GREEN + "File updated successfully!" + RESET);
    } else {
      System.err.println(RED + "Error updating file." + RESET);
    }

    pressEnterToContinue(scanner);
  }

  private static void deallocateRoom(Scanner scanner) {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Admin - Deallocate Room:                         |" + RESET
    );
    printBoxBottom();

    System.out.print(YELLOW + "Enter the room number to deallocate: " + RESET);
    int roomNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    try (
      BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE));
      BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))
    ) {
      String line;
      boolean deallocated = false;
      while ((line = reader.readLine()) != null) {
        String[] roomData = line.split("\t");
        if (
          roomData.length == 2 &&
          Integer.parseInt(roomData[1]) == roomNumber &&
          roomData[0].equalsIgnoreCase("Occupied")
        ) {
          deallocated = true;
          writer.write("Available\t" + roomNumber);
        } else {
          writer.write(line);
        }
        writer.newLine();
      }

      if (deallocated) {
        System.out.println(GREEN + "Room deallocated successfully!" + RESET);
      } else {
        System.out.println(RED + "Room not occupied for deallocation." + RESET);
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error deallocating room: " + e.getMessage() + RESET
      );
    }

    // Rename the temp file to rooms.txt
    File tempFile = new File("temp.txt");
    File originalFile = new File(ROOMS_FILE);
    if (tempFile.renameTo(originalFile)) {
      System.out.println(GREEN + "File updated successfully!" + RESET);
    } else {
      System.err.println(RED + "Error updating file." + RESET);
    }

    pressEnterToContinue(scanner);
  }

  private static void userMenu(String username, Scanner scanner) {
    while (true) {
      printTitle();
      System.out.println(
        YELLOW + "| User Menu - Welcome, " + username + "!" + RESET
      );
      printBoxTop();
      System.out.println(
        "| " +
        GREEN +
        "1. View Booking Details" +
        RESET +
        "                    |"
      );
      System.out.println(
        "| " +
        GREEN +
        "2. Make Reservation" +
        RESET +
        "                        |"
      );
      System.out.println(
        "| " + RED + "3. Logout" + RESET + "                                  |"
      );
      printBoxBottom();

      System.out.print(CYAN + "Enter your choice: " + RESET);
      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          viewBookingDetails(username);
          break;
        case 2:
          makeReservation(username, scanner);
          break;
        case 3:
          return;
        default:
          System.out.println(
            RED + "Invalid choice. Please enter a valid option." + RESET
          );
      }
    }
  }

  private static void viewBookingDetails(String username) {
    Scanner scanner = new Scanner(System.in);
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Booking Details for " + username + ":" + RESET
    );
    printBoxBottom();

    try (
      BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE))
    ) {
      String line;
      boolean found = false;
      while ((line = reader.readLine()) != null) {
        if (line.contains(username)) {
          System.out.println(GREEN + "Booking details: " + line + RESET);
          found = true;
          break;
        }
      }

      if (!found) {
        System.out.println(
          RED + "Booking details not found for " + username + RESET
        );
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error reading room details: " + e.getMessage() + RESET
      );
    }

    pressEnterToContinue(scanner);
  }

  private static void viewRoomStatus() {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Current Room Status:                         |" + RESET
    );
    printBoxBottom();

    try (
      BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE))
    ) {
      String line;
      System.out.println(BLUE + "Room Number\tStatus\t\tReserved By" + RESET);
      printBoxTop();
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\t");
        int roomNumber = Integer.parseInt(parts[1]);
        String status = parts[0];
        String reservedBy = (parts.length > 2) ? parts[2] : "";
        System.out.println(
          GREEN + roomNumber + "\t\t" + status + "\t\t" + reservedBy + RESET
        );
      }
      printBoxBottom();
    } catch (IOException e) {
      System.err.println(
        RED + "Error reading room details: " + e.getMessage() + RESET
      );
    }

    pressEnterToContinue(new Scanner(System.in));
  }

  private static void makeReservation(String username, Scanner scanner) {
    printTitle();
    printBoxTop();
    System.out.println(
      YELLOW + "| Make Reservation:                              |" + RESET
    );
    printBoxBottom();
    System.out.print(
      YELLOW + "Enter the room number you want to reserve: " + RESET
    );
    int roomNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    try (
      BufferedWriter writer = new BufferedWriter(
        new FileWriter(ROOMS_FILE, true)
      )
    ) {
      writer.write("Reserved\t" + roomNumber + "\t" + username);
      writer.newLine();
      System.out.println(
        GREEN + "Reservation successful! Enjoy your stay!" + RESET
      );
    } catch (IOException e) {
      System.err.println(
        RED + "Error making reservation: " + e.getMessage() + RESET
      );
    }
    pressEnterToContinue(scanner);
  }

  private static void checkAndCreateFile(String fileName) {
    try {
      // Check if the file exists, create it if not
      File file = new File(fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error creating file: " + e.getMessage() + RESET
      );
    }
  }

  private static void printTitle() {
    System.out.println(
      BLUE + "+----------------------------------------------+" + RESET
    );
    System.out.println(
      BLUE + "|              Hotel Reservation System         |" + RESET
    );
    System.out.println(
      BLUE + "+----------------------------------------------+" + RESET
    );
  }

  private static void printBoxTop() {
    System.out.println(
      YELLOW + "+----------------------------------------------+" + RESET
    );
  }

  private static void printBoxBottom() {
    System.out.println(
      YELLOW + "+----------------------------------------------+" + RESET
    );
  }

  private static boolean authenticateUser(String username, String password) {
    // Your authentication logic here
    // For simplicity, compare with hardcoded values (replace with database or more secure method)
    return username.equals("admin") && password.equals("123");
  }

  private static boolean checkUsernameExists(String username) {
    // Your logic to check if the username exists in the user data file
    // For simplicity, assume a file named "users.txt" with one username per line
    try (
      BufferedReader reader = new BufferedReader(new FileReader("users.txt"))
    ) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals(username)) {
          return true;
        }
      }
    } catch (IOException e) {
      System.err.println(
        RED + "Error reading user data: " + e.getMessage() + RESET
      );
    }
    return false;
  }

  private static void pressEnterToContinue(Scanner scanner) {
    System.out.println(CYAN + "Press Enter to continue..." + RESET);
    scanner.nextLine(); // Wait for user to press Enter
  }
}
