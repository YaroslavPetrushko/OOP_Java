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
import java.util.ArrayList;

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
                .registerTypeHierarchyAdapter(Book.class, new BookAdapter())
                .create();
    }

    // ---------------------------------------------------------------
    // Завантаження
    // ---------------------------------------------------------------

    /**
     * Зчитує книги з JSON-файлу.
     * Пропускає елементи з невідомим або некоректним {@code classType}.
     *
     * @return колекція завантажених книг (порожня при помилці)
     */
    @Override
    public ArrayList<Book> load() {
        ArrayList<Book> books = new ArrayList<Book>();
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            if (array == null) {
                System.out.println("  [JSON] File is empty: " + filePath);
                return books;
            }
            for (int i = 0; i < array.size(); i++) {
                try {
                    Book book = gson.fromJson(array.get(i), Book.class);
                    if (book != null) {
                        books.add(book);
                    }
                } catch (JsonParseException | InvalidBookDataException e) {
                    System.out.println("  [JSON] Skipping element " + i + ": " + e.getMessage());
                }
            }
            System.out.println("  [JSON] Loaded " + books.size() + " book(s) from " + filePath);
        } catch (IOException e) {
            System.out.println("  [JSON] File not found or unreadable: " + filePath
                    + " — starting with empty collection.");
        } catch (JsonParseException e) {
            System.out.println("  [JSON] Malformed JSON in " + filePath + ": " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
        return books;
    }

    // ---------------------------------------------------------------
    // Збереження
    // ---------------------------------------------------------------

    /**
     * Записує всі книги до JSON-файлу.
     *
     * @param books колекція для збереження
     */
    @Override
    public void save(ArrayList<Book> books) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath);
            gson.toJson(books, writer);
            System.out.println("  [JSON] Saved " + books.size() + " book(s) to " + filePath);
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
    private class BookAdapter
            implements JsonSerializer<Book>, JsonDeserializer<Book> {

        /**
         * Серіалізує об'єкт {@link Book} у JSON з полем {@code classType}.
         */
        @Override
        public JsonElement serialize(Book src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject obj = rawGson.toJsonTree(src).getAsJsonObject();
            String classType = src.getClass().getSimpleName().toUpperCase();
            obj.addProperty(CLASS_TYPE_FIELD, classType);
            return obj;
        }

        /**
         * Десеріалізує JSON-об'єкт у відповідний підклас {@link Book}.
         *
         * @throws JsonParseException якщо поле {@code classType} відсутнє
         *                            або має невідоме значення
         */
        @Override
        public Book deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            if (!obj.has(CLASS_TYPE_FIELD)) {
                throw new JsonParseException("Missing '" + CLASS_TYPE_FIELD + "' field.");
            }

            String classType = obj.get(CLASS_TYPE_FIELD).getAsString().toUpperCase();
            switch (classType) {
                case "BOOK":
                    return rawGson.fromJson(obj, Book.class);
                case "EBOOK":
                    return rawGson.fromJson(obj, EBook.class);
                case "AUDIOBOOK":
                    return rawGson.fromJson(obj, AudioBook.class);
                case "PAPERBOOK":
                    return rawGson.fromJson(obj, PaperBook.class);
                case "RAREBOOK":
                    return rawGson.fromJson(obj, RareBook.class);
                default:
                    throw new JsonParseException("Unknown classType: " + classType);
            }
        }
    }
}