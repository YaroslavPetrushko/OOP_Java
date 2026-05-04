package ua.edu.sumdu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * Точка входу JavaFX — завантажує {@code main.fxml} і відкриває вікно.
 *
 * <p>Вся логіка інтерфейсу знаходиться у {@link MainController}.</p>
 *
 * <h2>Запуск</h2>
 * <pre>mvn javafx:run</pre>
 */
public class MainApp extends Application {

    /**
     * Завантажує FXML, підключає CSS і відображає основне вікно.
     *
     * @param stage первинна сцена JavaFX
     * @throws Exception якщо FXML або CSS не знайдено
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Завантаження FXML
        URL fxmlUrl = Objects.requireNonNull(
                getClass().getResource("main.fxml"),
                "Cannot find main.fxml in classpath");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Завантаження CSS
        URL cssUrl = Objects.requireNonNull(
                getClass().getResource("styles.css"),
                "Cannot find styles.css in classpath");
        root.getStylesheets().add(cssUrl.toExternalForm());

        // Налаштування сцени
        Scene scene = new Scene(root, 1180, 730);
        stage.setTitle("Book Manager — UUID");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // Збереження при закритті
        MainController controller = loader.getController();
        stage.setOnCloseRequest(e -> controller.saveAll());

        stage.show();
    }

    /**
     * Точка входу JVM (для запуску без javafx-maven-plugin).
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        launch(args);
    }
}