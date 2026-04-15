package ua.edu.sumdu;

import java.time.Year;
import java.util.Objects;

/**
 * Представляє рідкісну або колекційну книгу — похідний клас від PaperBook.
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

    public RareBook(String title, String author, int year, double price,
                    Genre genre, int pages,
                    String publisher, int edition, double weightGrams,
                    BookCondition condition, double estimatedValueUSD, int acquisitionYear) {
        super(title, author, year, price, genre, pages, publisher, edition, weightGrams);
        setCondition(condition);
        setEstimatedValueUSD(estimatedValueUSD);
        setAcquisitionYear(acquisitionYear);
    }

    //Конструктор копіювання — створює незалежну копію {@code RareBook}.
    public RareBook(RareBook other) {
        super(other);
        this.condition          = other.condition;
        this.estimatedValueUSD  = other.estimatedValueUSD;
        this.acquisitionYear    = other.acquisitionYear;
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    public BookCondition getCondition() { return condition; }

    public void setCondition(BookCondition condition) {
        if (condition == null) {
            throw new InvalidBookDataException("Condition cannot be null.");
        }
        this.condition = condition;
    }

    public double getEstimatedValueUSD() { return estimatedValueUSD; }

    public void setEstimatedValueUSD(double estimatedValueUSD) {
        if (estimatedValueUSD < MIN_VALUE) {
            throw new InvalidBookDataException(
                    "Estimated value must be at least $" + MIN_VALUE + ".");
        }
        this.estimatedValueUSD = estimatedValueUSD;
    }

    public int getAcquisitionYear() { return acquisitionYear; }

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
    // Object overrides
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
                "[RareBook] \"%s\" by %s | %d | $%.2f | %s | %d pages"
                        + " | %s | ed.%d | %.0f g | %s | est. $%.2f | acquired %d",
                getTitle(), getAuthor(), getYear(), getPrice(), getGenre(), getPages(),
                getPublisher(), getEdition(), getWeightGrams(),
                condition, estimatedValueUSD, acquisitionYear);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        RareBook rb = (RareBook) o;
        return acquisitionYear == rb.acquisitionYear
                && Double.compare(estimatedValueUSD, rb.estimatedValueUSD) == 0
                && condition == rb.condition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), condition, estimatedValueUSD, acquisitionYear);
    }
}