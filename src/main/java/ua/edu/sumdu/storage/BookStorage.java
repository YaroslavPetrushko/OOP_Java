package ua.edu.sumdu.storage;

import ua.edu.sumdu.model.Book;

import java.util.ArrayList;

/**
 * Інтерфейс для збереження та завантаження колекції книг.
 */
public interface BookStorage {

    // Завантажує книги з джерела та повертає їх у вигляді колекції.
    ArrayList<Book> load();

    // Записує поточний вміст колекції до відповідного сховища.
    void save(ArrayList<Book> books);
}