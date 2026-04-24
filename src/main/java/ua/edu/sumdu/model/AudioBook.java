package ua.edu.sumdu.model;

import java.util.Objects;

/**
 * Представляє аудіокнигу — похідний клас від {@link Book}.
 *
 * <p>Розширює базовий клас трьома атрибутами, специфічними для аудіоформату:</p>
 * <ul>
 *   <li>{@code narrator}        — ім'я оповідача; не може бути порожнім</li>
 *   <li>{@code durationMinutes} — тривалість запису в хвилинах (&gt; 0)</li>
 *   <li>{@code audioFormat}     — формат аудіофайлу (напр. "MP3", "AAC", "FLAC")</li>
 * </ul>
 *
 * <p>Перевизначає {@link #toString()} для відображення повної
 * інформації про аудіовидання.</p>
 */
public class AudioBook extends Book {

    private String  narrator;
    private int     durationMinutes;
    private String  audioFormat;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    /**
     * Створює об'єкт {@code AudioBook} із перевіркою всіх параметрів,
     * включаючи поля базового класу.
     *
     * @param title           назва книги
     * @param author          ім'я автора
     * @param year            рік видання
     * @param price           ціна
     * @param genre           жанр ({@link Genre})
     * @param pages           кількість сторінок (відповідає оригіналу)
     * @param narrator        ім'я оповідача; не може бути {@code null} або порожнім
     * @param durationMinutes тривалість у хвилинах; має бути &gt; 0
     * @param audioFormat     формат файлу (напр. "MP3"); не може бути порожнім
     * @throws InvalidBookDataException якщо будь-який параметр некоректний
     */
    public AudioBook(String title, String author, int year, double price,
                     Genre genre, int pages,
                     String narrator, int durationMinutes, String audioFormat) {
        super(title, author, year, price, genre, pages);
        setNarrator(narrator);
        setDurationMinutes(durationMinutes);
        setAudioFormat(audioFormat);
    }

    /**
     * Конструктор копіювання — створює незалежну копію {@code AudioBook}.
     *
     * @param other джерело для копіювання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code other} є {@code null}
     */
    public AudioBook(AudioBook other) {
        super(other);
        this.narrator        = other.narrator;
        this.durationMinutes = other.durationMinutes;
        this.audioFormat     = other.audioFormat;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає ім'я оповідача.
     *
     * @return ім'я оповідача
     */
    public String getNarrator() { return narrator; }

    /**
     * Встановлює ім'я оповідача.
     *
     * @param narrator ім'я; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setNarrator(String narrator) {
        if (narrator == null || narrator.isBlank()) {
            throw new InvalidBookDataException("Narrator cannot be empty.");
        }
        this.narrator = narrator.trim();
    }

    /**
     * Повертає тривалість аудіозапису в хвилинах.
     *
     * @return тривалість (хв)
     */
    public int getDurationMinutes() { return durationMinutes; }

    /**
     * Встановлює тривалість аудіозапису в хвилинах.
     *
     * @param durationMinutes тривалість; має бути &gt; 0
     * @throws InvalidBookDataException якщо значення ≤ 0
     */
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new InvalidBookDataException("Duration must be greater than zero.");
        }
        this.durationMinutes = durationMinutes;
    }

    /**
     * Повертає формат аудіофайлу.
     *
     * @return формат (напр. "MP3")
     */
    public String getAudioFormat() { return audioFormat; }

    /**
     * Встановлює формат аудіофайлу.
     *
     * @param audioFormat формат; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setAudioFormat(String audioFormat) {
        if (audioFormat == null || audioFormat.isBlank()) {
            throw new InvalidBookDataException("Audio format cannot be empty.");
        }
        this.audioFormat = audioFormat.trim().toUpperCase();
    }

    // ---------------------------------------------------------------
    // Object overrides (polymorphism)
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення аудіокниги.
     *
     * <p>Перевизначає {@link Book#toString()} — при обробці через
     * посилання типу {@code Book} викликається саме цей метод.</p>
     *
     * @return форматований рядок з усіма полями
     */
    @Override
    public String toString() {
        int hours   = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return String.format(
                "[AudioBook] \"%s\" by %s | %d | $%.2f | %s | %d pages"
                        + " | narrated by %s | %dh %02dm | %s",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                narrator, hours, minutes, audioFormat);
    }

    /**
     * Порівнює два об'єкти {@code AudioBook} за всіма полями.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо всі поля рівні
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        AudioBook ab = (AudioBook) o;
        return durationMinutes == ab.durationMinutes
                && Objects.equals(narrator,    ab.narrator)
                && Objects.equals(audioFormat, ab.audioFormat);
    }

    /**
     * Повертає хеш-код з урахуванням полів підкласу.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), narrator, durationMinutes, audioFormat);
    }
}