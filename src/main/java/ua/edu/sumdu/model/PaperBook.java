package ua.edu.sumdu.model;

import java.util.Objects;

/**
 * Представляє паперову книгу — похідний клас від {@link Book}.
 *
 * <p>Розширює базовий клас трьома додатковими характеристиками,
 * що є специфічними для фізичного видання:</p>
 * <ul>
 *   <li>{@code publisher}  — видавництво; не може бути порожнім</li>
 *   <li>{@code edition}    — номер видання (&gt; 0)</li>
 *   <li>{@code weightGrams}— вага книги в грамах (&gt; 0)</li>
 * </ul>
 *
 * <p>Перевизначає {@link #toString()} для демонстрації поліморфізму:
 * при обробці через посилання типу {@code Book} виводиться інформація
 * саме паперової книги.</p>
 */
public class PaperBook extends Book {

    private String publisher;
    private int    edition;
    private double weightGrams;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code PaperBook} із перевіркою всіх параметрів,
     * включаючи поля базового класу.
     *
     * @param title       назва книги
     * @param author      ім'я автора
     * @param year        рік видання
     * @param price       ціна
     * @param genre       жанр ({@link Genre})
     * @param pages       кількість сторінок
     * @param publisher   назва видавництва; не може бути {@code null} або порожньою
     * @param edition     номер видання; має бути &gt; 0
     * @param weightGrams вага в грамах; має бути &gt; 0
     * @throws InvalidBookDataException якщо будь-який параметр некоректний
     */
    public PaperBook(String title, String author, int year, double price,
                     Genre genre, int pages,
                     String publisher, int edition, double weightGrams) {
        super(title, author, year, price, genre, pages);
        setPublisher(publisher);
        setEdition(edition);
        setWeightGrams(weightGrams);
    }

    /**
     * Конструктор копіювання — створює незалежну копію об'єкта {@code PaperBook}.
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public PaperBook(PaperBook other) {
        super(other);
        this.publisher   = other.publisher;
        this.edition     = other.edition;
        this.weightGrams = other.weightGrams;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає назву видавництва.
     *
     * @return видавництво
     */
    public String getPublisher() { return publisher; }

    /**
     * Встановлює назву видавництва.
     *
     * @param publisher видавництво; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setPublisher(String publisher) {
        if (publisher == null || publisher.isBlank()) {
            throw new InvalidBookDataException("Publisher cannot be empty.");
        }
        this.publisher = publisher.trim();
    }

    /**
     * Повертає номер видання.
     *
     * @return номер видання
     */
    public int getEdition() { return edition; }

    /**
     * Встановлює номер видання.
     *
     * @param edition номер видання; має бути &gt; 0
     * @throws InvalidBookDataException якщо значення ≤ 0
     */
    public void setEdition(int edition) {
        if (edition <= 0) {
            throw new InvalidBookDataException("Edition must be greater than zero.");
        }
        this.edition = edition;
    }

    /**
     * Повертає вагу книги в грамах.
     *
     * @return вага (г)
     */
    public double getWeightGrams() { return weightGrams; }

    /**
     * Встановлює вагу книги в грамах.
     *
     * @param weightGrams вага; має бути &gt; 0
     * @throws InvalidBookDataException якщо значення ≤ 0
     */
    public void setWeightGrams(double weightGrams) {
        if (weightGrams <= 0) {
            throw new InvalidBookDataException("Weight must be greater than zero.");
        }
        this.weightGrams = weightGrams;
    }

    // ---------------------------------------------------------------
    // Object overrides (поліморфізм)
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення паперової книги.
     *
     * <p>Перевизначає {@link Book#toString()} — при виклику через посилання
     * базового типу {@code Book} буде виконано саме цей метод (поліморфізм).</p>
     *
     * @return форматований рядок з усіма полями
     */
    @Override
    public String toString() {
        return String.format(
                "[PaperBook] \"%s\" by %s | %d | $%.2f | %s | %d pages" +
                        " | %s | ed.%d | %.0f g",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                publisher, edition, weightGrams);
    }

    /**
     * Порівнює два об'єкти {@code PaperBook} за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо всі поля рівні
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        PaperBook pb = (PaperBook) o;
        return edition == pb.edition
                && Double.compare(weightGrams, pb.weightGrams) == 0
                && Objects.equals(publisher, pb.publisher);
    }

    /**
     * Повертає хеш-код з урахуванням полів підкласу.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), publisher, edition, weightGrams);
    }
}