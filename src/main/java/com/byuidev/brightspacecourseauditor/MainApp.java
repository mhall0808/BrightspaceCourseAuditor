package com.byuidev.brightspacecourseauditor;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import coursefilesauditor.Manifest;
import java.io.File;
import java.io.FileInputStream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        // We need to open up Firebase now.  Well, I feel like I could open it
        // up at the end to save load time, but then again, it only should take
        // a few seconds for the whole program, so we will just do it this way.
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("Course File Auditor-a6bcecc40997.json"))
                .setDatabaseUrl("https://course-file-auditor.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

        File folder = new File("your/path");
        File[] listOfFiles = folder.listFiles();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println("File " + listOfFile.getName());
                Manifest manifest = new Manifest("C:\\Users\\Mark\\Downloads\\D2LExport_9730_20166104");
                manifest.gatherCSV();
                UnzipFiles unzip = new UnzipFiles("", "");
            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory " + listOfFile.getName());
            }
        }

        //Manifest manifest2 = new Manifest("C:\\Users\\Mark\\Downloads\\D2LExport_17905_20166332");
        //manifest2.gatherCSV();
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
