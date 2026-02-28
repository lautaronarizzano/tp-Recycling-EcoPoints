import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class EcoPointsRecyclingTracker {

    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, Household> households = new HashMap<>();

    public static void main(String[] args) {
        loadHouseholdsFromFile();

        boolean running = true;
        while (running) {
            System.out.println("\n=== Eco-Points Recycling Tracker ===");
            System.out.println("1. Register Household");
            System.out.println("2. Log Recycling Event");
            System.out.println("3. Display Households");
            System.out.println("4. Display Household Recycling Events");
            System.out.println("5. Generate Reports");
            System.out.println("6. Save and Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerHousehold();
                    break;
                case "2":
                    logRecyclingEvent();
                    break;
                case "3":
                    displayHouseholds();
                    break;
                case "4":
                    displayHouseholdEvents();
                    break;
                case "5":
                    generateReports();
                    break;
                case "6":
                    saveHouseholdsToFile();
                    running = false;
                    System.out.println("Data saved. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-6.");
            }

        }
    }

    private static void registerHousehold() {

        System.out.print("Enter household ID: ");
        String id = scanner.nextLine().trim();

        if (households.containsKey(id)) {
            System.out.println("Error: Household ID already exists.");
            return;
        }

        System.out.print("Enter household name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter household address: ");
        String address = scanner.nextLine().trim();

        Household household = new Household(id, name, address);

        households.put(id, household);

        System.out.println("Household registered successfully on " + household.getJoinDate());
    }

    private static void logRecyclingEvent() {
        System.out.print("Enter household ID: ");
        String id = scanner.nextLine().trim();

        Household household = households.get(id);

        if (household == null) {

            System.out.println("Error: Household ID not found.");
            return;
        }

        System.out.print("Enter material type (plastic/glass/metal/paper): ");
        String material = scanner.nextLine().trim();

        double weight = 0.0;

        while (true) {
            try {
                System.out.print("Enter weight in kilograms: ");
                weight = Double.parseDouble(scanner.nextLine());

                if (weight <= 0)
                    throw new IllegalArgumentException();

                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid weight. Must be a positive number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid weight. Must be a positive number.");
            }
        }

        RecyclingEvent event = new RecyclingEvent(material, weight);

        household.addEvent(event);

        System.out.println("Recycling event logged! Points earned: " + event.getEcoPoints());
    }

    // Task 6
    private static void displayHouseholds() {
        // Check if the households map is empty
        if (households.isEmpty()) {
            System.out.println("No households registered.");
            return; // Exit early if there's nothing to show
        }

        // If there are households, print a header first
        System.out.println("\nRegistered Households:");

        // Loop through each household in the map and print its details
        for (Household h : households.values()) {
            System.out.println("ID: " + h.getId() +
                    ", Name: " + h.getName() +
                    ", Address: " + h.getAddress() +
                    ", Joined: " + h.getJoinDate());
        }
    }

    private static void displayHouseholdEvents() {

        System.out.print("Enter household ID: ");
        String id = scanner.nextLine().trim();

        Household household = households.get(id);

        if (household == null) {
            System.out.println("Household not found.");
            return;
        }

        System.out.println("\nRecycling Events for " + household.getName() + ":");

        if (household.getEvents().isEmpty()) {
            System.out.println("No events logged.");
        } else {

            for (RecyclingEvent e : household.getEvents()) {

                System.out.println(e);
            }

            System.out.println("Total Weight: " + household.getTotalWeight() + " kg");

            System.out.println("Total Points: " + household.getTotalPoints() + " pts");
        }
    }

    private static void generateReports() {

        if (households.isEmpty()) {
            System.out.println("No households registered.");
            return;
        }

        Household top = null;
        for (Household h : households.values()) {

            if (top == null || h.getTotalPoints() > top.getTotalPoints()) {
                top = h;
            }
        }

        System.out.println("\nHousehold with Highest Points:");
        System.out.println("ID: " + top.getId() +
                ", Name: " + top.getName() +
                ", Points: " + top.getTotalPoints());

        double totalWeight = 0.0;

        for (Household h : households.values()) {
            totalWeight += h.getTotalWeight();
        }

        System.out.println("Total Community Recycling Weight: " + totalWeight + " kg");
    }

    private static void saveHouseholdsToFile() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("households.ser"));

            out.writeObject(households);

        } catch (IOException e) {

            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadHouseholdsFromFile() {

        try (

                ObjectInputStream in = new ObjectInputStream(new FileInputStream("households.ser"))) {

            households = (Map<String, Household>) in.readObject();

            System.out.println("Household data loaded.");
        } catch (FileNotFoundException e) {

            System.out.println("No saved data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {

            System.out.println("Error loading data: " + e.getMessage());
        }
    }

}
