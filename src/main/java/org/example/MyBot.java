package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {

    List<TelegramState> users = new ArrayList<>();
    @Override
    public void onUpdateReceived(Update update) {

        try {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        TelegramState currentUser = findChatId(chatId);
        if (text.equals("/start")){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Assalomu aleykum\nIltimos Ismingizni kiriting");
            execute(sendMessage);
            currentUser.setState(UserState.FIRSTNAME);
        }else {
        if (currentUser.getState().equals(UserState.FIRSTNAME)){
           currentUser.getUser().setFirstName(text);
           SendMessage sendMessage = new SendMessage();
           sendMessage.setChatId(chatId);
           sendMessage.setText("Maxsulotni tanlang");

           ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
           List<KeyboardRow> rows = new ArrayList<>();
           replyKeyboardMarkup.setKeyboard(rows);
           KeyboardRow row = new KeyboardRow();
           KeyboardRow row2 = new KeyboardRow();
           KeyboardButton button = new KeyboardButton();
           KeyboardButton button2 = new KeyboardButton();

           button.setText("Olma");
           button2.setText("Anor");

           row.add(button);
           row2.add(button2);

           rows.add(row);
           rows.add(row2);

           sendMessage.setReplyMarkup(replyKeyboardMarkup);

            execute(sendMessage);
            currentUser.setState(UserState.SELECT_PRODUCT);
        }
        }

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private TelegramState findChatId(Long chatId) {
        for (TelegramState user : users) {
            if (user.getChatId().equals(chatId)){
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
