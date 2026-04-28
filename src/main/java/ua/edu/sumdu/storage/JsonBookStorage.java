package ua.edu.sumdu.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ua.edu.sumdu.model.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Реалізація {@link BookStorage} для JSON-файлу з використанням бібліотеки Gson.
 *
 * <h2>Формат JSON-файлу</h2>
 * <p>Кожен об'єкт у масиві містить поле {@code "classType"} (наприклад,
 * {@code "EBOOK"}), яке дозволяє однозначно визначити тип при десеріалізації:</p>
 * <pre>
 * [
 *   { "classType": "BOOK",      "title": "...", ... },
 *   { "classType": "EBOOK",     "title": "...", "fileFormat": "EPUB", ... },
 *   { "classType": "AUDIOBOOK", "title": "...", "narrator": "...", ... },
 *   { "classType": "PAPERBOOK", "title": "...", "publisher": "...", ... },
 *   { "classType": "RAREBOOK",  "title": "...", "condition": "FINE", ... }
 * ]
 * </pre>
 */
public class JsonBookStorage implements BookStorage {

    /** Поле типу в JSON. */
    private static final String CLASS_TYPE_FIELD = "classType";

    /** Кількість книг. */
    private static final String QUANTITY_FIELD   = "quantity";

    /** Шлях до JSON-файлу. */
    private final String filePath;

    /**
     * Gson з зареєстрованими адаптерами для поліморфної ієрархії Book.
     * Використовується тільки для зовнішнього (де)серіалізації.
     */
    private final Gson gson;

    /**
     * "Чистий" Gson без власних адаптерів — використовується всередині
     * адаптерів для уникнення нескінченної рекурсії.
     */
    private final Gson rawGson;

    /**
     * Створює сховище, прив'язане до вказаного JSON-файлу.
     *
     * @param filePath шлях до файлу {@code input.json}
     */
    public JsonBookStorage(String filePath) {
        this.filePath = filePath;
        this.rawGson  = new Gson();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(BookEntry.class, new BookEntryAdapter())
                .create();
    }

    // ---------------------------------------------------------------
    // Завантаження
    // ---------------------------------------------------------------

    /**
     * Зчитує книги з JSON-файлу.
     * Пропускає елементи з невідомим або некоректним {@code classType}.
     */
    @Override
    public void load(Library library) {
        int loaded = 0;
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            if (array == null) {
                System.out.println("  [JSON] File is empty: " + filePath);
                return;
            }
            for (int i = 0; i < array.size(); i++) {
                try {
                    BookEntry entry = gson.fromJson(array.get(i), BookEntry.class);
                    if (entry != null) {
                        library.addNewBook(entry.getBook(), entry.getQuantity());

                        loaded++;
                    }
                } catch (JsonParseException | InvalidBookDataException e) {
                    System.out.println("  [JSON] Skipping element " + i + ": " + e.getMessage());
                }
            }
            System.out.println("  [JSON] Loaded " + loaded + " record(s) from " + filePath);
        } catch (IOException e) {
            System.out.println("  [JSON] File not found: " + filePath
                    + " — starting with empty library.");
        } catch (JsonParseException e) {
            System.out.println("  [JSON] Malformed JSON in " + filePath + ": " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    // ---------------------------------------------------------------
    // Збереження
    // ---------------------------------------------------------------

    /**
     * Записує всі книги до JSON-файлу.
     */
    @Override
    public void save(Library library) {
        FileWriter writer = null;
        try {
            // Збираємо масив BookEntry
            BookEntry[] entries = new BookEntry[library.getEntryCount()];
            for (int i = 0; i < library.getEntryCount(); i++) {
                entries[i] = library.getEntry(i);
            }

            writer = new FileWriter(filePath);
            gson.toJson(entries, writer);
            System.out.println("  [JSON] Saved " + library.getEntryCount() + " record(s) to " + filePath);
        } catch (IOException e) {
            System.out.println("  [JSON] Error saving to " + filePath + ": " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }

    // ---------------------------------------------------------------
    // Gson TypeAdapter для поліморфної ієрархії Book
    // ---------------------------------------------------------------

    /**
     * Адаптер Gson, що обробляє серіалізацію та десеріалізацію
     * поліморфної ієрархії {@link Book}.
     *
     * <p>При серіалізації до кожного об'єкта додається поле
     * {@code "classType"} зі значенням типу класу (напр. {@code "EBOOK"}).
     * При десеріалізації це поле читається першим і визначає,
     * до якого класу ієрархії перетворити решту полів.</p>
     */
    private class BookEntryAdapter
            implements JsonSerializer<BookEntry>, JsonDeserializer<BookEntry> {

        /**
         * Серіалізує об'єкт {@link Book} у JSON з полем {@code classType}.
         */
        @Override
        public JsonElement serialize(BookEntry src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            Book book = src.getBook();
            JsonObject obj = rawGson.toJsonTree(book).getAsJsonObject();
            obj.addProperty(CLASS_TYPE_FIELD, book.getClass().getSimpleName().toUpperCase());
            obj.addProperty(QUANTITY_FIELD, src.getQuantity());
            return obj;
        }

        /**
         * Десеріалізує JSON-об'єкт у відповідний підклас {@link Book}.
         *
         * @throws JsonParseException якщо поле {@code classType} відсутнє
         *                            або має невідоме значення
         */
        @Override
        public BookEntry deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            if (!obj.has(CLASS_TYPE_FIELD)) {
                throw new JsonParseException("Missing '" + CLASS_TYPE_FIELD + "' field.");
            }
            if (!obj.has(QUANTITY_FIELD)) {
                throw new JsonParseException("Missing '" + QUANTITY_FIELD + "' field.");
            }

            int quantity = obj.get(QUANTITY_FIELD).getAsInt();
            String classType = obj.get(CLASS_TYPE_FIELD).getAsString().toUpperCase();

            Book book;
            switch (classType) {
                case "BOOK":
                    book = rawGson.fromJson(obj, Book.class);
                    break;
                case "EBOOK":
                    book = rawGson.fromJson(obj, EBook.class);
                    break;
                case "AUDIOBOOK":
                    book = rawGson.fromJson(obj, AudioBook.class);
                    break;
                case "PAPERBOOK":
                    book = rawGson.fromJson(obj, PaperBook.class);
                    break;
                case "RAREBOOK":
                    book = rawGson.fromJson(obj, RareBook.class);
                    break;
                default:
                    throw new JsonParseException("Unknown classType: " + classType);
            }
            return new BookEntry(book, quantity);
        }
    }
}