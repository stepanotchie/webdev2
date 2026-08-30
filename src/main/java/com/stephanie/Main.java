package com.stephanie;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Roster roster = new Roster();

        // TASK 4: Exception Handling Demonstration
        System.out.println("=== TASK 4: Exception Handling Demonstration ===");
        try {
            // Attempt creation with invalid negative age
            Student invalidStudent = new Student("User", -5, "BSIT");
            roster.addStudent(invalidStudent);
        } catch (InvalidAgeException e) {
            System.err.println("[ERROR CAUGHT!]" + e.getMessage());
        }
        System.out.println("[STATUS] Program execution continued safely after handling exception.\n");

        // TASK 3: Roster Population & Display
        System.out.println("=== TASK 3: Populating Roster with 6 Students ===");
        try {
            roster.addStudent(new Student("Owen Anchola", 19, "BSCS"));
            roster.addStudent(new Student("Jan Liam Arias", 17, "BSCS"));
            roster.addStudent(new Student("Geoff Benedict Cariño", 20, "BSIT"));
            roster.addStudent(new Student("Brianna Bustamante", 16, "BSIT"));
            roster.addStudent(new Student("Janiyah Tan", 22, "BSIT"));
            roster.addStudent(new Student("Jhelliane Figuerres", 18, "BSIT"));
        } catch (InvalidAgeException e) {
            System.err.println("Unexpected error adding valid student: " + e.getMessage());
        }

        System.out.println("\n--- Full Student Roster ---");
        for (Student student : roster.getStudents()) {
            System.out.println(student);
        }

        // TASK 5: Stream-Based Roster Report
        System.out.println("==================================================");
        System.out.println("  TASK 5: Stream-Based Roster Report (Age >= 18)");
        System.out.println("==================================================");
        generateAdultReport(roster);
    }

    // TASK 5: Stream pipeline (filter -> map -> terminal count/list)
    public static void generateAdultReport(Roster roster) {
        List<String> qualifyingNames = roster.getStudents().stream()
                .filter(student -> student.getAge() >= 18) // (a) Filter 18+
                .map(Student::getName) // (b) Map to names
                .toList(); // Collect results

        long count = qualifyingNames.size(); // (c) Count qualifying

        System.out.printf("%-20s %s%n", "[QUALIFYING NAMES]", String.join(", ", qualifyingNames));
        System.out.printf("%-20s %d%n", "[TOTAL COUNT]", count);
    }
}
