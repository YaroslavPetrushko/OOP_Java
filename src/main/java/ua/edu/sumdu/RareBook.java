package ua.edu.sumdu;

import java.time.Year;
import java.util.Objects;

/**
 * Представляє рідкісну або колекційну книгу — похідний клас від {@link PaperBook}.
 *
 * <p>Розширює {@link PaperBook} трьома атрибутами, що описують
 * колекційну цінність фізичного примірника:</p>
 * <ul>
 *   <li>{@code condition}        — стан примірника ({@link BookCondition})</li>
 *   <li>{@code estimatedValueUSD}— оціночна вартість у доларах США (&gt; 0)</li>
 *   <li>{@code acquisitionYear}  — рік придбання примірника [1, поточний рік]</li>
 * </ul>
 *
 * <p>Перевизначає {@link #toString()} для відображення повної
 * інформації про рідкісне видання.</p>
 */
public class RareBook extends PaperBook {

    /** Мінімально допустима оціночна вартість. */
    private static final double MIN_VALUE = 0.01;

    /** Мінімально допустимий рік придбання. */
    private static final int MIN_ACQUISITION_YEAR = 1;

    private BookCondition   condition;
    private double          estimatedValueUSD;
    private int             acquisitionYear;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code RareBook} із перевіркою всіх параметрів,
     * включаючи поля батьківських класів.
     *
     * @param title             назва книги
     * @param author            ім'я автора
     * @param year              рік видання
     * @param price             ціна
     * @param genre             жанр ({@link Genre})
     * @param pages             кількість сторінок
     * @param publisher         видавництво
     * @param edition           номер видання
     * @param weightGrams       вага в грамах
     * @param condition         стан примірника ({@link BookCondition}); не {@code null}
     * @param estimatedValueUSD оціночна вартість (&gt; 0)
     * @param acquisitionYear   рік придбання [1, поточний рік]
     * @throws InvalidBookDataException якщо будь-який параметр некоректний
     */
    public RareBook(String title, String author, int year, double price,
                    Genre genre, int pages,
                    String publisher, int edition, double weightGrams,
                    BookCondition condition, double estimatedValueUSD, int acquisitionYear) {
        super(title, author, year, price, genre, pages, publisher, edition, weightGrams);
        setCondition(condition);
        setEstimatedValueUSD(estimatedValueUSD);
        setAcquisitionYear(acquisitionYear);
    }

    /**
     * Конструктор копіювання — створює незалежну копію {@code RareBook}.
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public RareBook(RareBook other) {
        super(other);
        this.condition          = other.condition;
        this.estimatedValueUSD  = other.estimatedValueUSD;
        this.acquisitionYear    = other.acquisitionYear;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає стан примірника.
     *
     * @return стан ({@link BookCondition})
     */
    public BookCondition getCondition() { return condition; }

    /**
     * Встановлює стан примірника.
     *
     * @param condition стан; не може бути {@code null}
     * @throws InvalidBookDataException якщо значення {@code null}
     */
    public void setCondition(BookCondition condition) {
        if (condition == null) {
            throw new InvalidBookDataException("Condition cannot be null.");
        }
        this.condition = condition;
    }

    /**
     * Повертає оціночну вартість у доларах США.
     *
     * @return оціночна вартість
     */
    public double getEstimatedValueUSD() { return estimatedValueUSD; }

    /**
     * Встановлює оціночну вартість у доларах США.
     *
     * @param estimatedValueUSD вартість; має бути &gt; 0
     * @throws InvalidBookDataException якщо значення ≤ 0
     */
    public void setEstimatedValueUSD(double estimatedValueUSD) {
        if (estimatedValueUSD < MIN_VALUE) {
            throw new InvalidBookDataException(
                    "Estimated value must be at least $" + MIN_VALUE + ".");
        }
        this.estimatedValueUSD = estimatedValueUSD;
    }

    /**
     * Повертає рік придбання примірника.
     *
     * @return рік придбання
     */
    public int getAcquisitionYear() { return acquisitionYear; }

    /**
     * Встановлює рік придбання примірника.
     *
     * @param acquisitionYear рік; діапазон [1, поточний рік]
     * @throws InvalidBookDataException якщо значення поза діапазоном
     */
    public void setAcquisitionYear(int acquisitionYear) {
        int currentYear = Year.now().getValue();
        if (acquisitionYear < MIN_ACQUISITION_YEAR || acquisitionYear > currentYear) {
            throw new InvalidBookDataException(
                    "Acquisition year must be between "
                            + MIN_ACQUISITION_YEAR + " and " + currentYear + ".");
        }
        this.acquisitionYear = acquisitionYear;
    }

    // ---------------------------------------------------------------
    // Object overrides (polymorphism)
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення рідкісної книги.
     *
     * <p>Перевизначає {@link PaperBook#toString()} — при обробці через
     * посилання типу {@code Book} викликається саме цей метод.</p>
     *
     * @return форматований рядок з усіма полями
     */
    @Override
    public String toString() {
        return String.format(
                "[RareBook] \"%s\" by %s | %d | $%.2f | %s | %d pages"
                        + " | %s | ed.%d | %.0f g | %s | est. $%.2f | acquired %d",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                getPublisher(), getEdition(), getWeightGrams(),
                condition, estimatedValueUSD, acquisitionYear);
    }

    /**
     * Порівнює два об'єкти {@code RareBook} за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо всі поля рівні
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        RareBook rb = (RareBook) o;
        return acquisitionYear == rb.acquisitionYear
                && Double.compare(estimatedValueUSD, rb.estimatedValueUSD) == 0
                && condition == rb.condition;
    }

    /**
     * Повертає хеш-код з урахуванням полів підкласу.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), condition, estimatedValueUSD, acquisitionYear);
    }
}