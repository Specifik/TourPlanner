package at.fhtw.tourplanner;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;

public class TourPlannerApplication extends Application {

    private static final Logger logger = LogManager.getLogger(TourPlannerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Tour Planner Application");
        try {
            launch(args);
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            logger.info("Initializing JavaFX application");

            Parent root = FXMLDependencyInjection.load("MainWindow.fxml", Locale.GERMAN);
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Tour planner");
            primaryStage.show();

            logger.info("Tour Planner Application started successfully");

        } catch (IOException e) {
            logger.error("Failed to load main window", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during application startup", e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        logger.info("Tour Planner Application shutting down");
        super.stop();
    }
}