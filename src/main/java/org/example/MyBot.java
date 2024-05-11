package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {

    List<TelegramState> users = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            TelegramState currentUser = findChatId(chatId);
            if (update.hasMessage() && update.getMessage().hasText()) {


                if (messageText.equals("/start")) {
                    SendMessage message = new SendMessage();
                    message.setText("Salom, ismingizni kiriting, iltimos.");
                    message.setChatId(chatId);
                    String firstName = update.getMessage().getFrom().getFirstName();
                    System.out.println("ismi: "+firstName);
                    execute(message);
                    currentUser.setState(UserState.FIRSTNAME);
                } else if (currentUser.getState().equals(UserState.FIRSTNAME)) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Telefon raqamingizni yuboring, iltimos.");
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    KeyboardRow keyboardRow = new KeyboardRow();
                    KeyboardButton contactButton = new KeyboardButton();
                    contactButton.setText("Telefon raqamingizni yuboring");
                    contactButton.setRequestContact(true);
                    keyboardRow.add(contactButton);
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    keyboard.add(keyboardRow);
                    replyKeyboardMarkup.setKeyboard(keyboard);
                    message.setReplyMarkup(replyKeyboardMarkup);
                    execute(message);
                    currentUser.setState(UserState.PHONENUMBER);

                }
            }
            if (currentUser.getState().equals(UserState.PHONENUMBER) && update.getMessage().hasContact()) {

                String phoneNumber = update.getMessage().getContact().getPhoneNumber();
                Long chatId1 = update.getMessage().getChatId();
                SendMessage message = new SendMessage();
                message.setChatId(chatId1);
                System.out.println("Telefon raqami : " + phoneNumber);
                message.setText("Telefon raqamingiz qabul qilindi: " + phoneNumber);
                execute(message);
                currentUser.setState(UserState.DONE);
            }
        }catch (TelegramApiException e){
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