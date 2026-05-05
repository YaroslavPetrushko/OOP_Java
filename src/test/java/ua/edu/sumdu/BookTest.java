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
import ua.edu.sumdu.model.ObjectNotFoundException;
import ua.edu.sumdu.model.DuplicateObjectException;
import ua.edu.sumdu.model.Library;
import ua.edu.sumdu.model.PaperBook;
import ua.edu.sumdu.model.RareBook;
import ua.edu.sumdu.storage.JsonBookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Library   library;

    // cleanCode і cleanCoder — EBook (Book абстрактний, конкретний об'єкт потрібен для тестів)
    private EBook     cleanCode;
    private EBook     pragmaticProgrammer;
    private AudioBook dune;
    private PaperBook designPatterns;
    private RareBook  mobyDick;
    private EBook     cleanCoder;

    // ---------------------------------------------------------------
    // Допоміжні валідні аргументи для EBook (щоб тестувати базові поля)
    // ---------------------------------------------------------------
    private static final String VALID_FORMAT  = "PDF";
    private static final double VALID_SIZE    = 5.0;
    private static final String VALID_URL     = "https://example.com/book";

    @BeforeEach
    void setUp() {
        library = new Library("Test Library", "Main St. 1");

        cleanCode = new EBook("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431,
                "PDF", 15.0, "https://example.com/cleancode.pdf");

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

        cleanCoder = new EBook("The Clean Coder", "Robert C. Martin",
                2011, 34.99, Genre.PROGRAMMING, 256,
                "EPUB", 8.0, "https://example.com/cleancoder.epub");

        library.addNewBook(cleanCode, 2);
        library.addNewBook(pragmaticProgrammer, 1);
        library.addNewBook(dune, 3);
        library.addNewBook(designPatterns, 1);
        library.addNewBook(mobyDick, 4);
        library.addNewBook(cleanCoder, 2);
    }

    // ---------------------------------------------------------------
    // Валідація базових полів Book (через EBook як конкретний підклас)
    // ---------------------------------------------------------------

    @Test
    void book_emptyTitle_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("", "A", 2000, 10.0, Genre.FICTION, 100,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_nullAuthor_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", null, 2000, 10.0, Genre.FICTION, 100,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_negativePrice_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", "A", 2000, -1.0, Genre.FICTION, 100,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_zeroPagesCount_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", "A", 2000, 10.0, Genre.FICTION, 0,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_futureYear_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", "A", java.time.Year.now().getValue() + 1,
                        10.0, Genre.FICTION, 100,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_nullGenre_throws() {
        assertThrows(InvalidBookDataException.class,
                () -> new EBook("T", "A", 2000, 10.0, null, 100,
                        VALID_FORMAT, VALID_SIZE, VALID_URL));
    }

    @Test
    void book_setPrice_zero_allowed() {
        assertDoesNotThrow(() -> cleanCode.setPrice(0.0));
    }

    @Test
    void book_copyConstructor_independent() {
        EBook copy = new EBook(cleanCode);
        assertEquals(cleanCode, copy);
        assertNotSame(cleanCode, copy);
        copy.setTitle("Other");
        assertEquals("Clean Code", cleanCode.getTitle());
    }

    // ---------------------------------------------------------------
    // EBook
    // ---------------------------------------------------------------

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

    // ---------------------------------------------------------------
    // AudioBook
    // ---------------------------------------------------------------

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

    // ---------------------------------------------------------------
    // RareBook
    // ---------------------------------------------------------------

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

    // ---------------------------------------------------------------
    // Library
    // ---------------------------------------------------------------

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
        // Дублікат — той самий EBook (title + усі поля однакові)
        EBook duplicate = new EBook("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431,
                "PDF", 15.0, "https://example.com/cleancode.pdf");

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

    // ---------------------------------------------------------------
    // Поліморфізм toString — Book абстрактний, тегу [Book] більше немає
    // ---------------------------------------------------------------

    @Test
    void polymorphism_toStringTagMatchesRealType() {
        assertTrue(cleanCode.toString().startsWith("[EBook]"));
        assertTrue(pragmaticProgrammer.toString().startsWith("[EBook]"));
        assertTrue(dune.toString().startsWith("[AudioBook]"));
        assertTrue(designPatterns.toString().startsWith("[PaperBook]"));
        assertTrue(mobyDick.toString().startsWith("[RareBook]"));
    }

    // ---------------------------------------------------------------
    // Comparable / сортування
    // ---------------------------------------------------------------

    @Test
    void compareTo_sameTitle_returnsZero() {
        EBook a = new EBook("Alpha", "Author", 2020, 10.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        EBook b = new EBook("Alpha", "Other", 2021, 20.0, Genre.SCIENCE, 200,
                "EPUB", 2.0, "https://y.com");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void compareTo_caseInsensitive_treatsEqual() {
        EBook lower = new EBook("alpha", "A", 2020, 0.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        EBook upper = new EBook("ALPHA", "A", 2020, 0.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        assertEquals(0, lower.compareTo(upper));
    }

    @Test
    void compareTo_lesserTitle_returnsNegative() {
        EBook a = new EBook("Alpha", "A", 2020, 0.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        EBook b = new EBook("Zebra", "A", 2020, 0.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareTo_crossSubclass_sortsByTitle() {
        // EBook і AudioBook сортуються між собою через Comparable
        EBook    eb = new EBook("Moby Dick", "A", 2000, 0.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com");
        AudioBook ab = new AudioBook("Clean Code", "B", 2010, 0.0, Genre.FICTION, 100,
                "N", 60, "MP3");
        // "Clean Code" < "Moby Dick"
        assertTrue(ab.compareTo(eb) < 0);
    }

    @Test
    void sortedBooks_singleElement_returnsSameElement() {
        Library single = new Library("Single", "Nowhere");
        single.addNewBook(cleanCode, 1);

        ArrayList<BookEntry> sorted = single.getAllEntries();
        Collections.sort(sorted, new java.util.Comparator<BookEntry>() {
            @Override
            public int compare(BookEntry a, BookEntry b) {
                return a.getBook().compareTo(b.getBook());
            }
        });

        assertEquals(1, sorted.size());
        assertEquals("Clean Code", sorted.get(0).getBook().getTitle());
    }

    @Test
    void sortedBooks_emptyLibrary_returnsEmptyList() {
        Library empty = new Library("Empty", "Nowhere");
        ArrayList<BookEntry> sorted = empty.getAllEntries();
        Collections.sort(sorted, new java.util.Comparator<BookEntry>() {
            @Override
            public int compare(BookEntry a, BookEntry b) {
                return a.getBook().compareTo(b.getBook());
            }
        });
        assertTrue(sorted.isEmpty());
    }

    @Test
    void sortedBooks_multipleEntries_alphabeticalOrder() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, new java.util.Comparator<BookEntry>() {
            @Override
            public int compare(BookEntry a, BookEntry b) {
                return a.getBook().compareTo(b.getBook());
            }
        });

        // Очікуваний порядок: Clean Code, Design Patterns, Dune,
        //                     Moby Dick, Pragmatic Programmer, The Clean Coder
        assertEquals("Clean Code",          sorted.get(0).getBook().getTitle());
        assertEquals("Design Patterns",     sorted.get(1).getBook().getTitle());
        assertEquals("Dune",                sorted.get(2).getBook().getTitle());
        assertEquals("Moby Dick",           sorted.get(3).getBook().getTitle());
        assertEquals("Pragmatic Programmer",sorted.get(4).getBook().getTitle());
        assertEquals("The Clean Coder",     sorted.get(5).getBook().getTitle());
    }

    @Test
    void sortedBooks_doesNotModifyLibraryOrder() {
        // Оригінальний перший елемент (за порядком додавання)
        String firstBefore = library.getEntry(0).getBook().getTitle();

        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, new java.util.Comparator<BookEntry>() {
            @Override
            public int compare(BookEntry a, BookEntry b) {
                return a.getBook().compareTo(b.getBook());
            }
        });

        // Внутрішній порядок Library не змінився
        assertEquals(firstBefore, library.getEntry(0).getBook().getTitle());
    }

    @Test
    void getAllEntries_returnsIndependentCopy() {
        ArrayList<BookEntry> copy = library.getAllEntries();
        int sizeBefore = library.getEntryCount();
        copy.clear();
        assertEquals(sizeBefore, library.getEntryCount());
    }

    // ---------------------------------------------------------------
    // Search (без змін логіки, залишаємо для регресії)
    // ---------------------------------------------------------------

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
        assertEquals(library.getEntryCount(),
                library.findByPriceRange(0.0, 100.0).size());
    }

    @Test
    void findByPriceRange_narrowRange_returnsOneEntry() {
        BookEntry entry = library.findByPriceRange(25.0, 30.0).get(0);
        assertInstanceOf(EBook.class, entry.getBook());
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

    // ---------------------------------------------------------------
    // Storage round-trip (без змін)
    // ---------------------------------------------------------------

    @Test
    void txtStorage_roundTrip_allTypesAndQuantities(@TempDir Path tempDir) {
        String path = tempDir.resolve("test.txt").toString();
        TxtBookStorage storage = new TxtBookStorage(path);

        Library original = createStorageLibrary();
        storage.save(original);

        Library loaded = new Library("Loaded", "Somewhere");
        storage.load(loaded);

        assertEquals(5, loaded.getEntryCount());
        assertInstanceOf(EBook.class,      loaded.getEntry(0).getBook());
        assertInstanceOf(EBook.class,      loaded.getEntry(1).getBook());
        assertInstanceOf(AudioBook.class,  loaded.getEntry(2).getBook());
        assertInstanceOf(PaperBook.class,  loaded.getEntry(3).getBook());
        assertInstanceOf(RareBook.class,   loaded.getEntry(4).getBook());
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
    void txtStorage_corruptedLine_skipsAndLoadsRest(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("corrupt.txt");
        Files.writeString(file,
                "EBOOK|Clean Code|Robert C. Martin|2008|39.99|PROGRAMMING|431|PDF|15.0|https://x.com|2\n"
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
        assertInstanceOf(EBook.class,      loaded.getEntry(0).getBook());
        assertInstanceOf(EBook.class,      loaded.getEntry(1).getBook());
        assertInstanceOf(AudioBook.class,  loaded.getEntry(2).getBook());
        assertInstanceOf(PaperBook.class,  loaded.getEntry(3).getBook());
        assertInstanceOf(RareBook.class,   loaded.getEntry(4).getBook());
        assertEquals(3, loaded.getEntry(2).getQuantity());
        assertEquals(5, loaded.getEntry(4).getQuantity());
    }

    @Test
    void jsonStorage_missingFile_leavesLibraryEmpty(@TempDir Path tempDir) {
        Library loaded = new Library("Loaded", "Somewhere");
        new JsonBookStorage(tempDir.resolve("nope.json").toString()).load(loaded);
        assertEquals(0, loaded.getEntryCount());
    }

    // ---------------------------------------------------------------
    // Фабрика для storage-тестів (без базового Book — він абстрактний)
    // ---------------------------------------------------------------

    private Library createStorageLibrary() {
        Library storageLibrary = new Library("Storage Library", "Archive St. 2");
        storageLibrary.addNewBook(new EBook("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431,
                "PDF", 15.0, "https://example.com/cleancode.pdf"), 2);
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

    // ---------------------------------------------------------------
    // Comparator — три критерії сортування
    // ---------------------------------------------------------------

    /** Компаратор 1: за назвою (A → Z). */
    private Comparator<BookEntry> titleComparator() {
        return (a, b) -> a.getBook().compareTo(b.getBook());
    }

    /** Компаратор 2: за ціною (зростання). */
    private Comparator<BookEntry> priceComparator() {
        return (a, b) -> Double.compare(a.getBook().getPrice(), b.getBook().getPrice());
    }

    /** Компаратор 3: за роком (спадання), вторинно за назвою. */
    private Comparator<BookEntry> yearComparator() {
        return (a, b) -> {
            int diff = b.getBook().getYear() - a.getBook().getYear();
            if (diff != 0) return diff;
            return a.getBook().compareTo(b.getBook());
        };
    }

    // --- title ---

    @Test
    void titleComparator_sixBooks_correctOrder() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, titleComparator());

        assertEquals("Clean Code",           sorted.get(0).getBook().getTitle());
        assertEquals("Design Patterns",      sorted.get(1).getBook().getTitle());
        assertEquals("Dune",                 sorted.get(2).getBook().getTitle());
        assertEquals("Moby Dick",            sorted.get(3).getBook().getTitle());
        assertEquals("Pragmatic Programmer", sorted.get(4).getBook().getTitle());
        assertEquals("The Clean Coder",      sorted.get(5).getBook().getTitle());
    }

    @Test
    void titleComparator_emptyList_noException() {
        ArrayList<BookEntry> empty = new ArrayList<BookEntry>();
        assertDoesNotThrow(() -> Collections.sort(empty, titleComparator()));
        assertTrue(empty.isEmpty());
    }

    @Test
    void titleComparator_singleElement_unchanged() {
        ArrayList<BookEntry> single = new ArrayList<BookEntry>();
        single.add(new BookEntry(cleanCode, 1));
        Collections.sort(single, titleComparator());
        assertEquals("Clean Code", single.get(0).getBook().getTitle());
    }

    // --- price ---

    @Test
    void priceComparator_sixBooks_cheapestFirst() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, priceComparator());

        // Dune $19.99 → Moby Dick $12.99 ... перевіряємо перший та останній
        double first = sorted.get(0).getBook().getPrice();
        double last  = sorted.get(sorted.size() - 1).getBook().getPrice();
        assertTrue(first <= last);

        // Повна перевірка порядку
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getBook().getPrice()
                    <= sorted.get(i + 1).getBook().getPrice());
        }
    }

    @Test
    void priceComparator_exactValues() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, priceComparator());

        // Найдешевша — Moby Dick $12.99, найдорожча — Design Patterns $54.99
        assertEquals("Moby Dick",        sorted.get(0).getBook().getTitle());
        assertEquals("Design Patterns",  sorted.get(sorted.size() - 1).getBook().getTitle());
    }

    @Test
    void priceComparator_emptyList_noException() {
        ArrayList<BookEntry> empty = new ArrayList<BookEntry>();
        assertDoesNotThrow(() -> Collections.sort(empty, priceComparator()));
    }

    // --- year ---

    @Test
    void yearComparator_sixBooks_newestFirst() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, yearComparator());

        // Перевіряємо, що роки не зростають
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getBook().getYear()
                    >= sorted.get(i + 1).getBook().getYear());
        }
    }

    @Test
    void yearComparator_exactOrder() {
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, yearComparator());

        // Найновіша — Pragmatic Programmer 2019, найстаріша — Moby Dick 1851
        assertEquals("Pragmatic Programmer", sorted.get(0).getBook().getTitle());
        assertEquals("Moby Dick",            sorted.get(sorted.size() - 1).getBook().getTitle());
    }

    @Test
    void yearComparator_sameYear_secondarySortByTitle() {
        Library sameYear = new Library("Same Year", "Nowhere");
        sameYear.addNewBook(new EBook("Zebra", "A", 2020, 10.0, Genre.FICTION, 100,
                "PDF", 1.0, "https://x.com"), 1);
        sameYear.addNewBook(new EBook("Alpha", "B", 2020, 20.0, Genre.FICTION, 200,
                "PDF", 1.0, "https://x.com"), 1);
        sameYear.addNewBook(new EBook("Mango", "C", 2020, 5.0, Genre.FICTION, 50,
                "PDF", 1.0, "https://x.com"), 1);

        ArrayList<BookEntry> sorted = sameYear.getAllEntries();
        Collections.sort(sorted, yearComparator());

        assertEquals("Alpha", sorted.get(0).getBook().getTitle());
        assertEquals("Mango", sorted.get(1).getBook().getTitle());
        assertEquals("Zebra", sorted.get(2).getBook().getTitle());
    }

    @Test
    void yearComparator_emptyList_noException() {
        ArrayList<BookEntry> empty = new ArrayList<BookEntry>();
        assertDoesNotThrow(() -> Collections.sort(empty, yearComparator()));
    }

    // --- незалежність бібліотеки ---

    @Test
    void anyComparator_doesNotModifyLibraryOrder() {
        String firstBefore = library.getEntry(0).getBook().getTitle();

        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, priceComparator());

        assertEquals(firstBefore, library.getEntry(0).getBook().getTitle());
    }

    // ---------------------------------------------------------------
    // reorderEntries
    // ---------------------------------------------------------------

    @Test
    void reorderEntries_reversesOrder() {
        ArrayList<BookEntry> reversed = library.getAllEntries();
        Collections.sort(reversed, (a, b) -> b.getBook().compareTo(a.getBook()));

        String expectedFirst = reversed.get(0).getBook().getTitle();
        library.reorderEntries(reversed);

        assertEquals(expectedFirst, library.getEntry(0).getBook().getTitle());
    }

    @Test
    void reorderEntries_persistsAfterSort() {
        // Сортуємо за ціною і зберігаємо
        ArrayList<BookEntry> byPrice = library.getAllEntries();
        Collections.sort(byPrice, (a, b) ->
                Double.compare(a.getBook().getPrice(), b.getBook().getPrice()));

        library.reorderEntries(byPrice);

        // Перевіряємо, що перший елемент — найдешевший
        double firstPrice = library.getEntry(0).getBook().getPrice();
        for (int i = 1; i < library.getEntryCount(); i++) {
            assertTrue(firstPrice <= library.getEntry(i).getBook().getPrice());
        }
    }

    @Test
    void reorderEntries_sizeMismatch_throws() {
        ArrayList<BookEntry> tooShort = new ArrayList<BookEntry>();
        tooShort.add(library.getEntry(0));

        assertThrows(InvalidBookDataException.class,
                () -> library.reorderEntries(tooShort));
    }

    @Test
    void reorderEntries_doesNotChangeTotalCount() {
        int before = library.getEntryCount();
        ArrayList<BookEntry> sorted = library.getAllEntries();
        Collections.sort(sorted, (a, b) -> a.getBook().compareTo(b.getBook()));
        library.reorderEntries(sorted);

        assertEquals(before, library.getEntryCount());
    }

    // ---------------------------------------------------------------
    // ObjectNotFoundException / DuplicateObjectException
    // ---------------------------------------------------------------

    @Test
    void delete_nonExistingBook_throwsObjectNotFoundException() {
        BookEntry notInLibrary = new BookEntry(
                new EBook("Ghost Book", "Ghost Author", 2020, 9.99,
                        Genre.FICTION, 100, "PDF", 1.0, "https://x.com"),
                1);

        assertThrows(ObjectNotFoundException.class, () -> library.delete(notInLibrary));
    }

    @Test
    void update_nonExistingBook_throwsObjectNotFoundException() {
        BookEntry notInLibrary = new BookEntry(
                new EBook("Ghost Book", "Ghost Author", 2020, 9.99,
                        Genre.FICTION, 100, "PDF", 1.0, "https://x.com"),
                1);

        assertThrows(ObjectNotFoundException.class,
                () -> library.update(notInLibrary, notInLibrary));
    }

    @Test
    void update_withDuplicateBook_throwsDuplicateObjectException() {
        Library lib = new Library("Dup Test", "Nowhere");

        EBook book1 = new EBook("Unique Title", "Some Author",
                2020, 10.0, Genre.FICTION, 100, "PDF", 1.0, "https://a.com");
        EBook book2 = new EBook("Other Title", "Other Author",
                2021, 20.0, Genre.SCIENCE, 200, "EPUB", 2.0, "https://b.com");

        lib.addNewBook(book1, 1);
        lib.addNewBook(book2, 1);

        // Змінюємо book2 щоб він став ідентичним book1 (за Book.equals + EBook.equals)
        BookEntry entry2 = lib.getEntry(1);
        EBook modBook = (EBook) entry2.getBook();
        modBook.setTitle("Unique Title");
        modBook.setAuthor("Some Author");
        modBook.setYear(2020);
        modBook.setPrice(10.0);
        modBook.setGenre(Genre.FICTION);
        modBook.setPages(100);
        modBook.setFileFormat("PDF");
        modBook.setFileSizeMB(1.0);
        modBook.setDownloadUrl("https://a.com");

        assertThrows(DuplicateObjectException.class, () -> lib.update(entry2, entry2));
    }

}