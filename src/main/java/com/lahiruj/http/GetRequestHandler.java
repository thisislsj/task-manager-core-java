package com.lahiruj.http;

import com.lahiruj.Task;
import com.lahiruj.TaskManager;
import com.lahiruj.util.JsonUtil;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class GetRequestHandler implements HttpRequestHandler {
    private TaskManager taskManager;

    public GetRequestHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handleRequest(String resource, PrintWriter out, Socket clientSocket) {
        if (resource.equals("/tasks")) {
            List<Task> tasks = taskManager.getAllTasks();
            sendResponse(out, "HTTP/1.1 200 OK", "application/json", JsonUtil.tasksToJson(tasks));
        } else if (resource.startsWith("/tasks/")) {
            String[] parts = resource.split("/");
            int taskId = Integer.parseInt(parts[2]);
            Optional<Task> task = taskManager.getTaskById(taskId);
            if (task.isPresent()) {
                sendResponse(out,"HTTP/1.1 200 OK", "application/json", JsonUtil.taskToJson(task.get()));
            } else {
                sendResponse(out, "HTTP/1.1 404 Not Found", "text/plain", "Task Not Found");
            }
        } else {
            sendResponse(out, "HTTP/1.1 404 Not Found", "text/plain", "Not Found");
        }
    }

    private void sendResponse(PrintWriter out, String status, String contentType, String content) {
        out.println(status);
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + content.length());
        out.println();
        out.println(content);
    }
}
