package com.hrms.retirement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Main CLI Application supporting all required functional operations
public class RetirementApp {

    private static final String DATA_FILE = "employees.json";
    private static FastProfileIndex profileIndex = new FastProfileIndex(1024);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Check if employees.json exists; generate 500 records automatically if missing
        java.io.File file = new java.io.File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("Dataset missing. Generating 500 initial employee records...");
            DataGenerator.generateDataset(DATA_FILE, 500);
        }

        // Load JSON File into fast index
        loadEmployeeData(DATA_FILE);

        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(" 1. Add New Employee Record (Addition)");
            System.out.println(" 2. Remove Employee Record (Removal)");
            System.out.println(" 3. Modify Employee Status / Details (Modification)");
            System.out.println(" 4. Rank/Sort Upcoming Retirees (Sorting)");
            System.out.println(" 5. Lookup Employee by ID (Search)");
            System.out.println(" 6. Print All Employee Records (Printing)");
            System.out.println(" 7. Financial Projection Calculator");
            System.out.println(" 8. Run Offboarding Task Workflow Engine");
            System.out.println(" 9. Exit & Save Changes");
            System.out.println("=========================================================================");
            System.out.print("Select an option (1-9): ");

            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input) {
                case "1" -> addEmployeeUI();
                case "2" -> removeEmployeeUI();
                case "3" -> modifyEmployeeUI();
                case "4" -> sortAndDisplayRetireesUI();
                case "5" -> lookupEmployeeUI();
                case "6" -> printAllEmployeesUI();
                case "7" -> projectionCalculatorUI();
                case "8" -> runOffboardingWorkflowUI();
                case "9" -> {
                    saveEmployeeData(DATA_FILE);
                    running = false;
                    System.out.println("Thank you for using HRMS Retirement Sub-Module. Goodbye!");
                }
                default -> System.out.println("[!] Invalid choice. Please pick an option between 1 and 9.");
            }
            if (running) {
                System.out.println("\nPress ENTER to return to the main menu...");
                scanner.nextLine();
            }
        }
    }

    private static void printHeader() {
        System.out.println("\n=========================================================================");
        System.out.println("                HRMS RETIREMENT & PENSION SUB-MODULE                     ");
        System.out.println("=========================================================================");
    }

    // 1. ADDITION OPERATION
    private static void addEmployeeUI() {
        System.out.println("--- ADD NEW EMPLOYEE ---");
        try {
            System.out.print("Enter Unique Employee ID (e.g. 2001): ");
            int id = Integer.parseInt(scanner.nextLine());
            if (profileIndex.get(id) != null) {
                System.out.println("[!] ID already exists in the system!");
                return;
            }
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Department: ");
            String dept = scanner.nextLine();
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Years of Service: ");
            int service = Integer.parseInt(scanner.nextLine());
            System.out.print("Hours Worked/Week: ");
            double hours = Double.parseDouble(scanner.nextLine());
            System.out.print("Hourly Wage ($): ");
            double rate = Double.parseDouble(scanner.nextLine());

            Employee emp = new Employee(id, name, dept, age, service, hours, rate, false, false);
            profileIndex.put(id, emp);
            System.out.println("\n[✓] Employee record successfully added!");
        } catch (Exception e) {
            System.out.println("[!] Invalid input format. Operation canceled.");
        }
    }

    // 2. REMOVAL OPERATION
    private static void removeEmployeeUI() {
        System.out.println("--- REMOVE EMPLOYEE RECORD ---");
        try {
            System.out.print("Enter ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            if (profileIndex.remove(id)) {
                System.out.println("[✓] Employee " + id + " successfully removed from system.");
            } else {
                System.out.println("[!] Employee ID not found.");
            }
        } catch (Exception e) {
            System.out.println("[!] Invalid ID input.");
        }
    }

    // 3. MODIFICATION OPERATION
    private static void modifyEmployeeUI() {
        System.out.println("--- MODIFY EMPLOYEE DETAILS ---");
        try {
            System.out.print("Enter Employee ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee emp = profileIndex.get(id);
            if (emp == null) {
                System.out.println("[!] Employee record not found.");
                return;
            }

            System.out.println("Modifying record for: " + emp.getName());
            System.out.println("1. Toggle Retirement Status (Current: " + (emp.isRetired() ? "Retired" : "Active") + ")");
            System.out.println("2. Toggle Claim Status (Current: " + (emp.isClaimStatus() ? "Claimed" : "Unclaimed") + ")");
            System.out.println("3. Update Hourly Rate (Current: $" + String.format("%.2f", emp.getHourlyRate()) + ")");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    emp.setRetired(!emp.isRetired());
                    System.out.println("[✓] Status updated to: " + (emp.isRetired() ? "Retired" : "Active"));
                }
                case "2" -> {
                    emp.setClaimStatus(!emp.isClaimStatus());
                    System.out.println("[✓] Claim Status updated to: " + (emp.isClaimStatus() ? "Claimed" : "Unclaimed"));
                }
                case "3" -> {
                    System.out.print("Enter new hourly wage ($): ");
                    double newRate = Double.parseDouble(scanner.nextLine());
                    emp.setHourlyRate(newRate);
                    System.out.println("[✓] Hourly wage updated.");
                }
                default -> System.out.println("[!] Invalid selection.");
            }
        } catch (Exception e) {
            System.out.println("[!] Input error.");
        }
    }

    // 4. SORTING OPERATION (Priority Queue Ranking)
    private static void sortAndDisplayRetireesUI() {
        System.out.println("--- TOP UPCOMING RETIREES (PRIORITY QUEUE HEAP SORT) ---");
        List<Employee> all = profileIndex.getAllEmployees();

        RetirementPriorityQueue pq = new RetirementPriorityQueue((e1, e2) -> 
            Double.compare(e2.getRetirementEligibilityScore(), e1.getRetirementEligibilityScore())
        );

        for (Employee e : all) {
            if (!e.isRetired()) {
                pq.insert(e);
            }
        }

        System.out.printf("%-6s | %-20s | %-5s | %-7s | %-12s | %-15s\n", "ID", "Name", "Age", "Service", "Score", "Est. Pension Payout");
        System.out.println("-------------------------------------------------------------------------");

        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            Employee e = pq.extractTop();
            System.out.printf("%-6d | %-20s | %-5d | %-7d | %-12.2f | $%-14.2f\n",
                    e.getId(), e.getName(), e.getAge(), e.getYearsOfService(),
                    e.getRetirementEligibilityScore(), e.calculatePensionPayout());
            count++;
        }
    }

    // 5. LOOKUP / SEARCH OPERATION
    private static void lookupEmployeeUI() {
        System.out.println("--- SEARCH EMPLOYEE BY ID ---");
        try {
            System.out.print("Enter Employee ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            long startTime = System.nanoTime();
            Employee emp = profileIndex.get(id);
            long endTime = System.nanoTime();

            if (emp != null) {
                System.out.println("\n-------------------------------------------------------------------------");
                System.out.println("ID:             " + emp.getId());
                System.out.println("Name:           " + emp.getName());
                System.out.println("Department:     " + emp.getDepartment());
                System.out.println("Age:            " + emp.getAge());
                System.out.println("Service Years:  " + emp.getYearsOfService());
                System.out.println("Weekly Hours:   " + emp.getHoursWorkedPerWeek());
                System.out.println("Hourly Rate:    $" + String.format("%.2f", emp.getHourlyRate()));
                System.out.println("Status:         " + (emp.isRetired() ? "Retired" : "Active"));
                System.out.println("Pension Claim:  " + (emp.isClaimStatus() ? "Claimed" : "Unclaimed"));
                System.out.printf("Calculated Pension: $%.2f\n", emp.calculatePensionPayout());
                System.out.println("-------------------------------------------------------------------------");
                System.out.printf("Lookup Execution Speed: %.4f ms\n", (endTime - startTime) / 1_000_000.0);
            } else {
                System.out.println("[!] Employee ID not found.");
            }
        } catch (Exception e) {
            System.out.println("[!] Invalid ID.");
        }
    }

    // 6. PRINTING OPERATION
    private static void printAllEmployeesUI() {
        List<Employee> list = profileIndex.getAllEmployees();
        System.out.println("--- PRINTING ALL EMPLOYEES (Total: " + list.size() + ") ---");
        System.out.printf("%-6s | %-18s | %-15s | %-4s | %-5s | %-9s | %-10s\n", "ID", "Name", "Dept", "Age", "Years", "Status", "Pension");
        System.out.println("-------------------------------------------------------------------------------");

        int limit = Math.min(list.size(), 20); // First 20 entries
        for (int i = 0; i < limit; i++) {
            Employee e = list.get(i);
            System.out.printf("%-6d | %-18s | %-15s | %-4d | %-5d | %-9s | $%-9.2f\n",
                    e.getId(), e.getName(), e.getDepartment(), e.getAge(), e.getYearsOfService(),
                    (e.isRetired() ? "Retired" : "Active"), e.calculatePensionPayout());
        }
        if (list.size() > 20) {
            System.out.println("... [" + (list.size() - 20) + " additional records truncated for display readability]");
        }
    }

    // 7. FINANCIAL PROJECTION ENGINE
    private static void projectionCalculatorUI() {
        System.out.println("--- PENSION FINANCIAL PROJECTION ENGINE ---");
        try {
            System.out.print("Enter Employee ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            Employee emp = profileIndex.get(id);

            if (emp == null) {
                System.out.println("[!] Employee record not found.");
                return;
            }

            System.out.print("Enter projected future work years (e.g. 5): ");
            int years = Integer.parseInt(scanner.nextLine());

            double currentPayout = emp.calculatePensionPayout();
            double projectedPayout = emp.calculateFuturePension(years);

            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("Employee Name:           " + emp.getName());
            System.out.println("Current Service Duration: " + emp.getYearsOfService() + " years");
            System.out.printf("Current Pension Payout:   $%.2f\n", currentPayout);
            System.out.printf("Projected Pension Payout (%d yrs later): $%.2f\n", years, projectedPayout);
            System.out.printf("Net Benefit Increase:     +$%.2f\n", (projectedPayout - currentPayout));
            System.out.println("-------------------------------------------------------------------------");
        } catch (Exception e) {
            System.out.println("[!] Invalid numerical input.");
        }
    }

    // 8. OFFBOARDING WORKFLOW ENGINE
    private static void runOffboardingWorkflowUI() {
        System.out.println("--- OFFBOARDING TASK WORKFLOW EXECUTION ---");
        System.out.println("Generating step-by-step checklist using Topological Sort on DAG...");
        List<String> checklist = OffboardingEngine.generateOffboardingChecklist();

        System.out.println("\n=========================================================================");
        System.out.println("              ORDERED OFFBOARDING CHECKLIST SEQUENCE                     ");
        System.out.println("=========================================================================");
        for (int i = 0; i < checklist.size(); i++) {
            System.out.printf(" Step %d: %s\n", (i + 1), checklist.get(i));
        }
        System.out.println("=========================================================================");
    }

    // Parse JSON File manually without external libraries
    private static void loadEmployeeData(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{") && line.endsWith("}") || line.endsWith("},")) {
                    line = line.replace("{", "").replace("}", "").replace(",", "");
                    String[] parts = line.split("\"");

                    int id = 0, age = 0, service = 0;
                    String name = "", dept = "";
                    double hours = 0, rate = 0;
                    boolean retired = false, claim = false;

                    for (int i = 0; i < parts.length; i++) {
                        String token = parts[i].trim();
                        if (token.equals("id")) id = extractInt(parts[i + 1]);
                        if (token.equals("name")) name = parts[i + 2];
                        if (token.equals("department")) dept = parts[i + 2];
                        if (token.equals("age")) age = extractInt(parts[i + 1]);
                        if (token.equals("yearsOfService")) service = extractInt(parts[i + 1]);
                        if (token.equals("hoursWorkedPerWeek")) hours = extractDouble(parts[i + 1]);
                        if (token.equals("hourlyRate")) rate = extractDouble(parts[i + 1]);
                        if (token.equals("isRetired")) retired = parts[i + 1].contains("true");
                        if (token.equals("claimStatus")) claim = parts[i + 1].contains("true");
                    }
                    if (id != 0) {
                        profileIndex.put(id, new Employee(id, name, dept, age, service, hours, rate, retired, claim));
                    }
                }
            }
            System.out.println("[✓] Loaded " + profileIndex.getSize() + " records into Fast Profile Index.");
        } catch (IOException e) {
            System.err.println("[!] Failed loading JSON file: " + e.getMessage());
        }
    }

    private static void saveEmployeeData(String filePath) {
        List<Employee> list = profileIndex.getAllEmployees();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("[\n");
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i).toJsonString());
                if (i < list.size() - 1) writer.write(",\n");
                else writer.write("\n");
            }
            writer.write("]\n");
            System.out.println("[✓] Employee database successfully saved to " + filePath);
        } catch (IOException e) {
            System.err.println("[!] Save failed: " + e.getMessage());
        }
    }

    private static int extractInt(String str) {
        return Integer.parseInt(str.replaceAll("[^0-0123456789]", "").trim());
    }

    private static double extractDouble(String str) {
        return Double.parseDouble(str.replaceAll("[^0-9.]", "").trim());
    }
}