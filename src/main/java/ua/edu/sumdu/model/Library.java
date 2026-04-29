package ua.edu.sumdu.model;

import java.util.ArrayList;

/**
 * Бібліотека — клас-контейнер для зберігання книг з урахуванням кількості примірників.
 *
 * <p>Внутрішня колекція — {@code ArrayList<BookEntry>}, де кожен запис
 * містить об'єкт {@link Book} та кількість наявних примірників.</p>
 *
 * <p>Метод {@link #addNewBook(Book, int)} забезпечує семантику «додай або оновити»:
 * якщо книга вже є в колекції, кількість збільшується; інакше — створюється новий
 * {@link BookEntry}.</p>
 *
 * <p>Методи пошуку повертають новий {@code ArrayList<BookEntry>} і <b>ніколи
 * не змінюють</b> внутрішню колекцію.</p>
 */
public class Library {

    private String name;
    private String address;

    /** Внутрішня колекція записів (книга + кількість примірників). */
    private final ArrayList<BookEntry> entries;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Створює бібліотеку з вказаними назвою та адресою.
     *
     * @param name    назва бібліотеки; не може бути {@code null} або порожньою
     * @param address адреса бібліотеки; не може бути {@code null} або порожньою
     * @throws InvalidBookDataException якщо параметри некоректні
     */
    public Library(String name, String address) {
        setName(name);
        setAddress(address);
        this.entries = new ArrayList<BookEntry>();
    }

    // ---------------------------------------------------------------
    // Геттери та сетери
    // ---------------------------------------------------------------

    /**
     * Повертає назву бібліотеки.
     *
     * @return назва
     */
    public String getName() { return name; }

    /**
     * Встановлює назву бібліотеки.
     *
     * @param name назва; не може бути {@code null} або порожньою
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidBookDataException("Library name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Повертає адресу бібліотеки.
     *
     * @return адреса
     */
    public String getAddress() { return address; }

    /**
     * Встановлює адресу бібліотеки.
     *
     * @param address адреса; не може бути {@code null} або порожньою
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidBookDataException("Library address cannot be empty.");
        }
        this.address = address.trim();
    }

    // ---------------------------------------------------------------
    // Робота з колекцією
    // ---------------------------------------------------------------

    /**
     * Повертає загальну кількість унікальних книг у бібліотеці.
     *
     * @return розмір внутрішньої колекції
     */
    public int getEntryCount() { return entries.size(); }

    /**
     * Повертає запис за його індексом.
     *
     * @param index індекс (0-based)
     * @return {@link BookEntry} за вказаним індексом
     * @throws InvalidBookDataException якщо індекс виходить за межі
     */
    public BookEntry getEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (entries.size() - 1));
        }
        return entries.get(index);
    }

    /**
     * Додає книгу до бібліотеки або збільшує кількість, якщо вона вже є.
     *
     * <p>Два екземпляри {@link Book} вважаються однаковими, якщо повертають
     * {@code true} з метода {@link Book#equals(Object)}. У такому разі
     * кількість у вже існуючому записі збільшується на {@code quantity}.</p>
     *
     * @param bk       книга для додавання; не може бути {@code null}
     * @param quantity кількість примірників; має бути &gt; 0
     * @throws InvalidBookDataException якщо {@code bk} є {@code null}
     *                                  або {@code quantity} &le; 0
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
    // Методи пошуку (не змінюють колекцію)
    // ---------------------------------------------------------------

    /**
     * Знаходить усі записи, автор книги яких містить {@code author}
     * (порівняння без урахування регістру).
     *
     * @param author підрядок для пошуку; порожній або {@code null} → порожній результат
     * @return новий список знайдених записів (може бути порожнім)
     */
    public ArrayList<BookEntry> findByAuthor(String author) {
        ArrayList<BookEntry> result = new ArrayList<BookEntry>();
        if (author == null || author.isBlank()) {
            return result;
        }
        String target = author.trim().toLowerCase();
        for (int i = 0; i < entries.size(); i++) {
            BookEntry entry = entries.get(i);
            if (entry.getBook().getAuthor().toLowerCase().contains(target)) {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * Знаходить усі записи із вказаним жанром книги.
     *
     * @param genre жанр для пошуку; {@code null} → порожній результат
     * @return новий список знайдених записів (може бути порожнім)
     */
    public ArrayList<BookEntry> findByGenre(Genre genre) {
        ArrayList<BookEntry> result = new ArrayList<BookEntry>();
        if (genre == null) {
            return result;
        }
        for (int i = 0; i < entries.size(); i++) {
            BookEntry entry = entries.get(i);
            if (entry.getBook().getGenre() == genre) {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * Знаходить усі записи, ціна книги яких знаходиться у діапазоні
     * [{@code minPrice}, {@code maxPrice}] (включно).
     *
     * @param minPrice нижня межа ціни
     * @param maxPrice верхня межа ціни
     * @return новий список знайдених записів (порожній при {@code minPrice > maxPrice})
     */
    public ArrayList<BookEntry> findByPriceRange(double minPrice, double maxPrice) {
        ArrayList<BookEntry> result = new ArrayList<BookEntry>();
        if (minPrice > maxPrice) {
            return result;
        }
        for (int i = 0; i < entries.size(); i++) {
            BookEntry entry = entries.get(i);
            double price = entry.getBook().getPrice();
            if (price >= minPrice && price <= maxPrice) {
                result.add(entry);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    /**
     * Повертає форматований рядок із назвою, адресою та всіма записами.
     *
     * @return текстовий вигляд бібліотеки
     */
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