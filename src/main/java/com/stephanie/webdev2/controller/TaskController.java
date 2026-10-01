package com.stephanie.webdev2.controller;

import com.stephanie.webdev2.exception.TaskNotFoundException;
import com.stephanie.webdev2.model.Task;
import com.stephanie.webdev2.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/tasks";
    }

    // View all tasks
    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "task-list";
    }

    // Show empty form
    @GetMapping("/tasks/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    // View one task
    @GetMapping("/tasks/{id}")
    public String showTask(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        model.addAttribute("task", task);
        return "task-detail";
    }

    // Validate -> save -> redirect (Post/Redirect/Get)
    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "task-form"; // redisplay with field errors
        }
        taskService.save(task);
        return "redirect:/tasks";
    }

    // Toggle pending <-> completed, then redirect (Post/Redirect/Get)
    @PostMapping("/tasks/{id}/toggle")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleCompleted(id);
        return "redirect:/tasks";
    }

    // Friendly error page instead of a stack trace
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTaskNotFound(TaskNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/not-found";
    }
}