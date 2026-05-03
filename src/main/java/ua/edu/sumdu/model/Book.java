package ua.edu.sumdu.model;

import java.util.Objects;
import java.util.UUID;
import java.time.Year;

/**
 * Абстрактний базовий клас, що представляє книгу з основними бібліографічними
 * характеристиками.
 *
 * <p>Є спільним батьківським типом для {@link EBook}, {@link AudioBook}
 * та {@link PaperBook}. Оголошено {@code abstract} — безпосереднє
 * створення екземплярів забороняється; слід використовувати конкретні підкласи.</p>
 *
 * <p>Реалізує {@link Comparable}{@code <Book>}: природний порядок —
 * лексикографічний за полем {@code title} (без урахування регістру).
 * Це дозволяє сортувати будь-яку колекцію об'єктів ієрархії через
 * {@code Collections.sort()} без додаткових компараторів.</p>
 *
 * <p>Поля:</p>
 * <ul>
 *   <li>{@code title}  — назва книги</li>
 *   <li>{@code author} — ім'я автора</li>
 *   <li>{@code year}   — рік видання [1, поточний рік]</li>
 *   <li>{@code price}  — ціна (≥ 0)</li>
 *   <li>{@code genre}  — жанр ({@link Genre})</li>
 *   <li>{@code pages}  — кількість сторінок (&gt; 0)</li>
 * </ul>
 */
public abstract class Book implements Comparable<Book>, Identifiable {

    // ---------------------------------------------------------------
    // Константи
    // ---------------------------------------------------------------

    /** Мінімально допустимий рік видання. */
    private static final int MIN_YEAR = 1;

    // ---------------------------------------------------------------
    // Поля екземпляра
    // ---------------------------------------------------------------

    // Унікальний ідентифікатор об'єкта
    private String uuid;

    private String title;
    private String author;
    private int    year;
    private double price;
    private Genre  genre;
    private int    pages;

    // ---------------------------------------------------------------
    // Основний конструктор
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code Book} із повною перевіркою всіх параметрів.
     * Викликається з конструкторів конкретних підкласів через {@code super(...)}.
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
        // UUID генерується автоматично
        this.uuid = UUID.randomUUID().toString();

        // Використовуємо сетери, щоб не дублювати логіку валідації
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPrice(price);
        setGenre(genre);
        setPages(pages);
    }

    // ---------------------------------------------------------------
    // Конструктор копіювання
    // ---------------------------------------------------------------

    /**
     * Конструктор копіювання — створює незалежну копію переданого об'єкта.
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public Book(Book other) {
        if (other == null) {
            throw new InvalidBookDataException("Source book for copying cannot be null.");
        }
        this.uuid   = UUID.randomUUID().toString(); // копія отримує свій UUID
        this.title  = other.title;
        this.author = other.author;
        this.year   = other.year;
        this.price  = other.price;
        this.genre  = other.genre;
        this.pages  = other.pages;
    }

    // ---------------------------------------------------------------
    // Identifiable
    // ---------------------------------------------------------------

    @Override
    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return UUID.fromString(uuid);
    }

    public void setUuid(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
            return;
        }
        try {
            UUID.fromString(uuidString.trim()); // validate
            this.uuid = uuidString.trim();
        } catch (IllegalArgumentException e) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

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

    protected String uuidSuffix() {
        return " | id:" + getUuid().toString().substring(0, 8);
    }

    // ---------------------------------------------------------------
    // Comparable
    // ---------------------------------------------------------------
    /**
     * Порівнює цю книгу з іншою за назвою ({@code title})
     * в лексикографічному порядку без урахування регістру.
     *
     * <p>Критерій є стабільним: поле {@code title} присутнє в кожному
     * об'єкті ієрархії та не може бути порожнім (перевіряється в сетері).</p>
     *
     * @param other інша книга для порівняння
     * @return від'ємне число, нуль або додатне число, якщо ця книга
     *         лексикографічно менша, рівна або більша за {@code other}
     */
    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    /**
     * Повертає форматований рядок із полями базового класу.
     * Перевизначається у кожному конкретному підкласі.
     *
     * @return рядкове представлення книги
     */
    @Override
    public String toString() {
        return String.format(
                "[Book] \"%s\" by %s | %d | $%.2f | %s | %d pages%s",
                title, author, year, price, genre, pages, uuidSuffix());
    }

    /**
     * Порівнює два об'єкти за всіма полями базового класу.
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