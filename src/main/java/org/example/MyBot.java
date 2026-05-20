package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class MyBot extends TelegramLongPollingBot {

    // ADMIN ID (o'zingni ID qo'y)
    private static final long ADMIN_ID = 1999630938L;

    // USER STORAGE
    private final Map<Long, TelegramState> users = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        if (update == null || !update.hasMessage() || update.getMessage() == null) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        TelegramState currentUser = findUser(chatId);

        try {

            // ================= TEXT MESSAGE =================
            if (update.getMessage().hasText()) {

                String text = update.getMessage().getText();

                // ========== ADMIN PANEL ==========
                if (chatId == ADMIN_ID) {

                    if (text.equals("/admin")) {
                        sendMessage(chatId,
                                "🛠 ADMIN PANEL\n\n" +
                                        "/users - userlar soni\n" +
                                        "/broadcast <text> - hamma userga xabar"
                        );
                        return;
                    }

                    if (text.equals("/users")) {
                        sendMessage(chatId, "👥 Userlar soni: " + users.size());
                        return;
                    }

                    if (text.startsWith("/broadcast ")) {

                        String msg = text.replace("/broadcast ", "");

                        for (TelegramState user : users.values()) {
                            try {
                                sendMessage(user.getChatId(), "📢 " + msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        sendMessage(chatId, "✅ Xabar barcha userlarga yuborildi!");
                        return;
                    }
                }

                // ========== USER FLOW ==========
                if (text.equals("/start")) {

                    sendMessage(chatId,
                            "👋 Salom! Botga xush kelibsiz.\n\nIsmingizni kiriting:"
                    );

                    currentUser.setState(UserState.FIRSTNAME);
                    return;
                }

                if (currentUser.getState() == UserState.FIRSTNAME) {

                    if (text.startsWith("/")) {
                        sendMessage(chatId, "❗ Iltimos, ism kiriting.");
                        return;
                    }

                    currentUser.setFirstName(text);

                    sendContactKeyboard(chatId);

                    currentUser.setState(UserState.PHONENUMBER);
                    return;
                }
            }

            // ================= CONTACT =================
            if (update.getMessage().hasContact()
                    && update.getMessage().getContact() != null
                    && currentUser.getState() == UserState.PHONENUMBER) {

                Contact contact = update.getMessage().getContact();

                String phone = contact.getPhoneNumber();

                currentUser.setPhoneNumber(phone);
                currentUser.setState(UserState.DONE);

                sendMessage(chatId,
                        "✅ Raqamingiz qabul qilindi:\n" + phone
                );

                // ADMIN NOTIFICATION
                sendMessage(ADMIN_ID,
                        "📥 Yangi user:\n" +
                                "Ism: " + currentUser.getFirstName() + "\n" +
                                "Tel: " + phone
                );
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // ================= USER FIND =================
    private TelegramState findUser(Long chatId) {

        if (users.containsKey(chatId)) {
            return users.get(chatId);
        }

        TelegramState user = new TelegramState();
        user.setChatId(chatId);
        user.setState(UserState.START);

        users.put(chatId, user);

        return user;
    }

    // ================= SEND MESSAGE =================
    private void sendMessage(Long chatId, String text) throws TelegramApiException {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        execute(message);
    }

    // ================= CONTACT KEYBOARD =================
    private void sendContactKeyboard(Long chatId) throws TelegramApiException {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        message.setText(
                "📱 Telefon raqamingizni yuboring:\n" +
                        "Ma'lumotlaringiz maxfiy saqlanadi."
        );

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        KeyboardButton button = new KeyboardButton("📞 Telefon raqamni yuborish");
        button.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);

        keyboard.setKeyboard(rows);

        message.setReplyMarkup(keyboard);

        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "MAFAI_Uzbot";
    }

    @Override
    public String getBotToken() {
        return "8923465159:AAEBmBQjZVIj1FQacIrHQZ1x_8XuP2dTm1k";
    }
}