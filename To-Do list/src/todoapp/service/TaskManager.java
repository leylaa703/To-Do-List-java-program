package todoapp.service;

import todoapp.model.Priority;
import todoapp.model.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * TaskManager class handles all task operations and data persistence
 */

public class TaskManager {

    private List<Task> tasks; //Main task storage

    /**
     * Loads tasks from persistent storage on initialization
     */
    public TaskManager() {
        this.tasks = DataPersistence.loadTasks();
    }

    /**
     * Clears all tasks from memory and deletes data file
     */
    public void deleteAllData() {
        tasks.clear();
        DataPersistence.deleteSavedData();
    }

    /**
     * Adds new task with auto-generated ID (max existing ID + 1)
     *
     * @param task the task to add to the task list
     */
    public void addTask(Task task) {
        int maxId = tasks.stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0); //Start from 1 if list is empty
        task.setId(maxId + 1);
        tasks.add(task);
        DataPersistence.saveTasks(tasks); //Auto-save after changes
    }

    /**
     * Displays tasks filtered by completion status and due date
     *
     * @param filter the filter criteria to apply when displaying tasks
     */
    public void viewTasks(TaskFilter filter) {
        System.out.println("\n ========= To-Do List ========\n");

        List<Task> filteredTasks = new ArrayList<>();

        switch (filter) {
            case ALL -> filteredTasks.addAll(tasks);
            case COMPLETE -> {
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        filteredTasks.add(task);
                    }
                }
            }
            case INCOMPLETE -> {
                for (Task task : tasks) {
                    if (!task.isCompleted()) {
                        filteredTasks.add(task);
                    }
                }
            }
            case OVERDUE -> {
                LocalDate today = LocalDate.now();
                for (Task task : tasks) {
                    if (!task.isCompleted() && task.getDueDate().isBefore(today)) {
                        filteredTasks.add(task);
                    }
                }
            }
        }

        if (filteredTasks.isEmpty()) {
            System.out.println("!No tasks found!");
        } else {
            for (Task task : filteredTasks) {
                System.out.println(task);
            }
        }
    }

    /**
     * Finds task by unique ID, returns null if not found
     *
     * @param id the ID of the task to find
     * @return the found task or null if not found
     */
    public Task findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }

        return null;
    }

    /**
     * Finds task by title
     *
     * @param searchText the text to search for in task titles
     * @return list of tasks containing the search text in their title
     */
    public List<Task> findByTitle(String searchText) {
        List<Task> foundTasks = new ArrayList<>();
        String searchTextLower = searchText.toLowerCase();

        for (Task task : tasks) {
            if (task.getTitle().toLowerCase().contains(searchTextLower)) {
                foundTasks.add(task);
            }
        }

        return foundTasks;
    }

    /**
     * Finds task by description (include searching tasks with empty description)
     *
     * @param searchText the text to search for in task descriptions,
     *                   empty string to find tasks with empty descriptions
     * @return list of tasks matching the description criteria
     */
    public List<Task> findByDescription(String searchText) {
        List<Task> foundTasks = new ArrayList<>();
        String searchTextLow = searchText.toLowerCase();

        //Special case: find tasks with empty descriptions
        if (searchText.trim().isEmpty()) {
            for (Task task : tasks) {
                String description = task.getDescription();

                if (description.trim().isEmpty()) {
                    foundTasks.add(task);
                }
            }

            return foundTasks;
        }

        //Normal search: tasks containing text in description
        for (Task task : tasks) {
            String description = task.getDescription();

            if (description.toLowerCase().contains(searchTextLow)) {
                foundTasks.add(task);
            }
        }

        return foundTasks;
    }

    /**
     * Finds tasks by priority level (LOW, MEDIUM, HIGH)
     *
     * @param taskPriority the priority level to filter by
     * @return list of tasks with the specified priority
     */
    public List<Task> findByPriority(Priority taskPriority) {
        List<Task> foundTasks = new ArrayList<>();

        for (Task task : tasks) {
            Priority priority = task.getPriority();
            if (taskPriority.equals(priority)) {
                foundTasks.add(task);
            }
        }

        return foundTasks;
    }

    /**
     * Finds tasks by exact due date match
     *
     * @param date the due date to search for
     * @return list of tasks with the specified due date
     */
    public List<Task> findByDate(LocalDate date) {
        List<Task> foundTasks = new ArrayList<>();

        for (Task task : tasks) {
            LocalDate taskDueDateDate = task.getDueDate();
            if (taskDueDateDate.equals(date)) {
                foundTasks.add(task);
            }
        }

        return foundTasks;
    }

    /**
     * Removes task by ID, returns true if found and deleted
     *
     * @param id the ID of the task to remove
     * @return true if task was found and removed, false otherwise
     */
    public boolean removeTask(int id) {
        Task task = findById(id);

        if (task != null) {
            tasks.remove(task);
            reassignTaskId();
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Reassigns sequential IDs to all tasks after deletions
     * Prevents gaps in task numbering (1, 2, 3, 4...)
     */
    public void reassignTaskId() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).reassignId(i + 1); // Use special reassign method
        }
    }

    /**
     * Updates the title of a task
     *
     * @param id the ID of the task to update
     * @param newName the new title for the task
     * @return true if task was found and updated, false otherwise
     */
    public boolean updateTitle(int id, String newName) {
        Task task = findById(id);

        if (task != null) {
            task.setTitle(newName);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Updates the description of a task
     *
     * @param id the ID of the task to update
     * @param newDescription the new description for the task
     * @return true if task was found and updated, false otherwise
     */
    public boolean updateDescription(int id, String newDescription) {
        Task task = findById(id);

        if (task != null) {
            task.setDescription(newDescription);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Updates the due date of a task
     *
     * @param id the ID of the task to update
     * @param newDate the new due date for the task
     * @return true if task was found and updated, false otherwise
     */
    public boolean updateDate(int id, LocalDate newDate) {
        Task task = findById(id);

        if (task != null) {
            task.setDueDate(newDate);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Updates the priority of a task
     *
     * @param id the ID of the task to update
     * @param newPriority the new priority for the task
     * @return true if task was found and updated, false otherwise
     */
    public boolean updatePriority(int id, Priority newPriority) {
        Task task = findById(id);

        if (task != null) {
            task.setPriority(newPriority);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Marks a task as completed
     *
     * @param id the ID of the task to mark as completed
     * @return true if task was found and updated, false otherwise
     */
    public boolean markCompleted(int id) {
        Task task = findById(id);

        if (task != null) {
            task.setCompleted(true);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Marks a task as incomplete
     *
     * @param id the ID of the task to mark as incomplete
     * @return true if task was found and updated, false otherwise
     */
    public boolean markIncompleted(int id) {
        Task task = findById(id);

        if (task != null) {
            task.setCompleted(false);
            DataPersistence.saveTasks(tasks);
            return true;
        }

        return false;
    }

    /**
     * Sorts tasks by due date in ascending order
     */
    public void sortByDueDate() {
        tasks.sort((task1, task2) -> task1.getDueDate().compareTo(task2.getDueDate()));
    }

    /**
     * Sorts tasks by priority in descending order (HIGH to LOW)
     */
    public void sortByPriority() {
        tasks.sort((task1, task2) -> task2.getPriority().compareTo(task1.getPriority()));
    }

    /**
     * Sorts tasks by ID in ascending order
     */
    public void sortById() {
        tasks.sort((task1, task2) -> Integer.compare(task1.getId(), task2.getId()));
    }

    /**
     * Checks if task list is empty
     *
     * @return true if there are no tasks, false otherwise
     */
    public boolean isTaskListEmpty() {
        return tasks.isEmpty();
    }

}