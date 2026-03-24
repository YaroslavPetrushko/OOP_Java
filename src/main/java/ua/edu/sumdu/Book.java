package ua.edu.sumdu;

import java.util.Objects;
import java.time.Year;

public class Book {
    private static final int MIN_YEAR = 1;

    private String title;
    private String author;
    private int year;
    private double price;
    private String genre;
    private int pages;

    public Book(String title, String author, int year, double price, String genre, int pages) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
        this.genre = genre;
        this.pages = pages;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidBookDataException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new InvalidBookDataException("Author cannot be empty.");
        }
        this.author = author.trim();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear) {
            throw new InvalidBookDataException(
                    "Year must be between " + MIN_YEAR + " and " + currentYear + ".");
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new InvalidBookDataException("Price cannot be negative.");
        }
        this.price = price;
    }

    public String getGenre() { return genre; }

    public void setGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            throw new InvalidBookDataException("Genre cannot be empty.");
        }
        this.genre = genre.trim();
    }

    public int getPages() { return pages; }

    public void setPages(int pages) {
        if (pages <= 0) {
            throw new InvalidBookDataException("Pages must be greater than zero.");
        }
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", genre='" + genre + '\'' +
                ", pages=" + pages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year &&
                Double.compare(price, book.price) == 0 &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(genre, book.genre)&&
                pages == book.pages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, price, genre, pages);
    }
}
