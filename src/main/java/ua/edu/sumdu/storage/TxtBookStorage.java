package ua.edu.sumdu.storage;

import ua.edu.sumdu.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Реалізація {@link BookStorage} для текстового файлу у форматі pipe-delimited.
 *
 * <h2>Формат рядка (поля розділено символом {@code |}):</h2>
 * <pre>
 * BOOK|title|author|year|price|genre|pages
 * EBOOK|title|author|year|price|genre|pages|fileFormat|fileSizeMB|downloadUrl
 * AUDIOBOOK|title|author|year|price|genre|pages|narrator|durationMinutes|audioFormat
 * PAPERBOOK|title|author|year|price|genre|pages|publisher|edition|weightGrams
 * RAREBOOK|title|author|year|price|genre|pages|publisher|edition|weightGrams|condition|estimatedValueUSD|acquisitionYear
 * </pre>
 *
 * <p>Рядки, що починаються з {@code #}, вважаються коментарями і пропускаються.
 * Некоректні рядки пропускаються з виведенням попередження.</p>
 *
 * <p><b>Обмеження:</b> рядкові поля не повинні містити символ {@code |}.</p>
 */
public class TxtBookStorage implements BookStorage {

    /** Розділювач полів у рядку файлу. */
    private static final String DELIMITER = "\\|";

    /** Розділювач при записі. */
    private static final String WRITE_DELIMITER = "|";

    /** Шлях до файлу зберігання. */
    private final String filePath;

    /**
     * Створює сховище, прив'язане до вказаного файлу.
     *
     * @param filePath шлях до файлу {@code input.txt}
     */
    public TxtBookStorage(String filePath) {
        this.filePath = filePath;
    }

    // ---------------------------------------------------------------
    // Завантаження
    // ---------------------------------------------------------------

    /**
     * Зчитує книги з текстового файлу.
     * Пропускає порожні рядки, коментарі та некоректні записи.
     */
    @Override
    public void load(Library library) {
        int loaded = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (parseLine(line, lineNumber, library)) {
                    loaded++;
                }
            }
            System.out.println("  [TXT] Loaded " + loaded + " record(s) from " + filePath);
        } catch (IOException e) {
            System.out.println("  [TXT] File not found: " + filePath
                    + " — starting with empty library.");
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Розбирає один рядок файлу та повертає відповідний об'єкт {@link Book}.
     * При помилці повертає {@code null} і виводить попередження.
     *
     * @param line       рядок файлу
     * @param lineNumber номер рядка (для діагностики)
     * @return об'єкт Book або {@code null} при помилці
     */
    private boolean parseLine(String line, int lineNumber, Library library) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 1) {
            warn(lineNumber, "empty record");
            return false;
        }

        String type = parts[0].trim().toUpperCase();
        try {
            switch (type) {
                case "BOOK":
                    return parseBook(parts, lineNumber, library);
                case "EBOOK":
                    return parseEBook(parts, lineNumber, library);
                case "AUDIOBOOK":
                    return parseAudioBook(parts, lineNumber, library);
                case "PAPERBOOK":
                    return parsePaperBook(parts, lineNumber, library);
                case "RAREBOOK":
                    return parseRareBook(parts, lineNumber, library);
                default:
                    warn(lineNumber, "unknown type: " + type);
                    return false;
            }
        } catch (InvalidBookDataException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            warn(lineNumber, e.getMessage());
            return false;
        }
    }

    /** Розбирає базову книгу (7 полів: type + 6). */
    private boolean parseBook(String[] parts, int lineNumber, Library library) {
        checkLength(parts, 8, lineNumber, "BOOK");
        Book book = new Book(
                parts[1].trim(), parts[2].trim(),
                parseInt(parts[3], lineNumber), parseDouble(parts[4], lineNumber),
                Genre.valueOf(parts[5].trim()), parseInt(parts[6], lineNumber));
        library.addNewBook(book, parseInt(parts[7], lineNumber));
        return true;
    }

    /** Розбирає EBook (10 полів). */
    private Boolean parseEBook(String[] parts, int lineNumber, Library library) {
        checkLength(parts, 11, lineNumber, "EBOOK");
        EBook book = new EBook(
                parts[1].trim(), parts[2].trim(),
                parseInt(parts[3], lineNumber), parseDouble(parts[4], lineNumber),
                Genre.valueOf(parts[5].trim()), parseInt(parts[6], lineNumber),
                parts[7].trim(), parseDouble(parts[8], lineNumber), parts[9].trim());
        library.addNewBook(book, parseInt(parts[10], lineNumber));
        return true;
    }

    /** Розбирає AudioBook (10 полів). */
    private boolean parseAudioBook(String[] parts, int lineNumber, Library library) {
        checkLength(parts, 11, lineNumber, "AUDIOBOOK");
        AudioBook book = new AudioBook(
                parts[1].trim(), parts[2].trim(),
                parseInt(parts[3], lineNumber), parseDouble(parts[4], lineNumber),
                Genre.valueOf(parts[5].trim()), parseInt(parts[6], lineNumber),
                parts[7].trim(), parseInt(parts[8], lineNumber), parts[9].trim());
        library.addNewBook(book, parseInt(parts[10], lineNumber));
        return true;
    }

    /** Розбирає PaperBook (10 полів). */
    private boolean parsePaperBook(String[] parts, int lineNumber, Library library) {
        checkLength(parts, 11, lineNumber, "PAPERBOOK");
        PaperBook book = new PaperBook(
                parts[1].trim(), parts[2].trim(),
                parseInt(parts[3], lineNumber), parseDouble(parts[4], lineNumber),
                Genre.valueOf(parts[5].trim()), parseInt(parts[6], lineNumber),
                parts[7].trim(), parseInt(parts[8], lineNumber),
                parseDouble(parts[9], lineNumber));
        library.addNewBook(book, parseInt(parts[10], lineNumber));
        return true;
    }

    /** Розбирає RareBook (13 полів). */
    private boolean parseRareBook(String[] parts, int lineNumber, Library library) {
        checkLength(parts, 14, lineNumber, "RAREBOOK");
        RareBook book = new RareBook(
                parts[1].trim(), parts[2].trim(),
                parseInt(parts[3], lineNumber), parseDouble(parts[4], lineNumber),
                Genre.valueOf(parts[5].trim()), parseInt(parts[6], lineNumber),
                parts[7].trim(), parseInt(parts[8], lineNumber),
                parseDouble(parts[9], lineNumber),
                BookCondition.valueOf(parts[10].trim()),
                parseDouble(parts[11], lineNumber), parseInt(parts[12], lineNumber));
        library.addNewBook(book, parseInt(parts[13], lineNumber));
        return true;
    }

    // ---------------------------------------------------------------
    // Збереження
    // ---------------------------------------------------------------

    /**
     * Записує всі книги до текстового файлу у pipe-delimited форматі.
     */
    @Override
    public void save(Library library) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write("# Book Manager — input.exe - do not edit manually");
            writer.newLine();
            writer.write("# Library: " + library.getName()
                    + " | " + library.getAddress());
            writer.newLine();

            for (int i = 0; i < library.getEntryCount(); i++) {
                BookEntry entry = library.getEntry(i);
                writer.write(serialize(entry.getBook(), entry.getQuantity()));
                writer.newLine();
            }
            System.out.println("  [TXT] Saved " + library.getEntryCount() + " record(s) to " + filePath);
        } catch (IOException e) {
            System.out.println("  [TXT] Error saving to " + filePath + ": " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Серіалізує один об'єкт {@link Book} у pipe-delimited рядок.
     *
     * @param book об'єкт для серіалізації
     * @return рядок у форматі файлу
     */
    private String serialize(Book book, int  quantity) {
        // Спільні поля базового класу
        String base = book.getTitle() + WRITE_DELIMITER
                + book.getAuthor() + WRITE_DELIMITER
                + book.getYear() + WRITE_DELIMITER
                + book.getPrice() + WRITE_DELIMITER
                + book.getGenre().name() + WRITE_DELIMITER
                + book.getPages() + WRITE_DELIMITER;

        if (book instanceof RareBook) {
            RareBook rb = (RareBook) book;
            return "RAREBOOK" + WRITE_DELIMITER + base
                    + rb.getPublisher() + WRITE_DELIMITER
                    + rb.getEdition() + WRITE_DELIMITER
                    + rb.getWeightGrams() + WRITE_DELIMITER
                    + rb.getCondition().name() + WRITE_DELIMITER
                    + rb.getEstimatedValueUSD() + WRITE_DELIMITER
                    + rb.getAcquisitionYear() + WRITE_DELIMITER
                    + quantity;
        }
        if (book instanceof PaperBook) {
            PaperBook pb = (PaperBook) book;
            return "PAPERBOOK" + WRITE_DELIMITER + base
                    + pb.getPublisher() + WRITE_DELIMITER
                    + pb.getEdition() + WRITE_DELIMITER
                    + pb.getWeightGrams() + WRITE_DELIMITER
                    + quantity;
        }
        if (book instanceof EBook) {
            EBook eb = (EBook) book;
            return "EBOOK" + WRITE_DELIMITER + base
                    + eb.getFileFormat() + WRITE_DELIMITER
                    + eb.getFileSizeMB() + WRITE_DELIMITER
                    + eb.getDownloadUrl() + WRITE_DELIMITER
                    + quantity;
        }
        if (book instanceof AudioBook) {
            AudioBook ab = (AudioBook) book;
            return "AUDIOBOOK" + WRITE_DELIMITER + base
                    + ab.getNarrator() + WRITE_DELIMITER
                    + ab.getDurationMinutes() + WRITE_DELIMITER
                    + ab.getAudioFormat() + WRITE_DELIMITER
                    + quantity;
        }
        return "BOOK" + WRITE_DELIMITER + base + quantity;
    }

    // ---------------------------------------------------------------
    // Допоміжні методи
    // ---------------------------------------------------------------

    private void checkLength(String[] parts, int expected, int lineNumber, String type) {
        if (parts.length < expected) {
            throw new InvalidBookDataException(
                    "Expected " + expected + " fields for " + type
                            + " but got " + parts.length);
        }
    }

    private int parseInt(String s, int lineNumber) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException("Cannot parse integer: '" + s.trim() + "'");
        }
    }

    private double parseDouble(String s, int lineNumber) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException("Cannot parse decimal: '" + s.trim() + "'");
        }
    }

    private void warn(int lineNumber, String reason) {
        System.out.println("  [TXT] Skipping line " + lineNumber + ": " + reason);
    }
}