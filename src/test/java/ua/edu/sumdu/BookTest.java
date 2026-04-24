package ua.edu.sumdu;

import org.junit.jupiter.api.Test;
import ua.edu.sumdu.model.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Автотести для повної ієрархії класів:
 * {@link Book}, {@link EBook}, {@link AudioBook},
 * {@link PaperBook}, {@link RareBook}.
 *
 * <p>Покривають:</p>
 * <ul>
 *   <li>коректне збереження полів у конструкторах;</li>
 *   <li>валідацію некоректних значень у конструкторах і сетерах;</li>
 *   <li>конструктори копіювання (рівність та незалежність);</li>
 *   <li>поліморфний вивід через {@code ArrayList<Book>}.</li>
 * </ul>
 */
class BookTest {

    // ---------------------------------------------------------------
    // Фабричні допоміжники
    // ---------------------------------------------------------------

    private Book validBook() {
        return new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431);
    }

    private EBook validEBook() {
        return new EBook("The Pragmatic Programmer", "David Thomas",
                2019, 29.99, Genre.PROGRAMMING, 352,
                "EPUB", 4.5, "https://example.com/book.epub");
    }

    private AudioBook validAudioBook() {
        return new AudioBook("Dune", "Frank Herbert",
                1965, 19.99, Genre.SCI_FI, 688,
                "Scott Brick", 1260, "MP3");
    }

    private PaperBook validPaperBook() {
        return new PaperBook("Design Patterns", "GoF",
                1994, 54.99, Genre.PROGRAMMING, 395,
                "Addison-Wesley", 1, 730.0);
    }

    private RareBook validRareBook() {
        return new RareBook("Moby Dick", "Herman Melville",
                1851, 12.99, Genre.FICTION, 635,
                "Harper & Brothers", 1, 480.0,
                BookCondition.FINE, 4500.00, 1980);
    }

    // ===================================================================
    // Book
    // ===================================================================

    @Test
    void book_validData_storesAllFields() {
        Book b = validBook();
        assertAll(
                () -> assertEquals("Clean Code",       b.getTitle()),
                () -> assertEquals("Robert C. Martin", b.getAuthor()),
                () -> assertEquals(2008,               b.getYear()),
                () -> assertEquals(39.99, b.getPrice(), 0.001),
                () -> assertEquals(Genre.PROGRAMMING,  b.getGenre()),
                () -> assertEquals(431,                b.getPages())
        );
    }

    @Test
    void book_emptyTitle_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("", "Author", 2000, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_nullAuthor_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", null, 2000, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_negativePrice_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, -0.01, Genre.FICTION, 100));
    }

    @Test
    void book_zeroPagesCount_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, 10.0, Genre.FICTION, 0));
    }

    @Test
    void book_futureYear_throws() {
        int future = java.time.Year.now().getValue() + 1;
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", future, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_nullGenre_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, 10.0, null, 100));
    }

    @Test
    void book_setPrice_zero_allowed() {
        Book b = validBook();
        assertDoesNotThrow(() -> b.setPrice(0.0));
        assertEquals(0.0, b.getPrice(), 0.001);
    }

    @Test
    void book_setPages_negative_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook().setPages(-1));
    }

    @Test
    void book_copyConstructor_equalAndIndependent() {
        Book original = validBook();
        Book copy = new Book(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setTitle("Other");
        assertEquals("Clean Code", original.getTitle());
    }

    @Test
    void book_copyConstructor_null_throws() {
        assertThrows(InvalidBookDataException.class, () -> new Book((Book) null));
    }

    @Test
    void book_toString_startsWithTag() {
        assertTrue(validBook().toString().startsWith("[Book]"));
    }

    // ===================================================================
    // EBook
    // ===================================================================

    @Test
    void eBook_validData_storesExtraFields() {
        EBook e = validEBook();
        assertAll(
                () -> assertEquals("EPUB", e.getFileFormat()),
                () -> assertEquals(4.5,   e.getFileSizeMB(), 0.001),
                () -> assertEquals("https://example.com/book.epub", e.getDownloadUrl())
        );
    }

    @Test
    void eBook_emptyFileFormat_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "", 2.0, "https://x.com"));
    }

    @Test
    void eBook_zeroFileSize_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "PDF", 0.0, "https://x.com"));
    }

    @Test
    void eBook_emptyUrl_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "PDF", 2.0, ""));
    }

    @Test
    void eBook_fileFormat_storedUpperCase() {
        EBook e = new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                "epub", 2.0, "https://x.com");
        assertEquals("EPUB", e.getFileFormat());
    }

    @Test
    void eBook_copyConstructor_equalAndIndependent() {
        EBook original = validEBook();
        EBook copy = new EBook(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setFileFormat("MOBI");
        assertEquals("EPUB", original.getFileFormat());
    }

    @Test
    void eBook_toString_startsWithTag() {
        assertTrue(validEBook().toString().startsWith("[EBook]"));
    }

    // ===================================================================
    // AudioBook
    // ===================================================================

    @Test
    void audioBook_validData_storesExtraFields() {
        AudioBook ab = validAudioBook();
        assertAll(
                () -> assertEquals("Scott Brick", ab.getNarrator()),
                () -> assertEquals(1260,          ab.getDurationMinutes()),
                () -> assertEquals("MP3",         ab.getAudioFormat())
        );
    }

    @Test
    void audioBook_emptyNarrator_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "", 120, "MP3"));
    }

    @Test
    void audioBook_zeroDuration_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "Narrator", 0, "MP3"));
    }

    @Test
    void audioBook_negativeDuration_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "Narrator", -30, "MP3"));
    }

    @Test
    void audioBook_emptyAudioFormat_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "Narrator", 120, "  "));
    }

    @Test
    void audioBook_audioFormat_storedUpperCase() {
        AudioBook ab = new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                "Narrator", 120, "flac");
        assertEquals("FLAC", ab.getAudioFormat());
    }

    @Test
    void audioBook_copyConstructor_equalAndIndependent() {
        AudioBook original = validAudioBook();
        AudioBook copy     = new AudioBook(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setNarrator("Other Narrator");
        assertEquals("Scott Brick", original.getNarrator());
    }

    @Test
    void audioBook_toString_startsWithTag() {
        assertTrue(validAudioBook().toString().startsWith("[AudioBook]"));
    }

    // ===================================================================
    // PaperBook
    // ===================================================================

    @Test
    void paperBook_validData_storesExtraFields() {
        PaperBook pb = validPaperBook();
        assertAll(
                () -> assertEquals("Addison-Wesley", pb.getPublisher()),
                () -> assertEquals(1,     pb.getEdition()),
                () -> assertEquals(730.0, pb.getWeightGrams(), 0.001)
        );
    }

    @Test
    void paperBook_emptyPublisher_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "", 1, 300.0));
    }

    @Test
    void paperBook_zeroEdition_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "Publisher", 0, 300.0));
    }

    @Test
    void paperBook_negativeWeight_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "Publisher", 1, -10.0));
    }

    @Test
    void paperBook_copyConstructor_equalAndIndependent() {
        PaperBook original = validPaperBook();
        PaperBook copy = new PaperBook(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setPublisher("Other");
        assertEquals("Addison-Wesley", original.getPublisher());
    }

    @Test
    void paperBook_toString_startsWithTag() {
        assertTrue(validPaperBook().toString().startsWith("[PaperBook]"));
    }

    // ===================================================================
    // RareBook
    // ===================================================================

    @Test
    void rareBook_validData_storesExtraFields() {
        RareBook rb = validRareBook();
        assertAll(
                () -> assertEquals(BookCondition.FINE, rb.getCondition()),
                () -> assertEquals(4500.00, rb.getEstimatedValueUSD(), 0.001),
                () -> assertEquals(1980,    rb.getAcquisitionYear())
        );
    }

    @Test
    void rareBook_nullCondition_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new RareBook("T", "A", 1900, 10.0, Genre.FICTION, 100,
                        "Publisher", 1, 300.0,
                        null, 1000.0, 2000));
    }

    @Test
    void rareBook_zeroEstimatedValue_throws() {
        assertThrows(InvalidBookDataException.class, () ->
                new RareBook("T", "A", 1900, 10.0, Genre.FICTION, 100,
                        "Publisher", 1, 300.0,
                        BookCondition.GOOD, 0.0, 2000));
    }

    @Test
    void rareBook_futureAcquisitionYear_throws() {
        int futureYear = java.time.Year.now().getValue() + 1;
        assertThrows(InvalidBookDataException.class, () ->
                new RareBook("T", "A", 1900, 10.0, Genre.FICTION, 100,
                        "Publisher", 1, 300.0,
                        BookCondition.GOOD, 500.0, futureYear));
    }

    @Test
    void rareBook_copyConstructor_equalAndIndependent() {
        RareBook original = validRareBook();
        RareBook copy     = new RareBook(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setCondition(BookCondition.POOR);
        assertEquals(BookCondition.FINE, original.getCondition());
    }

    @Test
    void rareBook_toString_startsWithTag() {
        assertTrue(validRareBook().toString().startsWith("[RareBook]"));
    }

    // ===================================================================
    // Поліморфізм — ArrayList<Book>
    // ===================================================================

    /**
     * Усі п'ять типів зберігаються в одній {@code ArrayList<Book>}.
     */
    @Test
    void polymorphism_allFiveTypesInOneCollection() {
        ArrayList<Book> list = new ArrayList<>();
        list.add(validBook());
        list.add(validEBook());
        list.add(validAudioBook());
        list.add(validPaperBook());
        list.add(validRareBook());

        assertEquals(5, list.size());
        assertInstanceOf(Book.class,      list.get(0));
        assertInstanceOf(EBook.class,     list.get(1));
        assertInstanceOf(AudioBook.class, list.get(2));
        assertInstanceOf(PaperBook.class, list.get(3));
        assertInstanceOf(RareBook.class,  list.get(4));
    }

    /**
     * {@code toString()} диспетчеризується за реальним типом (динамічний поліморфізм).
     */
    @Test
    void polymorphism_toStringDispatchedByRealType() {
        ArrayList<Book> list = new ArrayList<>();
        list.add(validBook());
        list.add(validEBook());
        list.add(validAudioBook());
        list.add(validPaperBook());
        list.add(validRareBook());

        assertTrue(list.get(0).toString().startsWith("[Book]"));
        assertTrue(list.get(1).toString().startsWith("[EBook]"));
        assertTrue(list.get(2).toString().startsWith("[AudioBook]"));
        assertTrue(list.get(3).toString().startsWith("[PaperBook]"));
        assertTrue(list.get(4).toString().startsWith("[RareBook]"));
    }

    /**
     * Колекція на початку порожня (відповідно до вимоги завдання).
     */
    @Test
    void collection_startsEmpty() {
        ArrayList<Book> list = new ArrayList<>();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }
}