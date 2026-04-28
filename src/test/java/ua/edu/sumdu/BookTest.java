package ua.edu.sumdu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.edu.sumdu.model.AudioBook;
import ua.edu.sumdu.model.Book;
import ua.edu.sumdu.model.BookCondition;
import ua.edu.sumdu.model.BookEntry;
import ua.edu.sumdu.model.EBook;
import ua.edu.sumdu.model.Genre;
import ua.edu.sumdu.model.InvalidBookDataException;
import ua.edu.sumdu.model.Library;
import ua.edu.sumdu.model.PaperBook;
import ua.edu.sumdu.model.RareBook;
import ua.edu.sumdu.storage.JsonBookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Library library;
    private Book cleanCode;
    private EBook pragmaticProgrammer;
    private AudioBook dune;
    private PaperBook designPatterns;
    private RareBook mobyDick;
    private Book cleanCoder;

    @BeforeEach
    void setUp() {
        library = new Library("Test Library", "Main St. 1");

        cleanCode = new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431);
        pragmaticProgrammer = new EBook("Pragmatic Programmer", "David Thomas",
                2019, 29.99, Genre.PROGRAMMING, 352,
                "EPUB", 4.5, "https://example.com/book.epub");
        dune = new AudioBook("Dune", "Frank Herbert",
                1965, 19.99, Genre.SCI_FI, 688,
                "Scott Brick", 1260, "MP3");
        designPatterns = new PaperBook("Design Patterns", "GoF",
                1994, 54.99, Genre.PROGRAMMING, 395,
                "Addison-Wesley", 1, 730.0);
        mobyDick = new RareBook("Moby Dick", "Herman Melville",
                1851, 12.99, Genre.FICTION, 635,
                "Harper", 1, 480.0,
                BookCondition.FINE, 4500.00, 1980);
        cleanCoder = new Book("The Clean Coder", "Robert C. Martin",
                2011, 34.99, Genre.PROGRAMMING, 256);

        library.addNewBook(cleanCode, 2);
        library.addNewBook(pragmaticProgrammer, 1);
        library.addNewBook(dune, 3);
        library.addNewBook(designPatterns, 1);
        library.addNewBook(mobyDick, 4);
        library.addNewBook(cleanCoder, 2);
    }

    @Test
    void book_emptyTitle_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("", "A", 2000, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_nullAuthor_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("T", null, 2000, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_negativePrice_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("T", "A", 2000, -1.0, Genre.FICTION, 100));
    }

    @Test
    void book_zeroPagesCount_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("T", "A", 2000, 10.0, Genre.FICTION, 0));
    }

    @Test
    void book_futureYear_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("T", "A", java.time.Year.now().getValue() + 1,
                        10.0, Genre.FICTION, 100));
    }

    @Test
    void book_nullGenre_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Book("T", "A", 2000, 10.0, null, 100));
    }

    @Test
    void book_setPrice_zero_allowed() {
        assertDoesNotThrow(() -> cleanCode.setPrice(0.0));
    }

    @Test
    void book_copyConstructor_independent() {
        Book copy = new Book(cleanCode);
        assertEquals(cleanCode, copy);
        assertNotSame(cleanCode, copy);
        copy.setTitle("Other");
        assertEquals("Clean Code", cleanCode.getTitle());
    }

    @Test
    void eBook_fileFormat_upperCase() {
        EBook book = new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                "epub", 2.0, "https://x.com");
        assertEquals("EPUB", book.getFileFormat());
    }

    @Test
    void eBook_zeroFileSize_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "PDF", 0.0, "https://x.com"));
    }

    @Test
    void audioBook_zeroDuration_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "N", 0, "MP3"));
    }

    @Test
    void audioBook_audioFormat_upperCase() {
        AudioBook book = new AudioBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                "N", 120, "flac");
        assertEquals("FLAC", book.getAudioFormat());
    }

    @Test
    void rareBook_nullCondition_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new RareBook("T", "A", 1900, 10.0, Genre.FICTION, 100,
                        "P", 1, 300.0, null, 1000.0, 2000));
    }

    @Test
    void rareBook_zeroEstimatedValue_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new RareBook("T", "A", 1900, 10.0, Genre.FICTION, 100,
                        "P", 1, 300.0, BookCondition.GOOD, 0.0, 2000));
    }

    @Test
    void library_blankName_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new Library(" ", "Main St. 1"));
    }

    @Test
    void bookEntry_zeroQuantity_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new BookEntry(cleanCode, 0));
    }

    @Test
    void library_addSameBook_mergesQuantity() {
        Book duplicate = new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431);

        library.addNewBook(duplicate, 5);

        assertEquals(6, library.getEntryCount());
        BookEntry entry = library.findByAuthor("Robert C. Martin").get(0);
        assertEquals(7, entry.getQuantity());
    }

    @Test
    void bookEntry_addQuantity_updatesAmount() {
        BookEntry entry = new BookEntry(cleanCode, 2);
        entry.addQuantity(3);
        assertEquals(5, entry.getQuantity());
    }

    @Test
    void polymorphism_toStringTagMatchesRealType() {
        assertTrue(cleanCode.toString().startsWith("[Book]"));
        assertTrue(pragmaticProgrammer.toString().startsWith("[EBook]"));
        assertTrue(dune.toString().startsWith("[AudioBook]"));
        assertTrue(designPatterns.toString().startsWith("[PaperBook]"));
        assertTrue(mobyDick.toString().startsWith("[RareBook]"));
    }

    @Test
    void findByAuthor_exactMatch_returnsTwoEntries() {
        assertEquals(2, library.findByAuthor("Robert C. Martin").size());
    }

    @Test
    void findByAuthor_partialSubstring_findsMatch() {
        assertEquals(2, library.findByAuthor("Martin").size());
    }

    @Test
    void findByAuthor_caseInsensitive_findsMatch() {
        assertEquals(2, library.findByAuthor("robert c. martin").size());
    }

    @Test
    void findByAuthor_noMatch_returnsEmpty() {
        assertTrue(library.findByAuthor("Tolkien").isEmpty());
    }

    @Test
    void findByAuthor_emptyString_returnsEmpty() {
        assertTrue(library.findByAuthor("").isEmpty());
    }

    @Test
    void findByAuthor_null_returnsEmpty() {
        assertTrue(library.findByAuthor(null).isEmpty());
    }

    @Test
    void findByAuthor_emptyLibrary_returnsEmpty() {
        Library emptyLibrary = new Library("Empty", "Nowhere");
        assertTrue(emptyLibrary.findByAuthor("Martin").isEmpty());
    }

    @Test
    void findByAuthor_doesNotModifyLibrary() {
        int countBefore = library.getEntryCount();
        library.findByAuthor("Martin");
        assertEquals(countBefore, library.getEntryCount());
    }

    @Test
    void findByGenre_programming_returnsFourEntries() {
        assertEquals(4, library.findByGenre(Genre.PROGRAMMING).size());
    }

    @Test
    void findByGenre_sciFi_returnsOneEntry() {
        BookEntry entry = library.findByGenre(Genre.SCI_FI).get(0);
        assertInstanceOf(AudioBook.class, entry.getBook());
        assertEquals(3, entry.getQuantity());
    }

    @Test
    void findByGenre_notPresent_returnsEmpty() {
        assertTrue(library.findByGenre(Genre.BIOGRAPHY).isEmpty());
    }

    @Test
    void findByGenre_null_returnsEmpty() {
        assertTrue(library.findByGenre(null).isEmpty());
    }

    @Test
    void findByGenre_doesNotModifyLibrary() {
        int countBefore = library.getEntryCount();
        library.findByGenre(Genre.FICTION);
        assertEquals(countBefore, library.getEntryCount());
    }

    @Test
    void findByPriceRange_fullRange_returnsAll() {
        assertEquals(library.getEntryCount(), library.findByPriceRange(0.0, 100.0).size());
    }

    @Test
    void findByPriceRange_narrowRange_returnsOneEntry() {
        BookEntry entry = library.findByPriceRange(25.0, 30.0).get(0);
        assertInstanceOf(EBook.class, entry.getBook());
        assertEquals(1, entry.getQuantity());
    }

    @Test
    void findByPriceRange_exactBoundary_inclusive() {
        BookEntry entry = library.findByPriceRange(39.99, 39.99).get(0);
        assertEquals("Clean Code", entry.getBook().getTitle());
        assertEquals(2, entry.getQuantity());
    }

    @Test
    void findByPriceRange_invertedRange_returnsEmpty() {
        assertTrue(library.findByPriceRange(50.0, 10.0).isEmpty());
    }

    @Test
    void findByPriceRange_outOfRange_returnsEmpty() {
        assertTrue(library.findByPriceRange(200.0, 500.0).isEmpty());
    }

    @Test
    void findByPriceRange_doesNotModifyLibrary() {
        int countBefore = library.getEntryCount();
        library.findByPriceRange(0.0, 100.0);
        assertEquals(countBefore, library.getEntryCount());
    }

    @Test
    void txtStorage_roundTrip_allTypesAndQuantities(@TempDir Path tempDir) {
        String path = tempDir.resolve("test.txt").toString();
        TxtBookStorage storage = new TxtBookStorage(path);

        Library original = createStorageLibrary();
        storage.save(original);

        Library loaded = new Library("Loaded", "Somewhere");
        storage.load(loaded);

        assertEquals(5, loaded.getEntryCount());
        assertInstanceOf(Book.class, loaded.getEntry(0).getBook());
        assertInstanceOf(EBook.class, loaded.getEntry(1).getBook());
        assertInstanceOf(AudioBook.class, loaded.getEntry(2).getBook());
        assertInstanceOf(PaperBook.class, loaded.getEntry(3).getBook());
        assertInstanceOf(RareBook.class, loaded.getEntry(4).getBook());
        assertEquals(3, loaded.getEntry(2).getQuantity());
        assertEquals(5, loaded.getEntry(4).getQuantity());
    }

    @Test
    void txtStorage_missingFile_leavesLibraryEmpty(@TempDir Path tempDir) {
        Library loaded = new Library("Loaded", "Somewhere");
        new TxtBookStorage(tempDir.resolve("nope.txt").toString()).load(loaded);
        assertEquals(0, loaded.getEntryCount());
    }

    @Test
    void txtStorage_corruptedLine_skipsAndLoadsRest(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("corrupt.txt");
        Files.writeString(file,
                "BOOK|Clean Code|Robert C. Martin|2008|39.99|PROGRAMMING|431|2\n"
                        + "BROKEN LINE NO DELIMITER\n"
                        + "EBOOK|Title|Author|2020|9.99|FICTION|200|EPUB|2.5|https://x.com|4\n");

        Library loaded = new Library("Loaded", "Somewhere");
        new TxtBookStorage(file.toString()).load(loaded);

        assertEquals(2, loaded.getEntryCount());
        assertEquals(2, loaded.getEntry(0).getQuantity());
        assertEquals(4, loaded.getEntry(1).getQuantity());
    }

    @Test
    void jsonStorage_roundTrip_allTypesAndQuantities(@TempDir Path tempDir) {
        String path = tempDir.resolve("test.json").toString();
        JsonBookStorage storage = new JsonBookStorage(path);

        Library original = createStorageLibrary();
        storage.save(original);

        Library loaded = new Library("Loaded", "Somewhere");
        storage.load(loaded);

        assertEquals(5, loaded.getEntryCount());
        assertInstanceOf(Book.class, loaded.getEntry(0).getBook());
        assertInstanceOf(EBook.class, loaded.getEntry(1).getBook());
        assertInstanceOf(AudioBook.class, loaded.getEntry(2).getBook());
        assertInstanceOf(PaperBook.class, loaded.getEntry(3).getBook());
        assertInstanceOf(RareBook.class, loaded.getEntry(4).getBook());
        assertEquals(3, loaded.getEntry(2).getQuantity());
        assertEquals(5, loaded.getEntry(4).getQuantity());
    }

    @Test
    void jsonStorage_missingFile_leavesLibraryEmpty(@TempDir Path tempDir) {
        Library loaded = new Library("Loaded", "Somewhere");
        new JsonBookStorage(tempDir.resolve("nope.json").toString()).load(loaded);
        assertEquals(0, loaded.getEntryCount());
    }

    private Library createStorageLibrary() {
        Library storageLibrary = new Library("Storage Library", "Archive St. 2");
        storageLibrary.addNewBook(new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431), 2);
        storageLibrary.addNewBook(new EBook("EPub Book", "Author",
                2020, 9.99, Genre.FICTION, 200,
                "EPUB", 2.5, "https://x.com"), 1);
        storageLibrary.addNewBook(new AudioBook("Audio", "Narrator",
                2018, 14.99, Genre.MYSTERY, 300,
                "N", 180, "MP3"), 3);
        storageLibrary.addNewBook(new PaperBook("Paper", "P. Author",
                2010, 24.99, Genre.SCIENCE, 400,
                "Publisher", 2, 500.0), 4);
        storageLibrary.addNewBook(new RareBook("Rare", "R. Author",
                1900, 199.99, Genre.NON_FICTION, 600,
                "OldPub", 1, 800.0,
                BookCondition.MINT, 15000.0, 2005), 5);
        return storageLibrary;
    }
}
