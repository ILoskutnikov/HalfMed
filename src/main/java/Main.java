import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
public class Main extends TelegramLongPollingBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.getMessage().getChat().getUserName() + " said: " + update.getMessage().getText());
        SendMessage sendMessage = new SendMessage();            // Создаем объект, в котором опишем сообщение, которое хотим послать в ответ
        sendMessage.setChatId(update.getMessage().getChatId().toString());  // Укажем, что отправить это сообщение следует в чате, из которого мы получили сообщение
        sendMessage.setText(update.getMessage().getText());      // Укажем текст сообщения
        try {
            execute(sendMessage);                           // Отправим сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace();                                // Это обработка исключительных ситуаций - на случай если что-то пойдет не так
        }
    }


    @Override
    public String getBotUsername() {
        return "HalfMedBot";
    }

    @Override
    public String getBotToken() {
        return "923890060:AAFoIS_UrA8P90zSAQ_xcCpGQkUSwPHv5uI";
    }
}