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
 * Реалізація BookStorage для JSON-файлу з використанням бібліотеки Gson.
 */
public class JsonBookStorage implements BookStorage {

    // Поле типу в JSON
    private static final String CLASS_TYPE_FIELD = "classType";

    // Шлях до JSON-файлу
    private final String filePath;

    private final Gson gson;

    private final Gson rawGson;

    // Створює сховище, прив'язане до вказаного JSON-файлу.
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

    // Зчитує книги з JSON-файлу.
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

    // Записує всі книги до JSON-файлу.
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

    private class BookAdapter
            implements JsonSerializer<Book>, JsonDeserializer<Book> {

        @Override
        public JsonElement serialize(Book src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject obj = rawGson.toJsonTree(src).getAsJsonObject();
            String classType = src.getClass().getSimpleName().toUpperCase();
            obj.addProperty(CLASS_TYPE_FIELD, classType);
            return obj;
        }

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