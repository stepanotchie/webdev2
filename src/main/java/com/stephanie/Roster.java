package com.stephanie;

import java.util.ArrayList;
import java.util.List;

// TASK 3: Roster Management using Generics (List<Student>)
public class Roster {
    private final List<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        if (student != null) {
            students.add(student);
        }
    }

    public List<Student> getStudents() {
        return students;
    }
}
