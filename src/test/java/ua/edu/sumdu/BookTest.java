package ua.edu.sumdu;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Автотести для класів {@link Book}, {@link EBook} та {@link PaperBook}
 *
 * <p>Покривають:</p>
 * <ul>
 *   <li>валідацію конструкторів і сетерів базового класу;</li>
 *   <li>специфічну валідацію підкласів;</li>
 *   <li>конструктор копіювання для кожного класу;</li>
 *   <li>поліморфний вивід через {@code ArrayList<Book>};</li>
 *   <li>перевірку instanceof для коректності типів у колекції.</li>
 * </ul>
 */
class BookTest {

    /** Повертає коректний об'єкт {@link Book}. */
    private Book validBook() {
        return new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, Genre.PROGRAMMING, 431);
    }

    /** Повертає коректний об'єкт {@link EBook}. */
    private EBook validEBook() {
        return new EBook("The Pragmatic Programmer", "David Thomas",
                2019, 29.99, Genre.PROGRAMMING, 352,
                "EPUB", 4.5, "https://example.com/pragmatic.epub");
    }

    /** Повертає коректний об'єкт {@link PaperBook}. */
    private PaperBook validPaperBook() {
        return new PaperBook("Design Patterns", "GoF",
                1994, 54.99, Genre.PROGRAMMING, 395,
                "Addison-Wesley", 1, 730.0);
    }

    // ---------------------------------------------------------------
    // Book — базовий конструктор
    // ---------------------------------------------------------------

    @Test
    void book_validData_storesAllFields() {
        Book b = validBook();
        assertEquals("Clean Code",       b.getTitle());
        assertEquals("Robert C. Martin", b.getAuthor());
        assertEquals(2008,               b.getYear());
        assertEquals(39.99, b.getPrice(), 0.001);
        assertEquals(Genre.PROGRAMMING,  b.getGenre());
        assertEquals(431,                b.getPages());
    }

    @Test
    void book_emptyTitle_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("", "Author", 2000, 10.0, Genre.FICTION, 100));
    }

    @Test
    void book_negativePrice_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, -1.0, Genre.FICTION, 100));
    }

    @Test
    void book_zeroPagesCount_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, 10.0, Genre.FICTION, 0));
    }

    @Test
    void book_nullGenre_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2000, 10.0, null, 100));
    }

    @Test
    void book_futureYear_throwsException() {
        int futureYear = java.time.Year.now().getValue() + 10;
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", futureYear, 10.0, Genre.FICTION, 100));
    }

    // ---------------------------------------------------------------
    // Book — сетери
    // ---------------------------------------------------------------

    @Test
    void setTitle_blank_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook().setTitle("   "));
    }

    @Test
    void setPrice_negative_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook().setPrice(-0.01));
    }

    @Test
    void setPrice_zero_isAllowed() {
        Book b = validBook();
        assertDoesNotThrow(() -> b.setPrice(0.0));
        assertEquals(0.0, b.getPrice(), 0.001);
    }

    @Test
    void setPages_negative_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook().setPages(-1));
    }

    // ---------------------------------------------------------------
    // Book — конструктор копіювання
    // ---------------------------------------------------------------

    @Test
    void book_copyConstructor_equalFields() {
        Book original = validBook();
        Book copy = new Book(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    void book_copyConstructor_mutatingCopy_doesNotAffectOriginal() {
        Book original = validBook();
        Book copy = new Book(original);
        copy.setTitle("Other Title");
        assertEquals("Clean Code", original.getTitle());
    }

    @Test
    void book_copyConstructor_null_throwsException() {
        assertThrows(InvalidBookDataException.class, () -> new Book((Book) null));
    }

    // ---------------------------------------------------------------
    // EBook — конструктор та валідація
    // ---------------------------------------------------------------

    @Test
    void eBook_validData_storesExtraFields() {
        EBook e = validEBook();
        assertEquals("EPUB", e.getFileFormat());
        assertEquals(4.5,    e.getFileSizeMB(), 0.001);
        assertEquals("https://example.com/pragmatic.epub", e.getDownloadUrl());
    }

    @Test
    void eBook_emptyFileFormat_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "", 2.0, "https://x.com"));
    }

    @Test
    void eBook_zeroFileSize_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "PDF", 0.0, "https://x.com"));
    }

    @Test
    void eBook_emptyDownloadUrl_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new EBook("T", "A", 2020, 5.0, Genre.FICTION, 100,
                        "PDF", 2.0, ""));
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

    // ---------------------------------------------------------------
    // PaperBook — конструктор та валідація
    // ---------------------------------------------------------------

    @Test
    void paperBook_validData_storesExtraFields() {
        PaperBook pb = validPaperBook();
        assertEquals("Addison-Wesley", pb.getPublisher());
        assertEquals(1,     pb.getEdition());
        assertEquals(730.0, pb.getWeightGrams(), 0.001);
    }

    @Test
    void paperBook_emptyPublisher_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "", 1, 300.0));
    }

    @Test
    void paperBook_zeroEdition_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "Publisher", 0, 300.0));
    }

    @Test
    void paperBook_negativeWeight_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new PaperBook("T", "A", 2000, 10.0, Genre.FICTION, 100,
                        "Publisher", 1, -50.0));
    }

    @Test
    void paperBook_copyConstructor_equalAndIndependent() {
        PaperBook original = validPaperBook();
        PaperBook copy = new PaperBook(original);
        assertEquals(original, copy);
        assertNotSame(original, copy);
        copy.setPublisher("Other Publisher");
        assertEquals("Addison-Wesley", original.getPublisher());
    }

    // ---------------------------------------------------------------
    // Поліморфізм — ArrayList<Book>
    // ---------------------------------------------------------------

    /**
     * Перевіряє, що об'єкти всіх трьох типів можна зберігати
     * в одній колекції {@code ArrayList<Book>} та отримувати через
     * посилання базового типу.
     */
    @Test
    void polymorphism_allTypesInOneCollection() {
        ArrayList<Book> list = new ArrayList<>();
        list.add(validBook());
        list.add(validEBook());
        list.add(validPaperBook());

        assertEquals(3, list.size());
        assertInstanceOf(Book.class,      list.get(0));
        assertInstanceOf(EBook.class,     list.get(1));
        assertInstanceOf(PaperBook.class, list.get(2));
    }

    /**
     * Перевіряє, що {@code toString()} викликається відповідно до реального
     * типу об'єкта (динамічна диспетчеризація).
     */
    @Test
    void polymorphism_toStringDispatchedByRealType() {
        ArrayList<Book> list = new ArrayList<>();
        list.add(validBook());
        list.add(validEBook());
        list.add(validPaperBook());

        assertTrue(list.get(0).toString().startsWith("[Book]"));
        assertTrue(list.get(1).toString().startsWith("[EBook]"));
        assertTrue(list.get(2).toString().startsWith("[PaperBook]"));
    }
}