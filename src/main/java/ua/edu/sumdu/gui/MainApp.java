package ua.edu.sumdu.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * JavaFX GUI для Book Manager.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Завантаження FXML
        URL fxmlUrl = Objects.requireNonNull(
                getClass().getResource("main.fxml"),
                "Cannot find main.fxml in classpath");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Налаштування сцени
        Scene scene = new Scene(root, 1180, 730);
        stage.setTitle("Book Manager — GUI");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // Збереження при закритті
        MainController controller = loader.getController();
        stage.setOnCloseRequest(e -> controller.saveAll());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}