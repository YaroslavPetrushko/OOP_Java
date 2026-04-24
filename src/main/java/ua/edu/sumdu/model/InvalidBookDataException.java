package ua.edu.sumdu.model;
/**
 * Виняток, що викидається при передачі некоректних даних у клас {@link Book}.
 * <p>
 * Використовується в конструкторі та сетерах для сигналізації про
 * порушення бізнес-правил (порожні рядки, від'ємні числа, недопустимі роки тощо).
 * </p>
 */
public class InvalidBookDataException extends RuntimeException {

    /**
     * Створює виняток із зазначеним повідомленням про помилку.
     *
     * @param message текст, що пояснює причину винятку
     */
    public InvalidBookDataException(String message) {
        super(message);
    }
}