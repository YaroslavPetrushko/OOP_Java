package ua.edu.sumdu.model;

/**
 * Перелік можливих станів фізичного примірника книги.
 *
 * <p>Використовується в класі {@link RareBook} для опису
 * збереженості колекційного видання.</p>
 *
 * <p>Константи впорядковані від найкращого стану до найгіршого.</p>
 */
public enum BookCondition {

    /** Ідеальний стан — без жодних слідів використання. */
    MINT("Mint"),

    /** Відмінний стан — мінімальні сліди зберігання. */
    FINE("Fine"),

    /** Хороший стан — незначні ознаки використання. */
    GOOD("Good"),

    /** Задовільний стан — помітні ознаки зносу, але цілий. */
    FAIR("Fair"),

    /** Поганий стан — суттєві пошкодження, але читабельний. */
    POOR("Poor");

    // ---------------------------------------------------------------

    /** Назва для відображення у меню та виводі. */
    private final String displayName;

    /**
     * Ініціалізує константу з людиночитаною назвою.
     *
     * @param displayName назва стану для виведення
     */
    BookCondition(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Повертає зручну для відображення назву стану.
     *
     * @return рядкова назва
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Повертає рядкове представлення константи ({@code displayName}).
     *
     * @return displayName
     */
    @Override
    public String toString() {
        return displayName;
    }
}