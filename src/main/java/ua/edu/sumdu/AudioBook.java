package ua.edu.sumdu;

import java.util.Objects;

/**
 * Представляє аудіокнигу — похідний клас від Book.
 */
public class AudioBook extends Book {

    private String  narrator;
    private int     durationMinutes;
    private String  audioFormat;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    public AudioBook(String title, String author, int year, double price,
                     Genre genre, int pages,
                     String narrator, int durationMinutes, String audioFormat) {
        super(title, author, year, price, genre, pages);
        setNarrator(narrator);
        setDurationMinutes(durationMinutes);
        setAudioFormat(audioFormat);
    }


    //Конструктор копіювання
    public AudioBook(AudioBook other) {
        super(other);
        this.narrator        = other.narrator;
        this.durationMinutes = other.durationMinutes;
        this.audioFormat     = other.audioFormat;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    public String getNarrator() { return narrator; }

    public void setNarrator(String narrator) {
        if (narrator == null || narrator.isBlank()) {
            throw new InvalidBookDataException("Narrator cannot be empty.");
        }
        this.narrator = narrator.trim();
    }

    public int getDurationMinutes() { return durationMinutes; }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new InvalidBookDataException("Duration must be greater than zero.");
        }
        this.durationMinutes = durationMinutes;
    }

    public String getAudioFormat() { return audioFormat; }

    public void setAudioFormat(String audioFormat) {
        if (audioFormat == null || audioFormat.isBlank()) {
            throw new InvalidBookDataException("Audio format cannot be empty.");
        }
        this.audioFormat = audioFormat.trim().toUpperCase();
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

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

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        AudioBook ab = (AudioBook) o;
        return durationMinutes == ab.durationMinutes
                && Objects.equals(narrator, ab.narrator)
                && Objects.equals(audioFormat, ab.audioFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), narrator, durationMinutes, audioFormat);
    }
}