package ua.edu.sumdu;

import java.util.ArrayList;

/**
 * Представляє бібліотеку — колекцію об'єктів {@link Book}.
 *
 * <p>Клас демонструє принцип <b>агрегації</b>: {@code Library} містить
 * список {@link Book}, але не є їхнім власником у сенсі компози­ції —
 * книги можуть існувати незалежно від бібліотеки.</p>
 *
 * <p>Основні можливості:</p>
 * <ul>
 *   <li>додавання та видалення книг;</li>
 *   <li>виведення всіх книг у форматованому вигляді.</li>
 * </ul>
 */
public class Library {

    // ---------------------------------------------------------------
    // Поля
    // ---------------------------------------------------------------
    /** Назва бібліотеки. */
    private String name;

    /** Адреса або місце розташування бібліотеки. */
    private String address;

    /**
     * Колекція книг, що зберігається в бібліотеці.
     * Тип {@link ArrayList} обрано для зручного динамічного додавання/видалення.
     */
    private final ArrayList<Book> books;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Створює нову бібліотеку з вказаними назвою та адресою.
     *
     * @param name    назва бібліотеки; не може бути {@code null} або порожнім
     * @param address адреса бібліотеки; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо параметри некоректні
     */
    public Library(String name, String address) {
        setName(name);
        setAddress(address);
        this.books = new ArrayList<>();
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
     * @param name назва; не може бути {@code null} або порожнім
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
     * @param address адреса; не може бути {@code null} або порожнім
     * @throws InvalidBookDataException якщо значення некоректне
     */
    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidBookDataException("Library address cannot be empty.");
        }
        this.address = address.trim();
    }

    // ---------------------------------------------------------------
    // Робота з колекцією книг
    // ---------------------------------------------------------------

    /**
     * Додає книгу до колекції бібліотеки.
     *
     * @param book книга для додавання; не може бути {@code null}
     * @throws InvalidBookDataException якщо {@code book} є {@code null}
     */
    public void addBook(Book book) {
        if (book == null) {
            throw new InvalidBookDataException("Cannot add null book to library.");
        }
        books.add(book);
    }

    /**
     * Видаляє книгу з колекції за її індексом.
     *
     * @param index індекс книги (0-based)
     * @throws InvalidBookDataException якщо індекс поза межами колекції
     */
    public void removeBook(int index) {
        if (index < 0 || index >= books.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (books.size() - 1) + ".");
        }
        books.remove(index);
    }

    /**
     * Повертає книгу за її індексом у колекції.
     *
     * @param index індекс книги (0-based)
     * @return об'єкт {@link Book}
     * @throws InvalidBookDataException якщо індекс поза межами колекції
     */
    public Book getBook(int index) {
        if (index < 0 || index >= books.size()) {
            throw new InvalidBookDataException(
                    "Invalid index: " + index + ". Valid range: 0–" + (books.size() - 1) + ".");
        }
        return books.get(index);
    }

    /**
     * Повертає поточну кількість книг у бібліотеці.
     *
     * @return кількість книг (≥ 0)
     */
    public int getBookCount() {
        return books.size();
    }

    // ---------------------------------------------------------------
    // Object overrides
    // ---------------------------------------------------------------

    /**
     * Повертає рядкове представлення бібліотеки з усіма книгами.
     *
     * @return форматований рядок
     */
    @Override
    public String toString() {
        return "Library{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", books=" + books +
                '}';
    }

}