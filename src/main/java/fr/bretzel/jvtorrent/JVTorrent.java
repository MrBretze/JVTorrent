package fr.bretzel.jvtorrent;

import com.jfoenix.controls.JFXCheckBox;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.validation.RequiredFieldValidator;

import de.jensd.fx.fontawesome.*;
import de.jensd.fx.fontawesome.Icon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by MrBretzel on 16/01/2016.
 */
public class JVTorrent extends Application {

    public static Stage mainStage = null;
    public static Logger logger;
    public static File appDir;

    public static void main(String[] args) {
        logger = Logger.getLogger(JVTorrent.class);

        logger.setLevel(Level.DEBUG);

        try {
            launch(args);
        } catch (Exception e) {
            String message = "An error that occurred: \n" + "Error: " + e.getLocalizedMessage();
            JOptionPane.showMessageDialog(new Frame(), message, "JVTorrent: Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("This a debug !");

            VBox main = new VBox();
            final JFXTextArea area = new JFXTextArea();
            area.setLabelFloat(true);
            RequiredFieldValidator validator = new RequiredFieldValidator();
            // NOTE adding error class to text area is causing the cursor to disapper
            validator.setErrorStyleClass("");
            validator.setMessage("Please type something!");
            validator.setIcon(new Icon(AwesomeIcon.WARNING, "1em", ";", "error"));
            area.getValidators().add(validator);
            area.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (!newVal) area.validate();
            });

            main.getChildren().add(area);
            StackPane pane = new StackPane();
            pane.getChildren().add(main);
            StackPane.setMargin(main, new Insets(100));
            pane.setStyle("-fx-background-color:WHITE");
            final Scene scene = new Scene(pane, 800, 600);
            scene.getStylesheets().add(JVTorrent.class.getResource("/css/jfoenix-components.css").toExternalForm());

            Stage stg2 = new Stage();
            stg2.setTitle("Debug Console");
            stg2.setScene(scene);
            stg2.show();
            TextAreaOutputStream out = new TextAreaOutputStream(area);
            Appender appender = new FileAppender();
            appender.
            logger.addAppender(out);
        }

        this.mainStage = stage;
        FlowPane pane;
        pane = new FlowPane();
        pane.setVgap(20);
        pane.setHgap(20);

        JFXCheckBox checkBox = new JFXCheckBox("Test Material");

        pane.getChildren().add(checkBox);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(pane);
        StackPane.setMargin(pane, new Insets(100));
        stackPane.setStyle("-fx-background-color:BLACK");

        final Scene scene = new Scene(stackPane, 600, 200);
        scene.getStylesheets().add(getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JVTorrent");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.getIcons().add(new Image(getResource("/icon/torrent.png").toExternalForm()));
        stage.show();

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            int i = 0;

            @Override
            public void run() {
                JVTorrent.logger.debug("Test: " + i);
            }
        }, 1000, 1000);
    }

    public static URL getResource(String destination) {
        try {
            return new URL(JVTorrent.class.getResource(destination).toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> getClasseNames(File jar) {
        ArrayList classes = new ArrayList();

        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
            JarEntry jarEntry;
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    classes.add(jarEntry.getName().replaceAll("/", "."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
}
