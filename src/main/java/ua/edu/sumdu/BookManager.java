package ua.edu.sumdu;

import ua.edu.sumdu.model.*;
import ua.edu.sumdu.storage.BookStorage;
import ua.edu.sumdu.storage.TxtBookStorage;
import ua.edu.sumdu.storage.JsonBookStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Контролер програми «Book Manager».
 *
 * <p>Делегує зберігання та пошук до екземпляра {@link Library}.
 * Відповідає виключно за взаємодію з користувачем, введення даних
 * та координацію завантаження/збереження через {@link BookStorage}.</p>
 *
 * <p>Головне меню:</p>
 * <ol>
 *   <li>Пошук об'єкта</li>
 *   <li>Створити новий об'єкт</li>
 *   <li>Вивести всі об'єкти</li>
 *   <li>Завершити роботу</li>
 * </ol>
 *
 * <p>Ієрархія підтримуваних класів:</p>
 * <pre>
 * Book
 * ├── EBook
 * ├── AudioBook
 * └── PaperBook
 *     └── RareBook
 * </pre>
 *
 * <p>На старті завантажує дані з {@code input.txt} або {@code input.json}.
 * При завершенні зберігає актуальний стан до обох файлів.</p>
 */
public class BookManager {

    // ---------------------------------------------------------------
    // Константи — шляхи до файлів
    // ---------------------------------------------------------------

    /** Шлях до текстового файлу зберігання. */
    private static final String TXT_FILE  = "input.txt";

    /** Шлях до JSON-файлу зберігання (альтернативний варіант). */
    private static final String JSON_FILE = "input.json";

    // ---------------------------------------------------------------
    // Поля
    // ---------------------------------------------------------------

    /** Єдина колекція для об'єктів усієї ієрархії. */
    private final Library library;

    /** Сховище у форматі текстового файлу. */
    private final BookStorage txtStorage;

    /** Сховище у форматі JSON. */
    private final BookStorage jsonStorage;

    /** Спільний Scanner для всієї програми. */
    private final Scanner scanner;

    // ---------------------------------------------------------------
    // Конструктор
    // ---------------------------------------------------------------

    /**
     * Ініціалізує контролер: створює бібліотеку з іменем/адресою за замовчуванням
     * та обидва сховища.
     */
    public BookManager() {
        this.library     = new Library("City Library", "Main St. 1");
        this.txtStorage  = new TxtBookStorage(TXT_FILE);
        this.jsonStorage = new JsonBookStorage(JSON_FILE);
        this.scanner     = new Scanner(System.in);
    }

    // ---------------------------------------------------------------
    // Точка входу в контролер
    // ---------------------------------------------------------------

