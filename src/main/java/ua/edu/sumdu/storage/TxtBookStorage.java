package ua.edu.sumdu.storage;

import ua.edu.sumdu.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Реалізація BookStorage для текстового файлу.
 */
public class TxtBookStorage implements BookStorage {

    private static final String DELIMITER = "\\|";

    private static final String WRITE_DELIMITER = "|";

    private final String filePath;

    // Створює сховище, прив'язане до вказаного файлу
    public TxtBookStorage(String filePath) {
        this.filePath = filePath;
    }

    // ---------------------------------------------------------------
    // Завантаження
    // ---------------------------------------------------------------

    // Зчитує книги з текстового файлу.
    @Override
    public ArrayList<Book> load() {
        ArrayList<Book> books = new ArrayList<Book>();
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
                Book book = parseLine(line, lineNumber);
                if (book != null) {
                    books.add(book);
                }
            }
            System.out.println("  [TXT] Loaded " + books.size() + " books from " + filePath);
        } catch (IOException e) {
            System.out.println("  [TXT] File not found or unreadable: " + filePath
                    + " — starting with empty collection.");
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
        return books;
    }

    // Розбирає один рядок файлу та повертає відповідний об'єкт
    private Book parseLine(String line, int lineNumber) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 1) {
            warn(lineNumber, "empty record");
            return null;
        }

        String type = parts[0].trim().toUpperCase();
        try {
            switch (type) {
                case "BOOK":
                    return parseBook(parts, lineNumber);
                case "EBOOK":
                    return parseEBook(parts, lineNumber);
                case "AUDIOBOOK":
                    return parseAudioBook(parts, lineNumber);
                case "PAPERBOOK":
                    return parsePaperBook(parts, lineNumber);
                case "RAREBOOK":
                    return parseRareBook(parts, lineNumber);
                default:
                    warn(lineNumber, "unknown type: " + type);
                    return null;
            }
        } catch (InvalidBookDataException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            warn(lineNumber, e.getMessage());
            return null;
        }
    }

    // Базовий Book
    private Book parseBook(String[] p, int ln) {
        checkLength(p, 7, ln, "BOOK");
        return new Book(p[1].trim(), p[2].trim(),
                parseInt(p[3], ln), parseDouble(p[4], ln),
                Genre.valueOf(p[5].trim()), parseInt(p[6], ln));
    }

    // EBook
    private EBook parseEBook(String[] p, int ln) {
        checkLength(p, 10, ln, "EBOOK");
        return new EBook(p[1].trim(), p[2].trim(),
                parseInt(p[3], ln), parseDouble(p[4], ln),
                Genre.valueOf(p[5].trim()), parseInt(p[6], ln),
                p[7].trim(), parseDouble(p[8], ln), p[9].trim());
    }

    // AudioBook
    private AudioBook parseAudioBook(String[] p, int ln) {
        checkLength(p, 10, ln, "AUDIOBOOK");
        return new AudioBook(p[1].trim(), p[2].trim(),
                parseInt(p[3], ln), parseDouble(p[4], ln),
                Genre.valueOf(p[5].trim()), parseInt(p[6], ln),
                p[7].trim(), parseInt(p[8], ln), p[9].trim());
    }

    // PaperBook
    private PaperBook parsePaperBook(String[] p, int ln) {
        checkLength(p, 10, ln, "PAPERBOOK");
        return new PaperBook(p[1].trim(), p[2].trim(),
                parseInt(p[3], ln), parseDouble(p[4], ln),
                Genre.valueOf(p[5].trim()), parseInt(p[6], ln),
                p[7].trim(), parseInt(p[8], ln), parseDouble(p[9], ln));
    }

    // RareBook
    private RareBook parseRareBook(String[] p, int ln) {
        checkLength(p, 13, ln, "RAREBOOK");
        return new RareBook(p[1].trim(), p[2].trim(),
                parseInt(p[3], ln), parseDouble(p[4], ln),
                Genre.valueOf(p[5].trim()), parseInt(p[6], ln),
                p[7].trim(), parseInt(p[8], ln), parseDouble(p[9], ln),
                BookCondition.valueOf(p[10].trim()),
                parseDouble(p[11], ln), parseInt(p[12], ln));
    }

    // ---------------------------------------------------------------
    // Збереження
    // ---------------------------------------------------------------

    @Override
    public void save(ArrayList<Book> books) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write("# Book Manager");
            writer.newLine();

            for (int i = 0; i < books.size(); i++) {
                writer.write(serialize(books.get(i)));
                writer.newLine();
            }
            System.out.println("  [TXT] Saved " + books.size() + " books to " + filePath);
        } catch (IOException e) {
            System.out.println("  [TXT] Error saving to " + filePath + ": " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }

    private String serialize(Book book) {
        // Спільні поля базового класу
        String base = book.getTitle() + WRITE_DELIMITER
                + book.getAuthor() + WRITE_DELIMITER
                + book.getYear() + WRITE_DELIMITER
                + book.getPrice() + WRITE_DELIMITER
                + book.getGenre().name() + WRITE_DELIMITER
                + book.getPages();

        if (book instanceof RareBook) {
            RareBook rb = (RareBook) book;
            return "RAREBOOK" + WRITE_DELIMITER + base
                    + WRITE_DELIMITER + rb.getPublisher()
                    + WRITE_DELIMITER + rb.getEdition()
                    + WRITE_DELIMITER + rb.getWeightGrams()
                    + WRITE_DELIMITER + rb.getCondition().name()
                    + WRITE_DELIMITER + rb.getEstimatedValueUSD()
                    + WRITE_DELIMITER + rb.getAcquisitionYear();
        }
        if (book instanceof PaperBook) {
            PaperBook pb = (PaperBook) book;
            return "PAPERBOOK" + WRITE_DELIMITER + base
                    + WRITE_DELIMITER + pb.getPublisher()
                    + WRITE_DELIMITER + pb.getEdition()
                    + WRITE_DELIMITER + pb.getWeightGrams();
        }
        if (book instanceof EBook) {
            EBook eb = (EBook) book;
            return "EBOOK" + WRITE_DELIMITER + base
                    + WRITE_DELIMITER + eb.getFileFormat()
                    + WRITE_DELIMITER + eb.getFileSizeMB()
                    + WRITE_DELIMITER + eb.getDownloadUrl();
        }
        if (book instanceof AudioBook) {
            AudioBook ab = (AudioBook) book;
            return "AUDIOBOOK" + WRITE_DELIMITER + base
                    + WRITE_DELIMITER + ab.getNarrator()
                    + WRITE_DELIMITER + ab.getDurationMinutes()
                    + WRITE_DELIMITER + ab.getAudioFormat();
        }
        return "BOOK" + WRITE_DELIMITER + base;
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