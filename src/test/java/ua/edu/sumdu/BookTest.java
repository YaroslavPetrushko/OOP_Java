package ua.edu.sumdu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ua.edu.sumdu.model.AudioBook;
import ua.edu.sumdu.model.Book;
import ua.edu.sumdu.model.BookCondition;
import ua.edu.sumdu.model.EBook;
import ua.edu.sumdu.model.Genre;
import ua.edu.sumdu.model.InvalidBookDataException;
import ua.edu.sumdu.model.PaperBook;
import ua.edu.sumdu.model.RareBook;
import ua.edu.sumdu.storage.JsonBookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Автотести для ієрархії моделі та обох реалізацій {@link ua.edu.sumdu.storage.BookStorage}.
 *
 * <p>Покривають:</p>
 * <ul>
 *   <li>валідацію конструкторів і сетерів усіх п'яти класів;</li>
 *   <li>конструктори копіювання;</li>
 *   <li>поліморфний вивід через {@code ArrayList<Book>};</li>
 *   <li>{@link TxtBookStorage}: запис і повторне зчитування всіх типів;</li>
 *   <li>{@link JsonBookStorage}: запис і повторне зчитування всіх типів;</li>
 *   <li>стійкість storage до відсутнього файлу та некоректних рядків.</li>
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
        return new EBook("Pragmatic Programmer", "David Thomas",
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
                "Harper", 1, 480.0,
                BookCondition.FINE, 4500.00, 1980);
    }

    // ===================================================================
    // Модель — Book
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
    void book_emptyTitle_throws()       { assertThrows(InvalidBookDataException.class, () -> new Book("", "A", 2000, 10.0, Genre.FICTION, 100)); }
    @Test
    void book_nullAuthor_throws()       { assertThrows(InvalidBookDataException.class, () -> new Book("T", null, 2000, 10.0, Genre.FICTION, 100)); }
    @Test
    void book_negativePrice_throws()    { assertThrows(InvalidBookDataException.class, () -> new Book("T", "A", 2000, -1.0, Genre.FICTION, 100)); }
    @Test
    void book_zeroPagesCount_throws()   { assertThrows(InvalidBookDataException.class, () -> new Book("T", "A", 2000, 10.0, Genre.FICTION, 0)); }
    @Test
    void book_futureYear_throws()       { assertThrows(InvalidBookDataException.class, () -> new Book("T", "A", java.time.Year.now().getValue() + 1, 10.0, Genre.FICTION, 100)); }
    @Test
    void book_nullGenre_throws()        { assertThrows(InvalidBookDataException.class, () -> new Book("T", "A", 2000, 10.0, null, 100)); }
    @Test
    void book_setPrice_zero_allowed()   { assertDoesNotThrow(() -> validBook().setPrice(0.0)); }
    @Test
    void book_copyConstructor_null_throws() { assertThrows(InvalidBookDataException.class, () -> new Book((Book) null)); }

    @Test
    void book_copyConstructor_equalAndIndependent() {
        Book orig = validBook();
        Book copy = new Book(orig);
        assertEquals(orig, copy);
        assertNotSame(orig, copy);
        copy.setTitle("Other");
        assertEquals("Clean Code", orig.getTitle());
    }

    @Test
    void book_toString_startsWithTag() { assertTrue(validBook().toString().startsWith("[Book]")); }

    // ===================================================================
    // Модель — EBook
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
    void eBook_emptyFileFormat_throws()  { assertThrows(InvalidBookDataException.class, () -> new EBook("T","A",2020,5.0,Genre.FICTION,100," ",2.0,"https://x.com")); }
    @Test
    void eBook_zeroFileSize_throws()     { assertThrows(InvalidBookDataException.class, () -> new EBook("T","A",2020,5.0,Genre.FICTION,100,"PDF",0.0,"https://x.com")); }
    @Test
    void eBook_emptyUrl_throws()         { assertThrows(InvalidBookDataException.class, () -> new EBook("T","A",2020,5.0,Genre.FICTION,100,"PDF",2.0,"")); }
    @Test
    void eBook_fileFormat_upperCase()    { assertEquals("EPUB", new EBook("T","A",2020,5.0,Genre.FICTION,100,"epub",2.0,"https://x.com").getFileFormat()); }
    @Test
    void eBook_toString_startsWithTag()  { assertTrue(validEBook().toString().startsWith("[EBook]")); }

    @Test
    void eBook_copyConstructor_independent() {
        EBook orig = validEBook();
        EBook copy = new EBook(orig);
        copy.setFileFormat("MOBI");
        assertEquals("EPUB", orig.getFileFormat());
    }

    // ===================================================================
    // Модель — AudioBook
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
    void audioBook_emptyNarrator_throws()   { assertThrows(InvalidBookDataException.class, () -> new AudioBook("T","A",2020,5.0,Genre.FICTION,100,"",120,"MP3")); }
    @Test
    void audioBook_zeroDuration_throws()    { assertThrows(InvalidBookDataException.class, () -> new AudioBook("T","A",2020,5.0,Genre.FICTION,100,"N",0,"MP3")); }
    @Test
    void audioBook_audioFormat_upperCase()  { assertEquals("FLAC", new AudioBook("T","A",2020,5.0,Genre.FICTION,100,"N",120,"flac").getAudioFormat()); }
    @Test
    void audioBook_toString_startsWithTag() { assertTrue(validAudioBook().toString().startsWith("[AudioBook]")); }

    @Test
    void audioBook_copyConstructor_independent() {
        AudioBook orig = validAudioBook();
        AudioBook copy = new AudioBook(orig);
        copy.setNarrator("Other");
        assertEquals("Scott Brick", orig.getNarrator());
    }

    // ===================================================================
    // Модель — PaperBook
    // ===================================================================

    @Test
    void paperBook_emptyPublisher_throws() { assertThrows(InvalidBookDataException.class, () -> new PaperBook("T","A",2000,10.0,Genre.FICTION,100,"",1,300.0)); }
    @Test
    void paperBook_zeroEdition_throws()    { assertThrows(InvalidBookDataException.class, () -> new PaperBook("T","A",2000,10.0,Genre.FICTION,100,"P",0,300.0)); }
    @Test
    void paperBook_negativeWeight_throws() { assertThrows(InvalidBookDataException.class, () -> new PaperBook("T","A",2000,10.0,Genre.FICTION,100,"P",1,-1.0)); }
    @Test
    void paperBook_toString_startsWithTag(){ assertTrue(validPaperBook().toString().startsWith("[PaperBook]")); }

    // ===================================================================
    // Модель — RareBook
    // ===================================================================

    @Test
    void rareBook_validData_storesExtraFields() {
        RareBook rb = validRareBook();
        assertAll(
                () -> assertEquals(BookCondition.FINE, rb.getCondition()),
                () -> assertEquals(4500.0, rb.getEstimatedValueUSD(), 0.001),
                () -> assertEquals(1980,   rb.getAcquisitionYear())
        );
    }

    @Test
    void rareBook_nullCondition_throws()       { assertThrows(InvalidBookDataException.class, () -> new RareBook("T","A",1900,10.0,Genre.FICTION,100,"P",1,300.0,null,1000.0,2000)); }
    @Test
    void rareBook_zeroEstimatedValue_throws()  { assertThrows(InvalidBookDataException.class, () -> new RareBook("T","A",1900,10.0,Genre.FICTION,100,"P",1,300.0,BookCondition.GOOD,0.0,2000)); }
    @Test
    void rareBook_futureAcquisitionYear_throws(){ assertThrows(InvalidBookDataException.class, () -> new RareBook("T","A",1900,10.0,Genre.FICTION,100,"P",1,300.0,BookCondition.GOOD,500.0,java.time.Year.now().getValue()+1)); }
    @Test
    void rareBook_toString_startsWithTag()      { assertTrue(validRareBook().toString().startsWith("[RareBook]")); }

    @Test
    void rareBook_copyConstructor_independent() {
        RareBook orig = validRareBook();
        RareBook copy = new RareBook(orig);
        copy.setCondition(BookCondition.POOR);
        assertEquals(BookCondition.FINE, orig.getCondition());
    }

    // ===================================================================
    // Поліморфізм
    // ===================================================================

    @Test
    void polymorphism_allFiveTypesInOneCollection() {
        ArrayList<Book> list = new ArrayList<Book>();
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

    @Test
    void polymorphism_toStringTagMatchesRealType() {
        ArrayList<Book> list = new ArrayList<Book>();
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

    // ===================================================================
    // TxtBookStorage — round-trip (зберегти → завантажити)
    // ===================================================================

    /**
     * Зберігає колекцію всіх п'яти типів у тимчасовий файл,
     * потім завантажує і перевіряє кількість та типи.
     *
     * @param tempDir тимчасова директорія, що надається JUnit
     */
    @Test
    void txtStorage_roundTrip_allTypes(@TempDir Path tempDir) {
        String path = tempDir.resolve("test.txt").toString();
        TxtBookStorage storage = new TxtBookStorage(path);

        ArrayList<Book> original = new ArrayList<Book>();
        original.add(validBook());
        original.add(validEBook());
        original.add(validAudioBook());
        original.add(validPaperBook());
        original.add(validRareBook());

        storage.save(original);
        ArrayList<Book> loaded = storage.load();

        assertEquals(5, loaded.size());
        assertInstanceOf(Book.class,      loaded.get(0));
        assertInstanceOf(EBook.class,     loaded.get(1));
        assertInstanceOf(AudioBook.class, loaded.get(2));
        assertInstanceOf(PaperBook.class, loaded.get(3));
        assertInstanceOf(RareBook.class,  loaded.get(4));
    }

    /**
     * Перевіряє збереження та відновлення конкретних полів EBook через TXT.
     */
    @Test
    void txtStorage_eBook_fieldsPreservedAfterRoundTrip(@TempDir Path tempDir) {
        String path = tempDir.resolve("ebook.txt").toString();
        TxtBookStorage storage = new TxtBookStorage(path);

        ArrayList<Book> list = new ArrayList<Book>();
        list.add(validEBook());
        storage.save(list);

        ArrayList<Book> loaded = storage.load();
        EBook e = (EBook) loaded.get(0);

        assertAll(
                () -> assertEquals("Pragmatic Programmer", e.getTitle()),
                () -> assertEquals("EPUB",                 e.getFileFormat()),
                () -> assertEquals(4.5, e.getFileSizeMB(), 0.001)
        );
    }

    /**
     * Перевіряє збереження та відновлення конкретних полів RareBook через TXT.
     */
    @Test
    void txtStorage_rareBook_fieldsPreservedAfterRoundTrip(@TempDir Path tempDir) {
        String path = tempDir.resolve("rare.txt").toString();
        TxtBookStorage storage = new TxtBookStorage(path);

        ArrayList<Book> list = new ArrayList<Book>();
        list.add(validRareBook());
        storage.save(list);

        ArrayList<Book> loaded = storage.load();
        RareBook rb = (RareBook) loaded.get(0);

        assertAll(
                () -> assertEquals(BookCondition.FINE, rb.getCondition()),
                () -> assertEquals(4500.0, rb.getEstimatedValueUSD(), 0.001),
                () -> assertEquals(1980,   rb.getAcquisitionYear())
        );
    }

    /**
     * TxtBookStorage повинен повертати порожню колекцію, якщо файл відсутній.
     */
    @Test
    void txtStorage_missingFile_returnsEmptyList(@TempDir Path tempDir) {
        String path = tempDir.resolve("nonexistent.txt").toString();
        ArrayList<Book> result = new TxtBookStorage(path).load();
        assertTrue(result.isEmpty());
    }

    /**
     * TxtBookStorage повинен пропускати некоректні рядки та завантажувати решту.
     */
    @Test
    void txtStorage_corruptedLine_skipsAndLoadsRest(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("corrupt.txt");
        Files.writeString(file,
                "BOOK|Clean Code|Robert C. Martin|2008|39.99|PROGRAMMING|431\n"
                        + "INVALID_LINE_WITHOUT_ENOUGH_FIELDS\n"
                        + "EBOOK|Title|Author|2020|9.99|FICTION|200|EPUB|2.5|https://x.com\n");

        ArrayList<Book> loaded = new TxtBookStorage(file.toString()).load();
        assertEquals(2, loaded.size());
    }

    // ===================================================================
    // JsonBookStorage — round-trip
    // ===================================================================

    /**
     * Зберігає та завантажує колекцію всіх типів через JSON.
     */
    @Test
    void jsonStorage_roundTrip_allTypes(@TempDir Path tempDir) {
        String path = tempDir.resolve("test.json").toString();
        JsonBookStorage storage = new JsonBookStorage(path);

        ArrayList<Book> original = new ArrayList<Book>();
        original.add(validBook());
        original.add(validEBook());
        original.add(validAudioBook());
        original.add(validPaperBook());
        original.add(validRareBook());

        storage.save(original);
        ArrayList<Book> loaded = storage.load();

        assertEquals(5, loaded.size());
        assertInstanceOf(Book.class,      loaded.get(0));
        assertInstanceOf(EBook.class,     loaded.get(1));
        assertInstanceOf(AudioBook.class, loaded.get(2));
        assertInstanceOf(PaperBook.class, loaded.get(3));
        assertInstanceOf(RareBook.class,  loaded.get(4));
    }

    /**
     * Перевіряє збереження та відновлення конкретних полів AudioBook через JSON.
     */
    @Test
    void jsonStorage_audioBook_fieldsPreservedAfterRoundTrip(@TempDir Path tempDir) {
        String path = tempDir.resolve("audio.json").toString();
        JsonBookStorage storage = new JsonBookStorage(path);

        ArrayList<Book> list = new ArrayList<Book>();
        list.add(validAudioBook());
        storage.save(list);

        ArrayList<Book> loaded = storage.load();
        AudioBook ab = (AudioBook) loaded.get(0);

        assertAll(
                () -> assertEquals("Dune",        ab.getTitle()),
                () -> assertEquals("Scott Brick", ab.getNarrator()),
                () -> assertEquals(1260,          ab.getDurationMinutes()),
                () -> assertEquals("MP3",         ab.getAudioFormat())
        );
    }

    /**
     * JsonBookStorage повинен повертати порожню колекцію, якщо файл відсутній.
     */
    @Test
    void jsonStorage_missingFile_returnsEmptyList(@TempDir Path tempDir) {
        String path = tempDir.resolve("nonexistent.json").toString();
        ArrayList<Book> result = new JsonBookStorage(path).load();
        assertTrue(result.isEmpty());
    }
}