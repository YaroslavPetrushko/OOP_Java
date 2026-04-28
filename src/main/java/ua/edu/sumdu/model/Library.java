package ua.edu.sumdu.model;

import java.util.ArrayList;

/**
 * Бібліотека — клас-контейнер для зберігання книг з урахуванням кількості примірників.
 */
public class Library {

    private String name;
    private String address;

    // Внутрішня колекція записів (книга + кількість примірників).
    private final ArrayList<BookEntry> entries;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    public Library(String name, String address) {
        setName(name);
        setAddress(address);
        this.entries = new ArrayList<BookEntry>();
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
    // Робота з колекцією
    // ---------------------------------------------------------------

    public int getEntryCount() { return entries.size(); }

    public BookEntry getEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (entries.size() - 1));
        }
        return entries.get(index);
    }

    /**
     * Додає книгу до бібліотеки або збільшує кількість, якщо вона вже є.
     */
    public void addNewBook(Book bk, int quantity) {
        if (bk == null) {
            throw new InvalidBookDataException("Cannot add null book to library.");
        }
        if (quantity <= 0) {
            throw new InvalidBookDataException("Quantity must be greater than zero.");
        }

        // Шукаємо існуючий запис
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getBook().equals(bk)) {
                entries.get(i).addQuantity(quantity);
                return;
            }
        }

        // Якщо не знайдено — додаємо новий запис
        entries.add(new BookEntry(bk, quantity));
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Library{ name='").append(name)
                .append("', address='").append(address)
                .append("', entries=").append(entries.size()).append(" }\n");
        for (int i = 0; i < entries.size(); i++) {
            sb.append("  ").append(i + 1).append(". ")
                    .append(entries.get(i)).append("\n");
        }
        return sb.toString();
    }
}