    /**
     * Запускає головний цикл програми:
     * завантажує дані → цикл меню → зберігає дані → закриває сканер.
     */
    public void run() {
        printBanner();
        loadBooks();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> searchMenu();
                case 2 -> createObject();
                case 3 -> modifyBook();
                case 4 -> deleteBook();
                case 5 -> printAllBooks();
                case 6 -> sortBooks();
                case 7 -> {
                    saveBooks();
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("  [!] Please enter 1, 2 or 3.\n");
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Операції при ініціації програми
    // ---------------------------------------------------------------

    /**
     * Виводить інформаційну шапку програми.
     */
    private void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║            BOOK MANAGER  v11.0           ║");
        System.out.println("║Library | Modify+Delete | TXT+JSON storage║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /**
     * Завантажує книги з файлів при старті програми.
     *
     * <p>Спочатку завантажує {@code input.txt}; якщо той порожній —
     * намагається завантажити {@code input.json}. Завдяки цьому обидва
     * формати залишаються актуальними після збереження.</p>
     */
    private void loadBooks() {
        System.out.println("\n--- Loading data ---");
        txtStorage.load(library);
        if (library.getEntryCount() == 0) {
            jsonStorage.load(library);
        }
        System.out.println("  Library \"" + library.getName()
                + "\": " + library.getEntryCount() + " unique title(s) on start.\n");
    }

    /**
     * Зберігає поточний стан колекції до обох форматів при виході.
     */
    private void saveBooks() {
        System.out.println("\n--- Saving data ---");
        txtStorage.save(library);
        jsonStorage.save(library);
    }

    // ---------------------------------------------------------------
    // Головне меню
    // ---------------------------------------------------------------

    /**
     * Виводить пункти головного меню.
     */
    private void printMainMenu() {
        System.out.println("==========================================");
        System.out.println("1. Search book");
        System.out.println("2. Create new book");
        System.out.println("3. Modify book");
        System.out.println("4. Delete book");
        System.out.println("5. Show all books");
        System.out.println("6. Sort books");
        System.out.println("7. Exit");
        System.out.print("Your choice: ");
    }

    /**
     * Зчитує вибір пункту меню (ціле число).
     * Якщо введено нечислове значення — повертає {@code -1}.
     *
     * @return вибраний пункт меню або {@code -1} при помилці введення
     */
    private int readMenuChoice() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    // ---------------------------------------------------------------
    // Пункт 1: Пошук книги
    // ---------------------------------------------------------------

    /**
     * Підменю вибору критерію пошуку. {@code 0} — повернення до меню.
     */
    private void searchMenu() {
            System.out.println("\n--- Search ---");
            System.out.println("  1. By author");
            System.out.println("  2. By genre");
            System.out.println("  3. By price range");
            System.out.println("  0. Back to main menu");
            System.out.print("Criterion: ");

            int choice = readMenuChoice();
            System.out.println();

            switch (choice) {
                case 1 -> searchByAuthor();
                case 2 -> searchByGenre();
                case 3 -> searchByPriceRange();
                case 0 -> System.out.println("  Cancelled.\n");
                default -> System.out.println("  [!] Unknown criterion.\n");
            }
    }

    private void searchByAuthor() {
        String author = readNonEmptyString("Author name: ");
        ArrayList<BookEntry> result = library.findByAuthor(author);
        printSearchResult(result, "author contains \"" + author + "\"");
    }

    private void searchByGenre() {
        Genre genre = readEnum(Genre.values(), "Genre");
        System.out.println();
        ArrayList<BookEntry> result = library.findByGenre(genre);
        printSearchResult(result, "genre = " + genre);
    }

    private void searchByPriceRange() {
        double minPrice = readDouble("Min price ($): ");
        double maxPrice = readDouble("Max price ($): ");
        ArrayList<BookEntry> result = library.findByPriceRange(minPrice, maxPrice);
        printSearchResult(result,
                "price in [$" + String.format("%.2f", minPrice)
                        + " .. $" + String.format("%.2f", maxPrice) + "]");
    }

    /**
     * Виводить результати пошуку або повідомлення про відсутність збігів.
     * Показує книгу та кількість примірників.
     *
     * @param result    список знайдених записів
     * @param criterion текстовий опис критерію
     */
    private void printSearchResult(ArrayList<BookEntry> result, String criterion) {
        System.out.println("--- Search results [" + criterion + "] ---");
        if (result.isEmpty()) {
            System.out.println("  No objects found matching the given criterion.\n");
            return;
        }
        System.out.println("  Found " + result.size() + " record(s):");
        for (int i = 0; i < result.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + result.get(i));
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Пункт 2: Створення нової книги
    // Прибрано Book base
    // ---------------------------------------------------------------

    /**
     * Показує підменю вибору типу об'єкта та делегує до відповідного
     * методу створення. Пункт {@code 0} дозволяє повернутись до головного
     * меню без створення об'єкта.
     */
    private void createObject() {
        System.out.println("\n--- Select type ---");
        System.out.println("  1. EBook");
        System.out.println("  2. Audio Book");
        System.out.println("  3. Paper Book");
        System.out.println("  4. Rare Book");
        System.out.println("  0. Back to main menu");
        System.out.print("Type: ");

        int type = readMenuChoice();
        System.out.println();

        switch (type) {
            case 1 -> createEBook();
            case 2 -> createAudioBook();
            case 3 -> createPaperBook();
            case 4 -> createRareBook();
            case 0 -> System.out.println("  Cancelled.\n");
            default -> System.out.println("  [!] Unknown type. Returning to main menu.\n");
        }
    }

    // ---------------------------------------------------------------
    // Методи створення конкретних книг
    // ---------------------------------------------------------------

    /**
     * Зчитує дані для {@link EBook} та додає її до колекції.
     */
    private void createEBook() {
        System.out.println("--- Add EBook ---");
        try {
            String title        = readNonEmptyString("Title:    ");
            String author       = readNonEmptyString("Author:   ");
            int    year         = readInt("Year:     ");
            double price        = readDouble("Price:    ");
            Genre  genre        = readEnum(Genre.values(),"Genre");
            int    pages        = readInt("Pages:  ");
            String fileFormat   = readNonEmptyString("File format (EPUB/PDF/MOBI): ");
            double fileSizeMB   = readDouble("File size (MB):  ");
            String downloadUrl  = readNonEmptyString("Download URL: ");
            int    quantity    = readInt("Quantity:  ");

            library.addNewBook(new EBook(title, author, year, price, genre, pages,
                    fileFormat, fileSizeMB, downloadUrl), quantity);
            System.out.println("  [OK] EBook added. Library size: " + library.getEntryCount() + "\n");

        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link AudioBook} та додає її до колекції.
     */
    private void createAudioBook() {
        System.out.println("--- Add AudioBook ---");
        try {
            String title            = readNonEmptyString("Title:    ");
            String author           = readNonEmptyString("Author:   ");
            int    year             = readInt("Year:     ");
            double price            = readDouble("Price:    ");
            Genre  genre            = readEnum(Genre.values(), "Genre");
            int    pages            = readInt("Pages (original):    ");
            String narrator         = readNonEmptyString("Narrator:     ");
            int    durationMinutes  = readInt("Duration (minutes):  ");
            String audioFormat      = readNonEmptyString("Audio format (MP3/AAC/FLAC): ");
            int    quantity        = readInt("Quantity:  ");

            library.addNewBook(new AudioBook(title, author, year, price, genre, pages,
                    narrator, durationMinutes, audioFormat), quantity);
            System.out.println("  [OK] AudioBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link PaperBook} та додає її до колекції.
     */
    private void createPaperBook() {
        System.out.println("--- Add PaperBook ---");
        try {
            String title       = readNonEmptyString("Title:     ");
            String author      = readNonEmptyString("Author:    ");
            int    year        = readInt("Year:      ");
            double price       = readDouble("Price:     ");
            Genre  genre       = readEnum(Genre.values(), "Genre");
            int    pages       = readInt("Pages:    ");
            String publisher   = readNonEmptyString("Publisher:  ");
            int    edition     = readInt("Edition:    ");
            double weightGrams = readDouble("Weight (g): ");
            int    quantity    = readInt("Quantity:  ");

            library.addNewBook(new PaperBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams), quantity);
            System.out.println("  [OK] PaperBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Зчитує дані для {@link RareBook} та додає її до колекції.
     */
    private void createRareBook() {
        System.out.println("--- Add RareBook ---");
        try {
            String        title             = readNonEmptyString("Title:    ");
            String        author            = readNonEmptyString("Author:   ");
            int           year              = readInt("Year:     ");
            double        price             = readDouble("Price:    ");
            Genre         genre             = readEnum(Genre.values(), "Genre:  ");
            int           pages             = readInt("Pages:   ");
            String        publisher         = readNonEmptyString("Publisher: ");
            int           edition           = readInt("Edition:   ");
            double        weightGrams       = readDouble("Weight (g): ");
            BookCondition condition         = readEnum(BookCondition.values(), "Condition");
            double        estimatedValueUSD = readDouble("Estimated value ($):  ");
            int           acquisitionYear   = readInt("Acquisition year:     ");
            int           quantity          = readInt("Quantity:  ");

            library.addNewBook(new RareBook(title, author, year, price, genre, pages,
                    publisher, edition, weightGrams,
                    condition, estimatedValueUSD, acquisitionYear), quantity);
            System.out.println("  [OK] RareBook added. Library size: " + library.getEntryCount() + "\n");
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 3: Модифікація книги
    // ---------------------------------------------------------------

    /**
     * Показує список книг, дає вибрати одну, потім — атрибут для зміни.
     * Після введення нового значення викликає {@link Library#update}.
     */
    private void modifyBook() {
        System.out.println("\n--- Modify book ---");
        if (library.getEntryCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }
        printAllBooks();

        int idx = readInt("Select book number: ") - 1;
        if (idx < 0 || idx >= library.getEntryCount()) {
            System.out.println("  [!] Invalid number.\n");
            return;
        }

        BookEntry entry = library.getEntry(idx);
        Book book = entry.getBook();

        System.out.println("  Selected: \"" + book.getTitle()+"\" by "+ book.getAuthor());
        System.out.println("  Attributes:");
        System.out.println("    1. Title");
        System.out.println("    2. Author");
        System.out.println("    3. Year");
        System.out.println("    4. Price");
        System.out.println("    5. Genre");
        System.out.println("    6. Pages");
        System.out.println("    7. Quantity");

        if (book instanceof EBook) {
            System.out.println("    8. File format");
            System.out.println("    9. File size (MB)");
            System.out.println("   10. Download URL");
        } else if (book instanceof AudioBook) {
            System.out.println("    8. Narrator");
            System.out.println("    9. Duration (minutes)");
            System.out.println("   10. Audio format");
        } else if (book instanceof RareBook) {
            System.out.println("    8. Publisher");
            System.out.println("    9. Edition");
            System.out.println("   10. Weight (g)");
            System.out.println("   11. Condition");
            System.out.println("   12. Estimated value ($)");
            System.out.println("   13. Acquisition year");
        } else if (book instanceof PaperBook) {
            System.out.println("    8. Publisher");
            System.out.println("    9. Edition");
            System.out.println("   10. Weight (g)");
        }
        System.out.println("    0. Cancel");

        int attr = readInt("  Attribute: ");
        if (attr == 0) {
            System.out.println("  Cancelled.\n");
            return;
        }

        try {
            boolean changed = applyModification(entry, book, attr);
            if (!changed) {
                System.out.println("  [!] Unknown attribute for this book type.\n");
                return;
            }
            boolean result = library.update(entry, entry);
            if (result) {
                System.out.println("  [OK] Book updated.\n");
            } else {
                System.out.println("  [!] Book not found in library.\n");
            }
        } catch (InvalidBookDataException e) {
            System.out.println("  [!] " + e.getMessage() + "\n");
        }
    }

    /**
     * Застосовує зміну атрибута до книги або запису за обраним номером.
     * Спочатку перевіряє загальні поля (1–7), потім делегує до методу підтипу.
     *
     * @param entry запис (для зміни quantity)
     * @param book  книга (для зміни решти полів)
     * @param attr  номер обраного атрибута
     * @return {@code true} якщо атрибут розпізнано і змінено
     */
    private boolean applyModification(BookEntry entry, Book book, int attr) {
        if (attr == 1) { book.setTitle(readNonEmptyString("New title:  "));      return true; }
        if (attr == 2) { book.setAuthor(readNonEmptyString("New author: "));     return true; }
        if (attr == 3) { book.setYear(readInt("New year:   "));                  return true; }
        if (attr == 4) { book.setPrice(readDouble("New price ($): "));           return true; }
        if (attr == 5) { book.setGenre(readEnum(Genre.values(), "New genre"));   return true; }
        if (attr == 6) { book.setPages(readInt("New pages:  "));                 return true; }
        if (attr == 7) { entry.setQuantity(readInt("New quantity: "));           return true; }

        // Специфічні поля — порядок важливий: RareBook перед PaperBook
        if (book instanceof RareBook  rb) return applyRareBookModification(rb,  attr);
        if (book instanceof PaperBook pb) return applyPaperBookModification(pb, attr);
        if (book instanceof EBook     eb) return applyEBookModification(eb,     attr);
        if (book instanceof AudioBook ab) return applyAudioBookModification(ab, attr);
        return false;
    }

    private boolean applyEBookModification(EBook eb, int attr) {
        if (attr == 8)  { eb.setFileFormat(readNonEmptyString("New file format: "));      return true; }
        if (attr == 9)  { eb.setFileSizeMB(readDouble("New file size (MB): "));           return true; }
        if (attr == 10) { eb.setDownloadUrl(readNonEmptyString("New download URL: "));    return true; }
        return false;
    }

    private boolean applyAudioBookModification(AudioBook ab, int attr) {
        if (attr == 8)  { ab.setNarrator(readNonEmptyString("New narrator: "));           return true; }
        if (attr == 9)  { ab.setDurationMinutes(readInt("New duration (minutes): "));     return true; }
        if (attr == 10) { ab.setAudioFormat(readNonEmptyString("New audio format: "));    return true; }
        return false;
    }

    private boolean applyPaperBookModification(PaperBook pb, int attr) {
        if (attr == 8)  { pb.setPublisher(readNonEmptyString("New publisher: "));         return true; }
        if (attr == 9)  { pb.setEdition(readInt("New edition: "));                        return true; }
        if (attr == 10) { pb.setWeightGrams(readDouble("New weight (g): "));              return true; }
        return false;
    }

    private boolean applyRareBookModification(RareBook rb, int attr) {
        if (attr == 8)  { rb.setPublisher(readNonEmptyString("New publisher: "));         return true; }
        if (attr == 9)  { rb.setEdition(readInt("New edition: "));                        return true; }
        if (attr == 10) { rb.setWeightGrams(readDouble("New weight (g): "));              return true; }
        if (attr == 11) { rb.setCondition(readEnum(BookCondition.values(), "New condition")); return true; }
        if (attr == 12) { rb.setEstimatedValueUSD(readDouble("New estimated value ($): ")); return true; }
        if (attr == 13) { rb.setAcquisitionYear(readInt("New acquisition year: "));       return true; }
        return false;
    }

    // ---------------------------------------------------------------
    // Пункт 4: Видалення книги
    // ---------------------------------------------------------------

    /**
     * Показує список книг, дозволяє вибрати одну за номером,
     * запитує підтвердження та викликає {@link Library#delete}.
     */
    private void deleteBook() {
        System.out.println("\n--- Delete book ---");
        if (library.getEntryCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }
        printAllBooks();

        int idx = readInt("Select book number to delete: ") - 1;
        if (idx < 0 || idx >= library.getEntryCount()) {
            System.out.println("  [!] Invalid number.\n");
            return;
        }

        BookEntry entry = library.getEntry(idx);
        System.out.println("  Selected: \"" + entry.getBook().getTitle()+"\" by "+ entry.getBook().getAuthor());
        System.out.print("  Confirm deletion (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("  Cancelled.\n");
            return;
        }

        boolean result = library.delete(entry);
        if (result) {
            System.out.println("  [OK] Book deleted. Library size: " + library.getEntryCount() + "\n");
        } else {
            System.out.println("  [!] Book not found in library.\n");
        }
    }

    // ---------------------------------------------------------------
    // Пункт 5: Виведення всіх книг
    // ---------------------------------------------------------------

    /**
     * Виводить усі записи бібліотеки (книга + кількість примірників)
     * через посилання базового типу {@link Book}.
     *
     * <p>Демонстрація поліморфізму: метод {@code toString()} викликається
     * відповідно до реального типу кожного об'єкта.</p>
     * Якщо список порожній — повідомляє про це.
     */
    private void printAllBooks() {
        System.out.println("\n--- Library: " + library.getName()
                + " [" + library.getEntryCount() + " title(s)] ---");
        if (library.getEntryCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }
        for (int i = 0; i < library.getEntryCount(); i++) {
            BookEntry entry = library.getEntry(i);
            System.out.println("  " + (i + 1) + ". " + entry);
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Пункт 6: Меню сортування та виведення відсортованих книг
    // ---------------------------------------------------------------

    /**
     * Виводить підменю вибору критерію сортування.
     *
     * <p>Для кожного критерію створюється анонімний внутрішній клас,
     * що реалізує {@link java.util.Comparator}. Лямбда-вирази не використовуються.</p>
     *
     * <p>Критерії:</p>
     * <ol>
     *   <li>За назвою (A→Z, без урахування регістру) — делегує до {@link Book#compareTo}</li>
     *   <li>За ціною (від найдешевшої до найдорожчої)</li>
     *   <li>За роком видання (від найновішої до найстарішої)</li>
     * </ol>
     *
     * <p>Пункт {@code 0} повертає до головного меню без сортування.</p>
     */
    private void sortBooks() {
        System.out.println("\n--- Sort books ---");
        System.out.println("  1. Sort by title        (A → Z)");
        System.out.println("  2. Sort by price        (cheapest first)");
        System.out.println("  3. Sort by release year (newest first)");
        System.out.println("  0. Back to main menu");
        System.out.print("Criterion: ");

        int choice = readMenuChoice();
        System.out.println();

        if (choice == 0) {
            System.out.println("  Cancelled.\n");
            return;
        }

        if (choice < 1 || choice > 3) {
            System.out.println("  [!] Unknown criterion.\n");
            return;
        }

        if (library.getEntryCount() == 0) {
            System.out.println("  (library is empty)\n");
            return;
        }

        ArrayList<BookEntry> sorted = library.getAllEntries();

        switch (choice) {
            case 1 -> sortAndPrint(sorted, buildTitleComparator(),  "title (A → Z)");
            case 2 -> sortAndPrint(sorted, buildPriceComparator(),  "price (cheapest first)");
            case 3 -> sortAndPrint(sorted, buildYearComparator(),   "year (newest first)");
        }

        saveSortedBooks(sorted);
    }

    // ---------------------------------------------------------------
    // Анонімні компаратори
    // ---------------------------------------------------------------

    /**
     * Компаратор 1: за назвою книги (лексикографічно, без урахування регістру).
     * Реалізовано як лямбда-вираз.
     * <p>Делегує порівняння до {@link Book#compareTo(Book)}, що реалізує
     * {@link Comparable} у батьківському класі.</p>
     *
     * @return анонімний {@code Comparator<BookEntry>}
     */
    private Comparator<BookEntry> buildTitleComparator() {
        return (a, b) -> a.getBook().compareTo(b.getBook());
    }

    /**
     * Компаратор 2: за ціною (зростання).
     * Реалізовано як лямбда-вираз.
     * <p>Використовує {@link Double#compare} для коректного порівняння
     * дійсних чисел без похибок рухомої крапки.</p>
     *
     * @return анонімний {@code Comparator<BookEntry>}
     */
    private Comparator<BookEntry> buildPriceComparator() {
        return (a, b) -> Double.compare(a.getBook().getPrice(), b.getBook().getPrice());
    }

    /**
     * Компаратор 3: за роком видання (спадання — від найновішої до найстарішої).
     * Реалізовано як лямбда-вираз.
     * <p>При однаковому році вторинним критерієм слугує назва книги,
     * що робить сортування стабільним і однозначним.</p>
     *
     * @return анонімний {@code Comparator<BookEntry>}
     */
    private Comparator<BookEntry> buildYearComparator() {
        return (a, b) -> {
            int diff = b.getBook().getYear() - a.getBook().getYear();
            if (diff != 0) return diff;
            return a.getBook().compareTo(b.getBook());
        };
    }

    /**
     * Сортує список за переданим компаратором і виводить результат у консоль.
     *
     * <p>Отримує незалежну копію від {@link Library#getAllEntries()},
     * тому внутрішній порядок бібліотеки залишається незмінним.</p>
     *
     * @param entries   список для сортування (копія, не оригінал)
     * @param cmp       компаратор ({@link java.util.Comparator}{@code <BookEntry>})
     * @param criterion текстовий опис критерію для заголовка виводу
     */
    private void sortAndPrint(ArrayList<BookEntry> entries,
                              java.util.Comparator<BookEntry> cmp,
                              String criterion) {
        System.out.println("--- Sorted by " + criterion
                + " [" + entries.size() + " title(s)] ---");

        Collections.sort(entries, cmp);
        // var with list.sort
        // entries.sort(cmp);

        for (int i = 0; i < entries.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + entries.get(i));
        }
        System.out.println();
    }

    /**
     * Зберігає відсортований порядок книг.
     *
     * <p>Якщо користувач підтверджує збереження ({@code y}),
     * внутрішній порядок {@link Library} оновлюється через
     * {@link Library#reorderEntries(ArrayList)}, після чого
     * зміни автоматично потраплять у файли при наступному збереженні.</p>
     *
     * <p>Якщо користувач відмовляється ({@code n}) або вводить щось інше —
     * бібліотека залишається без змін.</p>
     *
     * @param sortedBooks відсортований список книг
     */
    private void saveSortedBooks(ArrayList<BookEntry> sortedBooks) {
        // --- Запит на збереження порядку ---
        System.out.print("Save this order as the new library order? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("y")||answer.equals("yes")) {
            library.reorderEntries(sortedBooks);
            System.out.println("  [OK] New order saved.\n");
        } else {
            System.out.println("  Order not saved.\n");
        }

    }

    // ---------------------------------------------------------------
    // Допоміжні методи введення
    // ---------------------------------------------------------------

    /**
     * Зчитує непорожній рядок із клавіатури.
     * Повторює запит доти, доки користувач не введе хоча б один непробільний символ.
     *
     * @param prompt текст підказки, що виводиться перед полем введення
     * @return непорожній рядок після {@code trim()}
     */
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  [!] This field cannot be empty. Try again.");
        }
    }

    /**
     * Зчитує ціле число з клавіатури.
     * Повторює запит при нечисловому або порожньому введенні.
     *
     * @param prompt текст підказки
     * @return введене ціле число
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  [!] Value cannot be empty. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number. Try again.");
            }
        }
    }

    /**
     * Зчитує дійсне число. Підтримує крапку і кому як роздільник.
     *
     * @param prompt текст підказки
     * @return введене дійсне число
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            // Замінюємо кому на крапку, щоб підтримувати обидва формати
            String line = scanner.nextLine().trim().replace(',', '.');
            if (line.isEmpty()) {
                System.out.println("  [!] Value cannot be empty. Try again.");
                continue;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number (e.g. 19.99). Try again.");
            }
        }
    }

    /**
     * Відображає нумерований список констант enum і повертає обрану.
     *
     * @param <T>    тип enum
     * @param values масив констант ({@code SomeEnum.values()})
     * @param label  назва поля для підказки
     * @return обрана константа
     */
    private <T extends Enum<T>> T readEnum(T[] values, String label) {
        System.out.println("  " + label + ":");
        for (int i = 0; i < values.length; i++) {
            System.out.println("    " + (i + 1) + ". " + values[i]);
        }
        while (true) {
            int choice = readInt("  " + label + " [1-" + values.length + "]: ");
            if (choice >= 1 && choice <= values.length) {
                return values[choice - 1];
            }
            System.out.println("  [!] Enter a number from 1 to " + values.length + ".");
        }
    }

}

