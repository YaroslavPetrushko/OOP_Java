package ua.edu.sumdu;

import java.util.Objects;

/**
 * Представляє електронну книгу — похідний клас від {@link Book}.
 *
 * <p>Розширює базовий клас трьома додатковими характеристиками,
 * що є специфічними для цифрового формату:</p>
 * <ul>
 *   <li>{@code fileFormat}   — формат файлу (напр. "PDF", "EPUB", "MOBI")</li>
 *   <li>{@code fileSizeMB}   — розмір файлу в мегабайтах (&gt; 0)</li>
 *   <li>{@code downloadUrl}  — URL для завантаження; не може бути порожнім</li>
 * </ul>
 *
 * <p>Перевизначає {@link #toString()} для демонстрації поліморфізму:
 * при обробці через посилання типу {@code Book} виводиться інформація
 * саме електронної книги.</p>
 */
public class EBook extends Book {

    /** Мінімально допустимий розмір файлу (у мегабайтах). */
    private static final double MIN_FILE_SIZE = 0.01;

    private String fileFormat;
    private double fileSizeMB;
    private String downloadUrl;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code EBook} із перевіркою всіх параметрів,
     * включаючи поля базового класу.
     *
     * @param title       назва книги
     * @param author      ім'я автора
     * @param year        рік видання
     * @param price       ціна (може бути 0 для безкоштовних)
     * @param genre       жанр ({@link Genre})
     * @param pages       кількість сторінок
     * @param fileFormat  формат файлу; не може бути {@code null} або порожнім
     * @param fileSizeMB  розмір файлу в МБ; має бути &gt; 0
     * @param downloadUrl URL завантаження; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо будь-який параметр некоректний
     */
    public EBook(String title, String author, int year, double price,
                 Genre genre, int pages,
                 String fileFormat, double fileSizeMB, String downloadUrl) {
        super(title, author, year, price, genre, pages);
        setFileFormat(fileFormat);
        setFileSizeMB(fileSizeMB);
        setDownloadUrl(downloadUrl);
    }

    /**
     * Конструктор копіювання — створює незалежну копію об'єкта {@code EBook}.
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public EBook(EBook other) {
        super(other);
        this.fileFormat   = other.fileFormat;
        this.fileSizeMB   = other.fileSizeMB;
        this.downloadUrl  = other.downloadUrl;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає формат файлу електронної книги.
     *
     * @return формат файлу (напр. "EPUB")
     */
    public String getFileFormat() { return fileFormat; }

    /**
     * Встановлює формат файлу.
     *
     * @param fileFormat формат; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setFileFormat(String fileFormat) {
        if (fileFormat == null || fileFormat.isBlank()) {
            throw new InvalidBookDataException("File format cannot be empty.");
        }
        this.fileFormat = fileFormat.trim().toUpperCase();
    }

    /**
     * Повертає розмір файлу в мегабайтах.
     *
     * @return розмір файлу (МБ)
     */
    public double getFileSizeMB() { return fileSizeMB; }

    /**
     * Встановлює розмір файлу в мегабайтах.
     *
     * @param fileSizeMB розмір; має бути &gt; 0
     * @throws InvalidBookDataException якщо значення ≤ 0
     */
    public void setFileSizeMB(double fileSizeMB) {
        if (fileSizeMB < MIN_FILE_SIZE) {
            throw new InvalidBookDataException(
                    "File size must be at least " + MIN_FILE_SIZE + " MB.");
        }
        this.fileSizeMB = fileSizeMB;
    }

    /**
     * Повертає URL для завантаження книги.
     *
     * @return URL завантаження
     */
    public String getDownloadUrl() { return downloadUrl; }

    /**
     * Встановлює URL для завантаження книги.
     *
     * @param downloadUrl URL; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setDownloadUrl(String downloadUrl) {
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new InvalidBookDataException("Download URL cannot be empty.");
        }
        this.downloadUrl = downloadUrl.trim();
    }

    // ---------------------------------------------------------------
    // Object overrides (поліморфізм)
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення електронної книги.
     *
     * <p>Перевизначає {@link Book#toString()} — при виклику через посилання
     * базового типу {@code Book} буде виконано саме цей метод (поліморфізм).</p>
     *
     * @return форматований рядок з усіма полями
     */
    @Override
    public String toString() {
        return String.format(
                "[EBook] \"%s\" by %s | %d | $%.2f | %s | %d pages" +
                        " | %s | %.1f MB | %s",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                fileFormat, fileSizeMB, downloadUrl);
    }

    /**
     * Порівнює два об'єкти {@code EBook} за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо всі поля рівні
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        EBook eBook = (EBook) o;
        return Double.compare(fileSizeMB, eBook.fileSizeMB) == 0
                && Objects.equals(fileFormat,  eBook.fileFormat)
                && Objects.equals(downloadUrl, eBook.downloadUrl);
    }

    /**
     * Повертає хеш-код з урахуванням полів підкласу.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileFormat, fileSizeMB, downloadUrl);
    }
}