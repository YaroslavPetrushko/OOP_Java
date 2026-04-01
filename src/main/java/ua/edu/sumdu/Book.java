package ua.edu.sumdu;

import java.util.Objects;
import java.time.Year;

/**
 * Представляє книгу з основними бібліографічними характеристиками.
 *
 * <p>Клас забезпечує:</p>
 * <ul>
 *   <li>валідацію всіх полів у конструкторі та сетерах;</li>
 *   <li>підрахунок загальної кількості створених екземплярів
 *       через {@link #getInstanceCount()};</li>
 *   <li>конструктор копіювання для незалежного дублювання об'єктів;</li>
 *   <li>перерахування {@link Genre} для полів із фіксованим набором значень.</li>
 * </ul>
 *
 * <p>Поля класу:</p>
 * <ul>
 *   <li>{@code title}  — назва книги (не порожня)</li>
 *   <li>{@code author} — автор книги (не порожній)</li>
 *   <li>{@code year}   — рік видання (від 1 до поточного року включно)</li>
 *   <li>{@code price}  — ціна книги (≥ 0)</li>
 *   <li>{@code genre}  — жанр книги (enum)</li>
 *   <li>{@code pages}  — кількість сторінок (> 0)</li>
 * </ul>
 *
 * <p>При некоректних даних викидається {@link InvalidBookDataException}.</p>
 */
public class Book {

    // ---------------------------------------------------------------
    // Константи
    // ---------------------------------------------------------------

    /** Мінімально допустимий рік видання. */
    private static final int MIN_YEAR = 1;

    // ---------------------------------------------------------------
    // Статичне поле — лічильник екземплярів
    // ---------------------------------------------------------------

    /**
     * Загальна кількість об'єктів {@code Book}, створених за час роботи програми.
     * Збільшується у кожному конструкторі (основному та конструкторі копіювання).
     */
    private static int instanceCount = 0;

    // ---------------------------------------------------------------
    // Поля екземпляра
    // ---------------------------------------------------------------

    private String title;
    private String author;
    private int year;
    private double price;
    private Genre genre;
    private int pages;

// ---------------------------------------------------------------
    // Основний конструктор
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code Book} із повною перевіркою всіх параметрів.
     * Збільшує лічильник {@link #instanceCount} на 1.
     *
     * @param title     назва книги; не може бути {@code null} або порожнім
     * @param author    ім'я автора; не може бути {@code null} або порожнім
     * @param year      рік видання; допустимий діапазон [1, поточний рік]
     * @param price     ціна книги; не може бути від'ємною
     * @param genre     жанр книги ({@link Genre}); не може бути {@code null}
     * @param pages     кількість сторінок; має бути більше нуля
     * @throws InvalidBookDataException якщо будь-який із параметрів некоректний
     */
    public Book(String title, String author, int year, double price, Genre genre, int pages) {
        // Використовуємо сетери, щоб не дублювати логіку валідації
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPrice(price);
        setGenre(genre);
        setPages(pages);
        instanceCount++;
    }

    // ---------------------------------------------------------------
    // Конструктор копіювання
    // ---------------------------------------------------------------

    /**
     * Конструктор копіювання — створює незалежну копію переданого об'єкта.
     *
     * <p>{@code String} є незмінним типом, {@link Genre} — константою enum,
     * тому поверхневого копіювання полів достатньо для
     * незалежності двох об'єктів.</p>
     *
     * <p>Збільшує лічильник {@link #instanceCount} на 1.</p>
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public Book(Book other) {
        if (other == null) {
            throw new InvalidBookDataException("Source book for copying cannot be null.");
        }
        this.title      = other.title;
        this.author     = other.author;
        this.year       = other.year;
        this.price      = other.price;
        this.genre      = other.genre;
        this.pages      = other.pages;
        instanceCount++;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає загальну кількість об'єктів {@code Book},
     * створених за весь час роботи програми.
     *
     * @return кількість створених екземплярів (≥ 0)
     */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /**
     * Повертає назву книги.
     *
     * @return назва книги
     */
    public String getTitle() { return title; }

    /**
     * Встановлює назву книги.
     *
     * @param title назва книги; не може бути {@code null} або порожнім рядком
     * @throws InvalidBookDataException якщо {@code title} порожній або {@code null}
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidBookDataException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    /**
     * Повертає ім'я автора.
     *
     * @return ім'я автора
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Встановлює ім'я автора.
     *
     * @param author ім'я автора; не може бути {@code null} або порожнім рядком
     * @throws InvalidBookDataException якщо {@code author} порожній або {@code null}
     */
    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new InvalidBookDataException("Author cannot be empty.");
        }
        this.author = author.trim();
    }

    /**
     * Повертає рік видання.
     *
     * @return рік видання
     */
    public int getYear() {
        return year;
    }

    /**
     * Встановлює рік видання.
     *
     * @param year рік видання; допустимий діапазон [1, поточний рік]
     * @throws InvalidBookDataException якщо {@code year} поза допустимим діапазоном
     */
    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear) {
            throw new InvalidBookDataException(
                    "Year must be between " + MIN_YEAR + " and " + currentYear + ".");
        }
        this.year = year;
    }

    /**
     * Повертає ціну книги.
     *
     * @return ціна книги
     */
    public double getPrice() {
        return price;
    }

    /**
     * Встановлює ціну книги.
     *
     * @param price ціна книги; не може бути від'ємною
     * @throws InvalidBookDataException якщо {@code price} менше ніж 0
     */
    public void setPrice(double price) {
        if (price < 0) {
            throw new InvalidBookDataException("Price cannot be negative.");
        }
        this.price = price;
    }

    /**
     * Повертає жанр книги.
     *
     * @return жанр книги
     */
    public Genre getGenre() { return genre; }

    /**
     * Встановлює жанр книги.
     *
     * @param genre жанр; не може бути {@code null}
     * @throws InvalidBookDataException якщо значення {@code null}
     */
    public void setGenre(Genre genre) {
        if (genre == null) {
            throw new InvalidBookDataException("Genre cannot be empty.");
        }
        this.genre = genre;
    }

    /**
     * Повертає кількість сторінок.
     *
     * @return кількість сторінок
     */
    public int getPages() { return pages; }

    /**
     * Встановлює кількість сторінок.
     *
     * @param pages кількість сторінок; має бути більше нуля
     * @throws InvalidBookDataException якщо {@code pages} ≤ 0
     */
    public void setPages(int pages) {
        if (pages <= 0) {
            throw new InvalidBookDataException("Pages must be greater than zero.");
        }
        this.pages = pages;
    }


    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення книги.
     *
     * @return форматований рядок із усіма полями об'єкта
     */
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", genre='" + genre + '\'' +
                ", pages=" + pages +
                '}';
    }

    /**
     * Порівнює два об'єкти {@code Book} за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо всі поля рівні
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year  == book.year
                && pages == book.pages
                && Double.compare(price, book.price) == 0
                && Objects.equals(title,  book.title)
                && Objects.equals(author, book.author)
                && Objects.equals(genre,  book.genre);
    }

    /**
     * Повертає хеш-код об'єкта на основі всіх полів.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, price, genre, pages);
    }
}
