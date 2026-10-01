package com.stephanie.webdev2.repository;

import com.stephanie.webdev2.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/** Data-access layer. Stores tasks in memory; no database required. */
@Repository
public class TaskRepository {

    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId() != null && task.getId().equals(id))
                .findFirst();
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idCounter.incrementAndGet());
            tasks.add(task);
        }
        return task;
    }
}
