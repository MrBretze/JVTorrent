package fr.bretzel.jvtorrent;

import com.jfoenix.controls.JFXCheckBox;

import fr.bretzel.jvtorrent.runnable.ClearDownload;
import fr.bretzel.jvtorrent.util.Download;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Created by MrBretzel on 16/01/2016.
 */
public class JVTorrent extends Application {

    public static java.util.List<Download> downloads = new ArrayList<Download>();
    public static Stage mainStage = null;

    public static void main(String[] args) {
        Thread clearDownload = new Thread(new ClearDownload());

        clearDownload.start();

        try {
            File file = new File("library/");
            if (!file.exists()) {
                file.mkdir();
            }

            File lib = new File("library/JFoenix.jar");

            if (!lib.exists()) {
                lib.createNewFile();
                downloadFile("http://www.jfoenix.com/download/jfoenix.jar", true, lib);
            }

            loadLib(lib);

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
    }

    public static URL getResource(String destination) {
        try {
            return new URL(JVTorrent.class.getResource(destination).toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void downloadFile(String url, boolean popup, File out) {
        Download d = new Download(url, popup, out);
        d.download();
        downloads.add(d);
    }

    private static void loadLib(File file) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration e = jarFile.entries();

        URL[] urls = new URL[0];
        try {
            urls = new URL[]{ new URL("jar:file:" + file.getAbsolutePath() + "!/") };
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls, JVTorrent.class.getClassLoader());

        while (e.hasMoreElements()) {
            JarEntry je = (JarEntry) e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            try {
                cl.loadClass(className);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }

        }

        System.out.print("Number of class load for lib " + file.getName() + " : " + 0 + "\n");
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
