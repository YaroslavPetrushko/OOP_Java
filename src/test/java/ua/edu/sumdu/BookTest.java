package ua.edu.sumdu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book("Clean Code", "Robert C. Martin",
                2008, 39.99, "Programming", 431);
    }

    @Test
    void constructor_validData_createsObject() {
        assertNotNull(validBook);
        assertEquals("Clean Code", validBook.getTitle());
        assertEquals("Robert C. Martin", validBook.getAuthor());
        assertEquals(2008, validBook.getYear());
        assertEquals(39.99, validBook.getPrice(), 0.001);
        assertEquals("Programming", validBook.getGenre());
        assertEquals(431, validBook.getPages());
    }

    @Test
    void constructor_emptyTitle_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("", "Author", 2020, 10.0, "Fiction", 200));
    }

    @Test
    void constructor_nullAuthor_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", null, 2020, 10.0, "Fiction", 200));
    }

    @Test
    void constructor_negativePrice_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2020, -5.0, "Fiction", 200));
    }

    @Test
    void constructor_zeroPagesCount_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", 2020, 10.0, "Fiction", 0));
    }


    @Test
    void constructor_futureYear_throwsException() {
        int futureYear = java.time.Year.now().getValue() + 100;
        assertThrows(InvalidBookDataException.class, () ->
                new Book("Title", "Author", futureYear, 10.0, "Fiction", 100));
    }

    // ---------------------------------------------------------------
    // Тести сетерів
    // ---------------------------------------------------------------

    @Test
    void setTitle_blankString_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setTitle("   "));
    }

    @Test
    void setAuthor_emptyString_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setAuthor(""));
    }

    @Test
    void setYear_negativeValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setYear(-1));
    }

    @Test
    void setPrice_negativeValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setPrice(-0.01));
    }

    @Test
    void setPrice_zeroValue_isAllowed() {
        assertDoesNotThrow(() -> validBook.setPrice(0.0));
        assertEquals(0.0, validBook.getPrice(), 0.001);
    }

    @Test
    void setGenre_nullValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setGenre(null));
    }

    @Test
    void setPages_negativeValue_throwsException() {
        assertThrows(InvalidBookDataException.class, () ->
                validBook.setPages(-10));
    }

    @Test
    void setTitle_validValue_updatesField() {
        validBook.setTitle("The Pragmatic Programmer");
        assertEquals("The Pragmatic Programmer", validBook.getTitle());
    }
}