package ua.edu.sumdu.model;

import java.util.Objects;

/**
 * Запис у колекції бібліотеки: книга + кількість примірників.

 */
public class BookEntry {

    /** Мінімально допустима кількість примірників. */
    private static final int MIN_QUANTITY = 1;

    /** Книга, яку описує цей запис. */
    private final Book book;

    /** Кількість наявних примірників у бібліотеці. */
    private int quantity;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Створює запис із вказаною книгою та кількістю примірників.
     */
    public BookEntry(Book book, int quantity) {
        if (book == null) {
            throw new InvalidBookDataException("Book in BookEntry cannot be null.");
        }
        setQuantity(quantity);
        this.book = book;
    }

    // ---------------------------------------------------------------
    // Геттери
    // ---------------------------------------------------------------

    public Book getBook() { return book; }

    public int getQuantity() { return quantity; }

    // ---------------------------------------------------------------
    // Сетери та допоміжні методи
    // ---------------------------------------------------------------

    public void setQuantity(int quantity) {
        if (quantity < MIN_QUANTITY) {
            throw new InvalidBookDataException(
                    "Quantity must be at least " + MIN_QUANTITY + ".");
        }
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new InvalidBookDataException("Amount to add must be greater than zero.");
        }
        this.quantity += amount;
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return book.toString() + " | qty: " + quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEntry other = (BookEntry) o;
        return Objects.equals(book, other.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book);
    }
}