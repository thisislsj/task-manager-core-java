package com.lahiruj;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    int nextId = 1;

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Optional<Task> getTaskById(int id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst();
    }

    public Task addTask(String title, String description) {
        Task newTask = new Task(nextId++, title, description, "Pending");
        tasks.add(newTask);
        return newTask;
    }

    public Optional<Task> updateTask(int id, String title, String description, String status) {
        Optional<Task> optionalTask = getTaskById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(title);
            task.setDescription(description);
            task.setStatus(status);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    public boolean deleteTask(int taskId) {
        return tasks.removeIf(task -> task.getId() == taskId);
    }
}
