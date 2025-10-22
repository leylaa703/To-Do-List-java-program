package todoapp.model;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id; //Unique id
    private String title; //Task name
    private String description; //Task description
    private LocalDate dueDate; //Deadline
    private Priority priority; //Priority
    private boolean completed; //Completed status

    public Task(String title, String description, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    /**
     * Special method for reassigning IDs during cleanup operations
     * Only to be used by TaskManager for ID reorganization
     */
    public void reassignId(int newId) {
        if (newId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive number: " + newId);
        }
        this.id = newId; // Skip the normal protection
    }

    //GETTERS
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    //SETTERS
    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("Cannot modify task ID after it's set");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive number: " + id);
        }

        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

/*
 * Formats task details for display
 * Shows: ID, title, description (if not empty), deadline, priority, and status
 */
    @Override
    public String toString() {
        DateTimeFormatter formatted = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = dueDate.format(formatted);

        String completeness = completed ? "Done" : "In progress";
        String priorityText = priority.name();

        StringBuilder result = new StringBuilder();
        result.append(id).append(". ").append(title).append("\n");

        if (getDescription() != null && !getDescription().trim().isEmpty()) {
            result.append("Description: ").append(getDescription()).append("\n");
        }

        result.append("Deadline: ").append(formattedDate)
                .append("\nPriority: ").append(priorityText)
                .append("\nStatus: ").append(completeness)
                .append("\n");

        return result.toString();
    }
}
