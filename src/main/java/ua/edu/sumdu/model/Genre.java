package ua.edu.sumdu.model;

/**
 * Перелік літературних жанрів книги.
 */
public enum Genre {

    /** Художня проза. */
    FICTION("Fiction"),

    /** Документальна або науково-популярна проза. */
    NON_FICTION("Non-Fiction"),

    /** Комп'ютерні технології та програмування. */
    PROGRAMMING("Programming"),

    /** Природничі та точні науки. */
    SCIENCE("Science"),

    /** Жанр фентезі. */
    FANTASY("Fantasy"),

    /** Детектив та трилер. */
    MYSTERY("Mystery"),

    /** Біографія або автобіографія. */
    BIOGRAPHY("Biography"),

    /** Наукова фантастика. */
    SCI_FI("Sci-Fi");

    // ---------------------------------------------------------------

    private final String displayName;

    Genre(String displayName) {
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