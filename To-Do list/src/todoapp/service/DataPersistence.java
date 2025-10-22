package todoapp.service;

import todoapp.model.Task;
import java.io.*;
import java.util.List;

/**
 * Handles persistent storage of tasks using Java serialization
 * Saves and loads task data to/from binary file
 */
public class DataPersistence {
    private static final String DATA_FILE = "tasks.dat"; //Binary file name for storing tasks

    /**
     * Saves task list to file using object serialization
     * Uses try-with-resources for automatic stream cleanup
     * Silently handles errors to prevent application crashes
     */
    public static void saveTasks(List<Task> tasks) {
        //Try-with-resources to automatically close the output stream
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks); //Serialize and write entire task list to file
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage()); //Handle any IO errors during the save operation
        }
    }

    /**
     * Loads task list from serialized file
     * Returns empty list if file doesn't exist or errors occur
     * Suppresses unchecked cast warning for serialized List type
     */
    @SuppressWarnings("unchecked")
    public static List<Task> loadTasks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new java.util.ArrayList<>(); //First run - no data file exists
        }

        //Try-with-resources to automatically close the output stream
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (List<Task>) ois.readObject(); //Deserialize and cast to List<Task>
        } catch (IOException | ClassNotFoundException e) {
            return new java.util.ArrayList<>(); //Return empty list if file read fails or class not found during deserialization
        }
    }

    /**
     * Deletes the data file from storage
     * Used when user chooses to delete all application data
     * Shows warning if file deletion fails unexpectedly
     */
    public static void deleteSavedData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            boolean deleted = file.delete(); //Attempt to delete file
            if (!deleted) {
                System.out.println("Warning! Could not delete data file");
            }
        }
    }
}
