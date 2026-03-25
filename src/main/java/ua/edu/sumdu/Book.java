package ua.edu.sumdu;

import java.util.Objects;
import java.time.Year;

/**
 * Представляє книгу з основними бібліографічними характеристиками.
 *
 * <p>Клас забезпечує валідацію всіх полів як у конструкторі,
 * так і в сетерах. При некоректних даних викидається
 * {@link InvalidBookDataException}.</p>
 *
 * <p>Поля класу:</p>
 * <ul>
 *   <li>{@code title}  — назва книги (не порожня)</li>
 *   <li>{@code author} — автор книги (не порожній)</li>
 *   <li>{@code year}   — рік видання (від 1 до поточного року включно)</li>
 *   <li>{@code price}  — ціна книги (≥ 0)</li>
 *   <li>{@code genre}  — жанр книги (не порожній)</li>
 *   <li>{@code pages}  — кількість сторінок (> 0)</li>
 * </ul>
 */
public class Book {

    /** Мінімально допустимий рік видання. */
    private static final int MIN_YEAR = 1;

    private String title;
    private String author;
    private int year;
    private double price;
    private String genre;
    private int pages;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code Book} із повною перевіркою всіх параметрів.
     *
     * @param title  назва книги; не може бути {@code null} або порожнім рядком
     * @param author ім'я автора; не може бути {@code null} або порожнім рядком
     * @param year   рік видання; допустимий діапазон [1, поточний рік]
     * @param price  ціна книги; не може бути від'ємною
     * @param genre  жанр книги; не може бути {@code null} або порожнім рядком
     * @param pages  кількість сторінок; має бути більше нуля
     * @throws InvalidBookDataException якщо будь-який із параметрів некоректний
     */
    public Book(String title, String author, int year, double price, String genre, int pages) {
        // Використовуємо сетери, щоб не дублювати логіку валідації
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPrice(price);
        setGenre(genre);
        setPages(pages);
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
    public String getGenre() { return genre; }

    /**
     * Встановлює жанр книги.
     *
     * @param genre жанр книги; не може бути {@code null} або порожнім рядком
     * @throws InvalidBookDataException якщо {@code genre} порожній або {@code null}
     */
    public void setGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            throw new InvalidBookDataException("Genre cannot be empty.");
        }
        this.genre = genre.trim();
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
