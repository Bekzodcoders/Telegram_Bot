package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            String text = message.getText();

            if ("/start".equals(text)) {
                String username = message.getFrom().getFirstName();
                sendMsg(message.getChatId(), "Assalomu alekum, " + username + " Botga xush kelibsiz");
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(Long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        execute(sendMessage);
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
