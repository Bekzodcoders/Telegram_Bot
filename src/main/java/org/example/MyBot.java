package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MyBot extends TelegramLongPollingBot{

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Salom siz Bekzodning Sinov botidasiz");
        SendMessage sendMessage = new SendMessage();


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
