package ua.edu.sumdu;

import java.util.Objects;

public class Book {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGenre() {return genre;}

    public void setGenre(String genre) {this.genre = genre;}

    public int getPages() {return pages;}

    public void setPages(int pages) {this.pages = pages;}

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
