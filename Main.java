import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

class Customer {
    private String name;
    private double totalSpent;

    public Customer(String name) {
        this.name = name;
        this.totalSpent = 0.0;
    }

    public String getName() {
        return name;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void addSpent(double amount) {
        totalSpent += amount;
    }
}

class Computer {
    private int id;
    private boolean isAvailable;
    private double ratePerHour;
    private Customer currentUser;
    private LocalDateTime sessionStart;

    public Computer(int id, double ratePerHour) {
        this.id = id;
        this.ratePerHour = ratePerHour;
        this.isAvailable = true;
        this.currentUser = null;
        this.sessionStart = null;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Customer getCurrentUser() {
        return currentUser;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public void rent(Customer user) {
        this.isAvailable = false;
        this.currentUser = user;
        this.sessionStart = LocalDateTime.now();
    }

    public double stopRent() {
        if (!isAvailable) {
            Duration duration = Duration.between(sessionStart, LocalDateTime.now());
            double elapsedHours = duration.toMillis() / (1000.0 * 60 * 60);
            double payment = elapsedHours * ratePerHour;
            currentUser.addSpent(payment);
            this.isAvailable = true;
            this.currentUser = null;
            this.sessionStart = null;
            return payment;
        }
        return 0;
    }

    public String getFormattedStartTime() {
        if (sessionStart != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return sessionStart.format(formatter);
        }
        return "N/A";
    }

    @Override
    public String toString() {
        if (isAvailable) {
            return "[ Computer ] " + id + " - Available";
        } else {
            return "Computer " + id + " - Occupied by: " + currentUser.getName() +
                    " | Session Start: " + getFormattedStartTime();
        }
    }
}

class InternetCafe {
    private ArrayList<Computer> computers;
    private ArrayList<Customer> customers;
    private double totalRevenue;

    public InternetCafe(int numberOfComputers, double ratePerHour) {
        computers = new ArrayList<>();
        customers = new ArrayList<>();
        totalRevenue = 0.0;
        for (int i = 1; i <= numberOfComputers; i++) {
            computers.add(new Computer(i, ratePerHour));
        }
    }

    public void displayComputers() {
        System.out.println("\n==== [Computer Status] ====");
        for (Computer c : computers) {
            System.out.println(c);
        }
    }

    public void viewActiveCustomers() {
        System.out.println("\n===== ACTIVE CUSTOMERS =====");
        boolean found = false;
        for (Computer c : computers) {
            if (!c.isAvailable()) {
                found = true;
                Duration duration = Duration.between(c.getSessionStart(), LocalDateTime.now());
                long minutes = duration.toMinutes();
                System.out.println("PC " + c.getId() +
                        " | User: " + c.getCurrentUser().getName() +
                        " | Session Start: " + c.getFormattedStartTime() +
                        " | Elapsed: " + minutes + " mins");
            }
        }
        if (!found) System.out.println("No active customers.");
    }

    public void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        if (customers.isEmpty()) {
            System.out.println("No customers yet.");
        } else {
            for (Customer c : customers) {
                System.out.println("Name: " + c.getName() + ", Total Spent: PHP " + String.format("%.2f", c.getTotalSpent()));
            }
        }
    }

    public Computer getComputer(int id) {
        for (Computer c : computers) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public Customer getOrCreateCustomer(String name) {
        for (Customer c : customers) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        Customer newCustomer = new Customer(name);
        customers.add(newCustomer);
        return newCustomer;
    }

    public void addRevenue(double amount) {
        totalRevenue += amount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        InternetCafe cafe = new InternetCafe(5, 20.0);

        int choice;

        do {
            System.out.println("\n===== INTERNET CAFE =====");
            System.out.println("1. Show Computers");
            System.out.println("2. Rent a Computer");
            System.out.println("3. Stop Rent & Pay");
            System.out.println("4. View Active Customers");
            System.out.println("5. View All Customers");
            System.out.println("6. View Total Revenue");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1:
                    cafe.displayComputers();
                    break;

                case 2:
                    System.out.print("Enter computer ID to rent (1-5): ");
                    try {
                        int rentId = sc.nextInt();
                        sc.nextLine();

                        if (rentId < 1 || rentId > 5) {
                            System.out.println("Invalid computer ID. Must be 1-5.");
                            break;
                        }

                        Computer toRent = cafe.getComputer(rentId);

                        if (toRent == null || !toRent.isAvailable()) {
                            System.out.println("Computer not available.");
                        } else {
                            System.out.print("Enter your name: ");
                            String name = sc.nextLine().trim();

                            if (name.isEmpty()) {
                                System.out.println("Name cannot be empty.");
                                break;
                            }

                            Customer customer = cafe.getOrCreateCustomer(name);
                            toRent.rent(customer);
                            System.out.println(name + " rented Computer " + rentId + ". Session started at " + toRent.getFormattedStartTime());
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input for renting. Try again.");
                        sc.nextLine();
                    }
                    break;

                case 3:
                    System.out.print("Enter computer ID to stop (1-5): ");
                    try {
                        int stopId = sc.nextInt();
                        sc.nextLine();

                        if (stopId < 1 || stopId > 5) {
                            System.out.println("Invalid computer ID. Must be 1-5.");
                            break;
                        }

                        Computer toStop = cafe.getComputer(stopId);

                        if (toStop == null || toStop.isAvailable()) {
                            System.out.println("[Computer is not currently rented].");
                        } else {
                            String userName = toStop.getCurrentUser().getName();
                            double payment = toStop.stopRent();
                            cafe.addRevenue(payment);
                            System.out.println("Session ended for " + userName);
                            System.out.println("Payment: PHP " + String.format("%.2f", payment));
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input for stopping. Try again.");
                        sc.nextLine();
                    }
                    break;

                case 4:
                    cafe.viewActiveCustomers();
                    break;

                case 5:
                    cafe.viewAllCustomers();
                    break;

                case 6:
                    System.out.println("Total Revenue: PHP " + String.format("%.2f", cafe.getTotalRevenue()));
                    break;

                case 0:
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 0-6.");
            }

        } while (choice != 0);

        sc.close();
    }
}
