package ua.edu.sumdu.model;

/**
 * Кидається коли об'єкт не знайдено в колекції при спробі update або delete.
 */
public class ObjectNotFoundException extends RuntimeException {

    /**
     * @param message опис об'єкта, якого не вдалось знайти
     */
    public ObjectNotFoundException(String message) {
        super(" [ObjNotFound] "+message);
    }
}