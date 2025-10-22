package todoapp;

import todoapp.service.TaskManager;
import todoapp.service.TaskFilter;
import todoapp.model.Task;
import todoapp.model.Priority;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class Main {
    /**
     * Reads and validates task ID input with 'back' cancellation support
     * Ensures ID is positive integer and exists in the task list
     */
    private static int readId() {
        while (true) {
            String idInput = scanner.nextLine().trim();

            if (idInput.equals("back")) {
                throw new CancellationException();
            }

            if (idInput.isEmpty()) {
                System.out.println("!ID cannot be empty! Please enter a number");
                continue;
            }

            try {
                int id = Integer.parseInt(idInput);

                if (id <= 0) {
                    System.out.println("!ID must be positive number!");
                    continue;
                }

                if (manager.findById(id) == null) {
                    System.out.println("!Task with ID " + id + " not found!");
                    continue;
                }

                return id;
            } catch (NumberFormatException e) {
                System.out.println("!Wrong ID format! Please enter a number");
            }
        }
    }

    /**
     * Reads non-empty title with 'back' cancellation support
     * Ensures task has a valid title
     */
    private static String readTitle() {
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equals("back")) {
                throw new CancellationException();
            }

            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("!Task title cannot be empty! Please enter a title");
            }
        }
    }

    /**
     * Reads and validates date input with 'back' cancellation support
     * Ensures date is not in past and follows dd.MM.yyyy format
     */
    private static LocalDate readDate() {
        while (true) {
            String dateInput = scanner.nextLine();

            if (dateInput.equals("back")) {
                throw new CancellationException();
            }

            try {
                DateTimeFormatter dateFormatted = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(dateInput, dateFormatted);

                if (date.isBefore(LocalDate.now())) {
                    System.out.println("!Date can not be in the past!");
                    continue;
                }

                return date;
            } catch (Exception e) {
                System.out.println("!Wrong date format! Use dd.MM.yyyy!");
            }
        }
    }

    /**
     * Reads and validates priority input with 'back' cancellation
     * Converts input to uppercase for enum matching
     */
    private static Priority readPriority() {
        while (true) {
            String priorityInput = scanner.nextLine().toUpperCase();

            if (priorityInput.equals("BACK")) {
                throw new CancellationException();
            }

            try {
                return Priority.valueOf(priorityInput);
            } catch (IllegalArgumentException e) {
                System.out.println("!Wrong priority format! Use HIGH, MEDIUM, LOW");
            }
        }
    }

    private static final TaskManager manager = new TaskManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n======== To-Do List Menu ========");
            System.out.println("0. Exit");
            System.out.println("1. Add a task");
            System.out.println("2. Show tasks");
            System.out.println("3. Find task");
            System.out.println("4. Delete task");
            System.out.println("5. Update in the task");
            System.out.println("6. Sort tasks");
            System.out.println("7. Delete all data");
            System.out.println("Please enter your choice(0-7): ");
            String c = scanner.nextLine().trim(); //To catch if choice is not a number

            try {
                int choice = Integer.parseInt(c);
                label: //Label for breaking out of nested loops

                switch (choice) {
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    case 1 -> {
                        try{
                            //Task creation flow with cancellation at any step
                            System.out.println("Enter task title (or 'back' to cancel): ");
                            String title = readTitle();

                            System.out.println("Enter task description (or 'back' to cancel): ");
                            String description = scanner.nextLine();
                            if (description.equals("back")) {
                                throw new CancellationException();
                            }

                            System.out.println("Enter due date (dd.MM.yyyy) (or 'back' to cancel): ");
                            LocalDate dueDate = readDate();

                            System.out.println("Enter task priority (HIGH, MEDIUM, LOW) (or 'back' to cancel): ");
                            Priority priority = readPriority();

                            Task task = new Task(title, description, dueDate, priority);
                            manager.addTask(task);

                            manager.viewTasks(TaskFilter.ALL);
                        } catch (CancellationException e) {
                            System.out.println("Task creation cancelled");
                        }
                    }
                    case 2 -> {
                        //Display tasks with filtering options

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No tasks to show");
                            break;
                        }

                        System.out.println("Show tasks (all, completed, incompleted, overdue): ");
                        System.out.println("Use 'back' to cancel"); //Supports back cancellation

                        while(true) {
                            String input = scanner.nextLine().toLowerCase();

                            switch (input) {
                                case "back":
                                    break label; //Break to main menu using label
                                case "all":
                                    manager.viewTasks(TaskFilter.ALL);
                                    break;
                                case "completed":
                                    manager.viewTasks(TaskFilter.COMPLETE);
                                    break;
                                case "incompleted":
                                    manager.viewTasks(TaskFilter.INCOMPLETE);
                                    break;
                                case "overdue":
                                    manager.viewTasks(TaskFilter.OVERDUE);
                                    break;
                                default:
                                    System.out.println("!Wrong input! Use 'all, completed, incompleted, overdue'");
                                    continue; //Continue inner loop for retry
                            }
                            break;
                        }
                    }
                    case 3 -> {
                        //Search functionality with multiple search criteria
                        //Supports searching for an empty or partially matched description
                        //And partially matched title

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No tasks to find");
                            break;
                        }

                        try {
                            System.out.println("Find by (ID, title, description, date, priority): ");
                            System.out.println("Use 'back' to cancel"); //Supports back cancellation

                            while (true) {
                                String input = scanner.nextLine().toLowerCase();

                                switch (input) {
                                    case "back":
                                        break label;
                                    case "id":
                                        System.out.println("Enter ID: ");
                                        int id = readId();
                                        Task task = manager.findById(id);
                                        System.out.println(task != null ? task : "!Task not found!");
                                        break;
                                    case "title":
                                        System.out.println("Enter title: ");
                                        String title = readTitle();
                                        List<Task> byTitle = manager.findByTitle(title);

                                        if (byTitle.isEmpty()) {
                                            System.out.println("!No tasks found!");
                                        } else {
                                            byTitle.forEach(System.out::println);
                                        }
                                        break;
                                    case "description":
                                        System.out.println("Enter description: ");
                                        String description = scanner.nextLine();
                                        List<Task> byDesc = manager.findByDescription(description);

                                        if (byDesc.isEmpty()) {
                                            System.out.println("!No tasks found!");
                                        } else {
                                            byDesc.forEach(System.out::println);
                                        }
                                        break;
                                    case "date":
                                        System.out.println("Enter date: ");
                                        LocalDate date = readDate();
                                        List<Task> byDate = manager.findByDate(date);

                                        if (byDate.isEmpty()) {
                                            System.out.println("!No tasks found!");
                                        } else {
                                            byDate.forEach(System.out::println);
                                        }
                                        break;
                                    case "priority":
                                        System.out.println("Enter priority: ");
                                        Priority priority = readPriority();
                                        List<Task> byPrior = manager.findByPriority(priority);

                                        if (byPrior.isEmpty()) {
                                            System.out.println("!No tasks found!");
                                        } else {
                                            byPrior.forEach(System.out::println);
                                        }
                                        break;
                                    default:
                                        System.out.println("!Wrong input! Use 'ID, title, description, date, priority'");
                                        continue;
                                }
                                break;
                            }
                        } catch (CancellationException e) {
                            System.out.println("Search cancelled");
                        }
                    }
                    case 4 -> {
                        //Delete individual task by ID
                        //Includes empty list check and ID validation

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No tasks to delete");
                            break;
                        }

                        try {
                            System.out.println("Enter ID of the task to delete: ");
                            System.out.println("Use 'back' to cancel"); //Supports back cancellation
                            int id = readId();

                            if (manager.removeTask(id)) {
                                System.out.println("The task removed successfully");
                                if (!manager.isTaskListEmpty()) {
                                    manager.viewTasks(TaskFilter.ALL);
                                }
                            }
                        } catch (CancellationException e) {
                            System.out.println("Deletion cancelled");
                        }
                    }
                    case 5 -> {
                        //Update functionality with multiple search criteria
                        // Two-step process: first select field to update, then enter new value

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No tasks to update");
                            break;
                        }


                        System.out.println("Update in the task (title, description, date, priority, completeness): ");
                        System.out.println("Use 'back' to cancel"); //Supports back cancellation

                        while (true) {
                            String input = scanner.nextLine().toLowerCase();

                            if (input.equals("back")) {
                                break label;
                            }

                            //Validate that user selected a supported field to update
                            if (!input.equals("title") && !input.equals("description") && !input.equals("date") && !input.equals("priority") && !input.equals("completeness")) {
                                System.out.println("!Wrong input! Use 'title, description, date, priority, completeness'");
                                continue; //Retry field selection
                            }

                            try {
                                System.out.println("Enter ID of the task to update (or 'back' to cancel): ");
                                int id = readId();

                                switch (input) {
                                    case "title":
                                        System.out.println("Enter new title: ");
                                        String title = readTitle();

                                        if (manager.updateTitle(id, title)) {
                                            System.out.println("The task updated successfully");
                                        } else {
                                            System.out.println("!The task does not exist!");
                                        }

                                        break;
                                    case "description":
                                        System.out.println("Enter new description: ");
                                        String description = scanner.nextLine();

                                        if (manager.updateDescription(id, description)) {
                                            System.out.println("The task updated successfully");
                                        } else {
                                            System.out.println("!The task does not exist!");
                                        }

                                        break;
                                    case "date":
                                        System.out.println("Enter new date: ");
                                        LocalDate date = readDate();

                                        if (manager.updateDate(id, date)) {
                                            System.out.println("The task updated successfully");
                                        } else {
                                            System.out.println("!The task does not exist!");
                                        }

                                        break;
                                    case "priority":
                                        System.out.println("Enter new priority: ");
                                        Priority priority = readPriority();

                                        if (manager.updatePriority(id, priority)) {
                                            System.out.println("The task updated successfully");
                                        } else {
                                            System.out.println("!The task does not exist!");
                                        }

                                        break;
                                    case "completeness":
                                        System.out.println("Enter new completeness (complete, incomplete): ");
                                        String completeness = scanner.nextLine();

                                        if (completeness.equals("complete") && manager.markCompleted(id)) {
                                            System.out.println("The task updated successfully");
                                        } else if (completeness.equals("incomplete") && manager.markIncompleted(id)) {
                                            System.out.println("The task updated successfully");
                                        } else {
                                            System.out.println("!The task does not exist!");
                                        }

                                        break;
                                }
                                break;
                            } catch (CancellationException e) {
                                System.out.println("Update cancelled");
                            }
                            break;
                        }
                    }
                    case 6 -> {
                        //Sort tasks by different criteria
                        //Sorting options: by ID (creation order), by due date (chronological)
                        //Or by priority (importance level)

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No tasks to sort");
                            break;
                        }

                        System.out.println("Sort by (ID, date, priority): ");
                        System.out.println("Use 'back' to cancel"); //Supports back cancellation

                        while (true) {
                            String input = scanner.nextLine().toLowerCase();

                            switch (input) {
                                case "back":
                                    break label;
                                case "id":
                                    manager.sortById();
                                    break;
                                case "date":
                                    manager.sortByDueDate();
                                    break;
                                case "priority":
                                    manager.sortByPriority();
                                    break;
                                default:
                                    System.out.println("!Wrong input! Use 'ID, date, priority'");
                                    continue;
                            }
                            manager.viewTasks(TaskFilter.ALL);
                            break;
                        }
                    }
                    case 7 -> {
                        // Dangerous operation - permanently deletes ALL tasks and data file
                        // Requires explicit confirmation to prevent accidental data loss

                        //Checks if list of tasks is empty
                        if (manager.isTaskListEmpty()) {
                            System.out.println("!Task list is empty! No data to delete");
                            break;
                        }

                        System.out.print("Delete ALL data? (yes/no): ");

                        if (scanner.nextLine().trim().equals("yes")) {
                            manager.deleteAllData();
                            System.out.println("All data deleted!");
                        } else {
                            System.out.println("Cancelled");
                        }
                    }
                    default -> System.out.println("!Invalid choice! Please enter number (0-7)");
                }
            } catch (NumberFormatException e) {
                System.out.println("!Invalid choice! Please enter a number (0-7), not text!");
            }
        }
    }
}


