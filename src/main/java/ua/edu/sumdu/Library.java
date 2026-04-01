package ua.edu.sumdu;

import java.util.ArrayList;

/**
 * Представляє бібліотеку — колекцію об'єктів Book. Клас демонструє принцип агрегації.
 */
public class Library {

    // ---------------------------------------------------------------
    // Поля
    // ---------------------------------------------------------------
    private String name;
    private String address;
    private final ArrayList<Book> books;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------
    public Library(String name, String address) {
        setName(name);
        setAddress(address);
        this.books = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidBookDataException("Library name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getAddress() { return address; }

    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidBookDataException("Library address cannot be empty.");
        }
        this.address = address.trim();
    }

    // ---------------------------------------------------------------
    // Робота з колекцією книг
    // ---------------------------------------------------------------

    public void addBook(Book book) {
        if (book == null) {
            throw new InvalidBookDataException("Cannot add null book to library.");
        }
        books.add(book);
    }

    public void removeBook(int index) {
        if (index < 0 || index >= books.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (books.size() - 1) + ".");
        }
        books.remove(index);
    }

    public Book getBook(int index) {
        if (index < 0 || index >= books.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (books.size() - 1) + ".");
        }
        return books.get(index);
    }

    public int getBookCount() {
        return books.size();
    }

    @Override
    public String toString() {
        return "Library{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", books=" + books +
                '}';
    }

}