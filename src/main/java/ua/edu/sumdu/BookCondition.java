package ua.edu.sumdu;

/**
 * Перелік можливих станів фізичного примірника книги.
 */
public enum BookCondition {

    MINT("Mint"),

    FINE("Fine"),

    GOOD("Good"),

    FAIR("Fair"),

    POOR("Poor");


    private final String displayName;

    BookCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}