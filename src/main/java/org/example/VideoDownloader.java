package org.example;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoDownloader {

    public static void downloadVideo(String videoUrl, String outputFilePath) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(videoUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // Check if the connection is successful
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to connect. HTTP error code: " + connection.getResponseCode());
                return;
            }

            // Download the video
            try (InputStream in = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Video downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
