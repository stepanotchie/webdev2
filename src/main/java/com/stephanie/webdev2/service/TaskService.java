package com.stephanie.webdev2.service;

import com.stephanie.webdev2.exception.TaskNotFoundException;
import com.stephanie.webdev2.model.Task;
import com.stephanie.webdev2.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Single constructor => Spring injects the TaskRepository bean automatically.
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    // Flip a task between pending and completed
    public void toggleCompleted(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setCompleted(!task.isCompleted());
    }
}