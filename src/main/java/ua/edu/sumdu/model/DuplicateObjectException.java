package ua.edu.sumdu.model;

/**
 * Кидається коли об'єкт вже існує в колекції при спробі update.
 */
public class DuplicateObjectException extends RuntimeException {

    /**
     * @param message опис конфлікту дублювання
     */
    public DuplicateObjectException(String message) {
        super(" [Duplicate] "+ message);
    }
}
