package com.hrms.retirement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

// Automated dataset generator to create 500+ realistic employee entries in employees.json
public class DataGenerator {

    private static final String[] FIRST_NAMES = {"James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson"};
    private static final String[] DEPARTMENTS = {"Engineering", "Human Resources", "Finance", "Marketing", "Operations", "Legal", "IT Support"};

    public static void generateDataset(String filePath, int recordCount) {
        Random rand = new Random(42); // Seeded for reproducibility

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("[\n");
            for (int i = 1; i <= recordCount; i++) {
                int id = 1000 + i;
                String name = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
                String dept = DEPARTMENTS[rand.nextInt(DEPARTMENTS.length)];
                int age = 22 + rand.nextInt(48); // Ages 22 - 69
                int yearsOfService = Math.min(age - 21, rand.nextInt(40));
                double hours = 30 + rand.nextInt(20); // 30 - 50 hours/week
                double rate = 25.0 + (125.0 - 25.0) * rand.nextDouble(); // $25 - $125/hr
                boolean isRetired = age >= 65 || (age >= 60 && yearsOfService >= 20);
                boolean claimStatus = isRetired && rand.nextBoolean();

                Employee emp = new Employee(id, name, dept, age, yearsOfService, hours, rate, isRetired, claimStatus);
                writer.write(emp.toJsonString());

                if (i < recordCount) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write("]\n");
            System.out.println(" Successfully generated " + recordCount + " records inside " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing dataset file: " + e.getMessage());
        }
    }
}