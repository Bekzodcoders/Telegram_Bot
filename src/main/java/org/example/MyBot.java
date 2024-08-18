package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {

    private List<TelegramState> users = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                TelegramState currentUser = findChatId(chatId);

                if (messageText.equals("/start")) {
                    SendMessage message = new SendMessage();
                    message.setText("Salom, ismingizni kiriting, iltimos.");
                    message.setChatId(chatId);
                    execute(message);
                    currentUser.setState(UserState.FIRSTNAME);

                } else if (currentUser.getState().equals(UserState.FIRSTNAME)) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Instagram postning URL manzilini yuboring.");
                    message.setReplyMarkup(null);
                    execute(message);
                    currentUser.setState(UserState.INSTAGRAMURL);

                } else if (currentUser.getState().equals(UserState.INSTAGRAMURL)) {
                    String videoUrl = fetchVideoUrl(messageText);
                    if (videoUrl != null) {
                        String videoFilePath = downloadVideo(videoUrl, "video.mp4");
                        if (videoFilePath != null) {
                            sendVideo(chatId, videoFilePath);
                        } else {
                            sendErrorMessage(chatId, "Video fayli yuklab olinmadi.");
                        }
                    } else {
                        sendErrorMessage(chatId, "Video URL olinganida xato yuz berdi.");
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String fetchVideoUrl(String instagramUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://instagram-downloader-reels-and-videos-downloader.p.rapidapi.com/post?url=" + instagramUrl))
                    .header("x-rapidapi-key", "75981dfe95msh5e880111ca38131p18c7c2jsn7f044351206d")
                    .header("x-rapidapi-host", "instagram-downloader-reels-and-videos-downloader.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("API Response: " + response.body());
            return parseVideoUrl(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String parseVideoUrl(String apiResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(apiResponse);
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            if (dataArray.length() > 0) {
                JSONObject videoData = dataArray.getJSONObject(0);
                return videoData.getString("download_url");
            } else {
                System.out.println("No video data found in API response.");
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String downloadVideo(String videoUrl, String outputFilePath) {
        try {
            URL url = new URL(videoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Failed to download video: " + connection.getResponseCode());
                return null;
            }

            File outputFile = new File(outputFilePath);
            try (InputStream in = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            connection.disconnect();
            return outputFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendVideo(Long chatId, String videoFilePath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        File videoFile = new File(videoFilePath);

        if (videoFile.exists() && videoFile.length() > 0) {
            sendDocument.setDocument(new InputFile(videoFile));
            try {
                execute(sendDocument);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            sendErrorMessage(chatId, "Video fayli mavjud emas yoki bo'sh.");
        }
    }

    private void sendErrorMessage(Long chatId, String errorMessageText) {
        SendMessage errorMessage = new SendMessage();
        errorMessage.setChatId(chatId);
        errorMessage.setText(errorMessageText);
        try {
            execute(errorMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private TelegramState findChatId(Long chatId) {
        for (TelegramState user : users) {
            if (user.getChatId().equals(chatId)) {
                return user;
            }
        }
        TelegramState telegramState = new TelegramState();
        telegramState.setChatId(chatId);
        telegramState.setState(UserState.START);
        users.add(telegramState);
        return telegramState;
    }

    @Override
    public String getBotUsername() {
        return "BEKZODSINOVBOT";
    }

    @Override
    public String getBotToken() {
        return "6188028528:AAHzO98Xploh3JeJdO1jdpH-4f5_7fCu_6k";
    }
}
