package ua.edu.sumdu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Автотести для класів {@link Book} та {@link Library}.
 *
 * <p>Покривають:</p>
 * <ul>
 *   <li>основний конструктор ({@link Book#Book(String, String, int, double, Genre, int)})</li>
 *   <li>конструктор копіювання ({@link Book#Book(Book)})</li>
 *   <li>статичний лічильник ({@link Book#getInstanceCount()})</li>
 *   <li>валідацію сетерів;</li>
 *   <li>агрегацію через {@link Library}.</li>
 * </ul>
 */
class BookTest {

    /** Коректний об'єкт, що переініціалізується перед кожним тестом. */
    private Book validBook;

    /**
     * Ініціалізує валідний об'єкт {@link Book} перед кожним тестом.
     */
    @BeforeEach
    void setUp() {
        validBook = new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431);
    }

    // ---------------------------------------------------------------
    // Основний конструктор
    // ---------------------------------------------------------------

    /**
     * Конструктор повинен коректно зберігати всі передані значення.
     */
    @Test
    void constructor_validData_storesAllFields() {
        assertEquals("Clean Code",          validBook.getTitle());
        assertEquals("Robert C. Martin",    validBook.getAuthor());
        assertEquals(2008,                  validBook.getYear());
        assertEquals(39.99, validBook.getPrice(), 0.001);
        assertEquals(Genre.PROGRAMMING,     validBook.getGenre());
        assertEquals(431,                   validBook.getPages());
    }

    /**
     * Порожня назва у конструкторі — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_emptyTitle_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("", "Author", 2020, 10.0,
                        Genre.FICTION, 200));
    }

    /**
     * {@code null}-автор у конструкторі — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_nullAuthor_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", null, 2020, 10.0,
                        Genre.FICTION, 200));
    }

    /**
     * Від'ємна ціна у конструкторі — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_negativePrice_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2020, -1.0,
                        Genre.FICTION, 200));
    }

    /**
     * Нульова кількість сторінок — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_zeroPagesCount_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2020, 10.0,
                        Genre.FICTION, 0));
    }

    /**
     * Майбутній рік (+100 років) — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_futureYear_throwsException() {
        int futureYear = java.time.Year.now().getValue() + 100;
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", futureYear, 10.0,
                        Genre.SCI_FI, 100));
    }

    /**
     * {@code null} жанр у конструкторі — {@link InvalidBookDataException}.
     */
    @Test
    void constructor_nullGenre_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2020, 10.0,
                        null, 200));
    }

    // ---------------------------------------------------------------
    // Конструктор копіювання
    // ---------------------------------------------------------------

    /**
     * Копія повинна мати рівні значення всіх полів оригіналу.
     */
    @Test
    void copyConstructor_equalFields_afterCopy() {
        Book copy = new Book(validBook);
        assertEquals(validBook, copy);
    }

    /**
     * Копія повинна бути окремим об'єктом (не тим самим посиланням).
     */
    @Test
    void copyConstructor_differentReference_afterCopy() {
        Book copy = new Book(validBook);
        assertNotSame(validBook, copy);
    }

    /**
     * Зміна поля у копії не повинна впливати на оригінал.
     */
    @Test
    void copyConstructor_mutatingCopy_doesNotAffectOriginal() {
        Book copy = new Book(validBook);
        copy.setTitle("Modified Title");
        assertEquals("Clean Code", validBook.getTitle());
    }

    /**
     * Копіювання {@code null} — {@link InvalidBookDataException}.
     */
    @Test
    void copyConstructor_nullSource_throwsException() {
        assertThrows(InvalidBookDataException.class, () -> new Book(null));
    }

    // ---------------------------------------------------------------
    // Статичний лічильник
    // ---------------------------------------------------------------

    /**
     * Після створення N нових об'єктів лічильник повинен збільшитися рівно на N.
     */
    @Test
    void instanceCount_incrementsForEachNewObject() {
        int before = Book.getInstanceCount();

        new Book("A", "B", 2000, 5.0, Genre.MYSTERY, 100);
        new Book("C", "D", 2001, 6.0, Genre.FANTASY, 200);

        assertEquals(before + 2, Book.getInstanceCount());
    }

    /**
     * Конструктор копіювання теж повинен збільшувати лічильник.
     */
    @Test
    void instanceCount_incrementsForCopyConstructor() {
        int before = Book.getInstanceCount();
        new Book(validBook);
        assertEquals(before + 1, Book.getInstanceCount());
    }

    // ---------------------------------------------------------------
    // Сетери — негативні кейси
    // ---------------------------------------------------------------

    /**
     * {@code setTitle} з рядком тільки з пробілів — {@link InvalidBookDataException}.
     */
    @Test
    void setTitle_blankString_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setTitle("   "));
    }

    /**
     * {@code setAuthor} з порожнім рядком — {@link InvalidBookDataException}.
     */
    @Test
    void setAuthor_emptyString_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setAuthor(""));
    }

    /**
     * {@code setYear} з нулем — {@link InvalidBookDataException}.
     */
    @Test
    void setYear_zeroValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setYear(0));
    }

    /**
     * {@code setPrice} з від'ємним числом — {@link InvalidBookDataException}.
     */
    @Test
    void setPrice_negativeValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setPrice(-0.01));
    }

    /**
     * {@code setPrice(0.0)} дозволено (безкоштовна книга).
     */
    @Test
    void setPrice_zeroValue_isAllowed() {
        assertDoesNotThrow(() -> validBook.setPrice(0.0));
        assertEquals(0.0, validBook.getPrice(), 0.001);
    }

    /**
     * {@code setPages} з від'ємним числом — {@link InvalidBookDataException}.
     */
    @Test
    void setPages_negativeValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setPages(-5));
    }

    /**
     * {@code setGenre(null)} — {@link InvalidBookDataException}.
     */
    @Test
    void setGenre_nullValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setGenre(null));
    }

    // ---------------------------------------------------------------
    // Агрегація: Library
    // ---------------------------------------------------------------

    /**
     * Додавання книги до бібліотеки збільшує лічильник книг на 1.
     */
    @Test
    void library_addBook_increasesCount() {
        Library library = new Library("Test Library", "Test St. 1");
        assertEquals(0, library.getBookCount());
        library.addBook(validBook);
        assertEquals(1, library.getBookCount());
    }

    /**
     * Видалення книги з бібліотеки зменшує лічильник на 1.
     */
    @Test
    void library_removeBook_decreasesCount() {
        Library library = new Library("Test Library", "Test St. 1");
        library.addBook(validBook);
        library.removeBook(0);
        assertEquals(0, library.getBookCount());
    }

    /**
     * Спроба отримати книгу за некоректним індексом — {@link InvalidBookDataException}.
     */
    @Test
    void library_getBook_invalidIndex_throwsException() {
        Library library = new Library("Test Library", "Test St. 1");
        assertThrows(InvalidBookDataException.class, () -> library.getBook(0));
    }

    /**
     * Порожня назва бібліотеки у конструкторі — {@link InvalidBookDataException}.
     */
    @Test
    void library_emptyName_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Library("", "Address"));
    }
}