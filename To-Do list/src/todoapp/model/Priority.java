package todoapp.model;

import java.io.Serializable;

/**
 * Task priority levels for importance categorization
 * Serializable for task persistence in file storage
 * Order: LOW → MEDIUM → HIGH (increasing importance)
 */
public enum Priority implements Serializable {
    LOW,
    MEDIUM,
    HIGH
}
