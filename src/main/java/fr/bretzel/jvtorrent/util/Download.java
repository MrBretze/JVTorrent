package fr.bretzel.jvtorrent.util;

import com.jfoenix.controls.JFXProgressBar;

import fr.bretzel.jvtorrent.JVTorrent;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MrBretzel on 16/01/2016.
 */
public class Download {

    private String url;
    private static boolean popup = true;
    private static JFXProgressBar progressBar;
    private File out;
    private int progresse;
    private Thread t;
    private VBox pane;

    public Download(String url, boolean popup, File out) {
        this.url = url;
        this.popup = popup;
        this.out = out;
    }

    public void download() {
        pane = new VBox();
        pane.setSpacing(30);
        pane.setStyle("-fx-background-color:WHITE");

        progressBar = new JFXProgressBar(-1);
        progressBar.setPrefHeight(500);

        final StackPane main = new StackPane();
        main.getChildren().add(progressBar);
        main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane.setMargin(pane, new Insets(20, 0, 0, 20));

        Stage stage = new Stage();
        final Scene scene = new Scene(main, 600, 200, Color.WHITE);
        scene.getStylesheets().add(JVTorrent.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("Download JVTorrent");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    URL url = new URL(getUrl());
                    HttpURLConnection httpCo = (HttpURLConnection) url.openConnection();

                    long completeFileSize = httpCo.getContentLength();

                    BufferedInputStream in = new BufferedInputStream(httpCo.getInputStream());
                    FileOutputStream outputStream = new FileOutputStream(out);

                    BufferedOutputStream out = new BufferedOutputStream(outputStream, 1024);
                    byte[] data = new byte[1024];
                    long downloadedFileSize = 0;
                    int x = 0;

                    while ((x = in.read(data, 0, 1024)) >= 0) {
                        downloadedFileSize += x;
                        final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100000D);

                        progresse = currentProgress;

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                if (popup)
                                    progressBar.setProgress(currentProgress);
                            }
                        });
                        out.write(data, 0, x);
                    }
                    out.close();
                    in.close();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        };

        t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        t.stop();
    }

    public int getProgresse() {
        return progresse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public boolean hasPopup() {
        return popup;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }
}