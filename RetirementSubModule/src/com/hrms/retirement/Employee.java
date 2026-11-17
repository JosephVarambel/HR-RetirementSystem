package com.hrms.retirement;

// Simple model class representing an employee's financial and retirement profile
public class Employee {
    private int id;
    private String name;
    private String department;
    private int age;
    private int yearsOfService;
    private double hoursWorkedPerWeek;
    private double hourlyRate;
    private boolean isRetired;
    private boolean claimStatus;

    // Constructor to initialize an employee record
    public Employee(int id, String name, String department, int age, int yearsOfService, 
                    double hoursWorkedPerWeek, double hourlyRate, boolean isRetired, boolean claimStatus) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.age = age;
        this.yearsOfService = yearsOfService;
        this.hoursWorkedPerWeek = hoursWorkedPerWeek;
        this.hourlyRate = hourlyRate;
        this.isRetired = isRetired;
        this.claimStatus = claimStatus;
    }

    // --- Math Calculations ---

    // Calculates base pension payout based on hours, hourly wage, and service duration
    public double calculatePensionPayout() {
        double annualBase = hoursWorkedPerWeek * hourlyRate * 52;
        return annualBase * (yearsOfService * 0.025); // 2.5% multiplier per year worked
    }

    // Financial Projection Engine: Calculates estimated future pension after X extra years
    public double calculateFuturePension(int additionalYears) {
        double projectedYears = yearsOfService + additionalYears;
        double annualBase = hoursWorkedPerWeek * hourlyRate * 52;
        return annualBase * (projectedYears * 0.025);
    }

    // Prioritization Score for the Min/Max Heap Queue
    public double getRetirementEligibilityScore() {
        return (age * 0.6) + (yearsOfService * 0.4);
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getYearsOfService() { return yearsOfService; }
    public void setYearsOfService(int yearsOfService) { this.yearsOfService = yearsOfService; }
    public double getHoursWorkedPerWeek() { return hoursWorkedPerWeek; }
    public void setHoursWorkedPerWeek(double hours) { this.hoursWorkedPerWeek = hours; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double rate) { this.hourlyRate = rate; }
    public boolean isRetired() { return isRetired; }
    public void setRetired(boolean retired) { isRetired = retired; }
    public boolean isClaimStatus() { return claimStatus; }
    public void setClaimStatus(boolean claimStatus) { this.claimStatus = claimStatus; }

    // Helper method to turn employee data into a custom formatted string line for JSON saving
    public String toJsonString() {
        return String.format(
            "  {\"id\": %d, \"name\": \"%s\", \"department\": \"%s\", \"age\": %d, \"yearsOfService\": %d, \"hoursWorkedPerWeek\": %.1f, \"hourlyRate\": %.2f, \"isRetired\": %b, \"claimStatus\": %b}",
            id, name, department, age, yearsOfService, hoursWorkedPerWeek, hourlyRate, isRetired, claimStatus
        );
    }
}