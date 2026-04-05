package ua.edu.sumdu;

import java.util.Objects;

public class PaperBook extends Book {

    private String publisher;
    private int    edition;
    private double weightGrams;

    // ---------------------------------------------------------------
    // Конструктори
    // ---------------------------------------------------------------

    // Основний конструктор
    public PaperBook(String title, String author, int year, double price,
                     Genre genre, int pages,
                     String publisher, int edition, double weightGrams) {
        super(title, author, year, price, genre, pages);
        setPublisher(publisher);
        setEdition(edition);
        setWeightGrams(weightGrams);
    }

    // Конструктор копіювання
    public PaperBook(PaperBook other) {
        super(other);
        this.publisher   = other.publisher;
        this.edition     = other.edition;
        this.weightGrams = other.weightGrams;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) {
        if (publisher == null || publisher.isBlank()) {
            throw new InvalidBookDataException("Publisher cannot be empty.");
        }
        this.publisher = publisher.trim();
    }

    public int getEdition() { return edition; }

    public void setEdition(int edition) {
        if (edition <= 0) {
            throw new InvalidBookDataException("Edition must be greater than zero.");
        }
        this.edition = edition;
    }

    public double getWeightGrams() { return weightGrams; }

    public void setWeightGrams(double weightGrams) {
        if (weightGrams <= 0) {
            throw new InvalidBookDataException("Weight must be greater than zero.");
        }
        this.weightGrams = weightGrams;
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
                "[PaperBook] \"%s\" by %s | %d | $%.2f | %s | %d pages" +
                        " | %s | ed.%d | %.0f g",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                publisher, edition, weightGrams);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        PaperBook pb = (PaperBook) o;
        return edition == pb.edition
                && Double.compare(weightGrams, pb.weightGrams) == 0
                && Objects.equals(publisher, pb.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), publisher, edition, weightGrams);
    }
}
