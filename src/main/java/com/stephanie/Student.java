package com.stephanie;

// TASK 2: Encapsulated Domain Class implementing Gradable
public class Student implements Gradable {
    private String name;
    private int age;
    private String course;

    public Student(String name, int age, String course) {
        this.name = name;
        setAge(age); // Route through setter for validation
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    // TASK 4: Age setter throwing InvalidAgeException instead of silent rejection
    public void setAge(int age) {
        if (age < 0) {
            throw new InvalidAgeException("Age cannot be negative: " + age);
        }
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    // TASK 2: Gradable interface method implementation
    @Override
    public String computeStanding() {
        return age >= 18 ? "Good Standing" : "Provisional Standing";
    }

    @Override
    public String toString() {
        return String.format("""
                ----------------------------------------
                Name     : %s
                Age      : %d
                Course   : %s
                Standing : %s""",
                name, age, course, computeStanding());
    }
}
