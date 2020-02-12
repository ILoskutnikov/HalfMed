package pack;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

@Log4j2
public class Main extends TelegramLongPollingBot {

    private static HashMap<Integer, HalfLivesOfDrugs> drugsList = new HashMap<>();
    private static String idOfDrug = "";
    private static Double countOfDose;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner reader = new Scanner(new File("halfLive.csv"));
        int i = 0;
        while (reader.hasNextLine()) {
            List<String> stringForParce;
            stringForParce = Arrays.asList(reader.nextLine().split("\t"));
            drugsList.put(
                    i++,
                    new HalfLivesOfDrugs(
                            stringForParce.get(0),
                            parseInt(stringForParce.get(1)),
                            parseDouble(stringForParce.get(2)),
                            parseDouble(stringForParce.get(3))));
        }

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    private static SendMessage sendInlineKeyBoardMessage(long chatId) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 0; i < drugsList.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(drugsList.get(i).getDrugName());
            inlineKeyboardButton1.setCallbackData(i + "\t" + drugsList.get(i).getDrugName());
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton2.setText(drugsList.get(++i).getDrugName());
            inlineKeyboardButton2.setCallbackData(i + "\t" + drugsList.get(i).getDrugName());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton1);
            keyboardButtonsRow.add(inlineKeyboardButton2);
            rowList.add(keyboardButtonsRow);

        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Ну и что за колеса ты употреблял?").setReplyMarkup(inlineKeyboardMarkup);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                log.info(update.getMessage().getChat().getUserName() + " said: " + update.getMessage().getText());
                boolean flag = true;
                switch (update.getMessage().getText()) {
                    case "/showMeDrugs": {
                        try {
                            execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                            flag = false;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case "/start": {
                        try {
                            execute(new SendMessage().setChatId(update.getMessage().getChatId()).setText("Если вы хотите рассчитать плановое время пьянки, но не знаете, через сколько можно пить после колес, что вы приняли, то пишите боту /showMeDrugs и посмотрите насколько глубока жопа трезвеничества в которую вы попали."));
                            flag = false;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                if (flag) {
                    if (update.getMessage().getText().contains("Dose ")) {
                        String text = update.getMessage().getText().split(" ")[1];
                        if (text.length() > 10 || !StringUtils.isNumeric(text)) {
                            try {
                                execute(new SendMessage().setChatId(update.getMessage().getChatId()).setText("Дима, еб твою мать! Тебе вообще никогда пить нельзя, и к ботам пускать нельзя, иди нахуй!"));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                countOfDose = Double.parseDouble(update.getMessage().getText().split(" ")[1]);
                                execute(new SendMessage().setChatId(update.getMessage().getChatId()).setText(calulateAlkoTime()));
                            } catch (Exception e) {
                                try {
                                    e.printStackTrace();
                                    execute(new SendMessage().setChatId(update.getMessage().getChatId()).setText("Дорогой мой человек, я тебя не понял, перечитай, что написано выше и следуй, блин, инструкциям!\n Это тебе надо или мне?"));
                                } catch (TelegramApiException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String[] callback = update.getCallbackQuery().getData().split("\t");
            String nameOfDrug = callback[1];
            idOfDrug = callback[0];
            log.info("choice: " + nameOfDrug);
            try {
                execute(new SendMessage().setText("Ага, значит колеса - это " + nameOfDrug + "...").setChatId(update.getCallbackQuery().getMessage().getChatId()));
                Thread.sleep(1000);
                String drug = nameOfDrug.equals("Терафлю") ? "Терафлю" : (nameOfDrug + "а");
                execute(new SendMessage().setText("А сколько ты " + drug + " выпил?").setChatId(update.getCallbackQuery().getMessage().getChatId()));
                Thread.sleep(1000);
                execute(new SendMessage().setText("Значит так, одна доза " + drug + " содержит " + drugsList.get(Integer.parseInt(idOfDrug)).getOneDose() + " мг. действующего вещества. \nТеперь не поленись и посчитай, сколько ты выпил в дозах этих своих колес. А после этого напиши мне Dose*пробел**получившееся число*. \nИзи же, ну.").setChatId(update.getCallbackQuery().getMessage().getChatId()));
                Thread.sleep(1000);
                execute(new SendMessage().setText("Пример: Dose 4").setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String calulateAlkoTime() {
        Double time = drugsList.get(Integer.parseInt(idOfDrug)).getMaxConcentration();
        double conc = (drugsList.get(Integer.parseInt(idOfDrug)).getOneDose() * countOfDose);
        while (conc > 2) {
            conc = conc / 2;
            time += drugsList.get(Integer.parseInt(idOfDrug)).getHalfLife();
        }


        return "Дорогой мой человек, придется тебе пережить без алкоголя еще " + (time) + " часов.\nСочувствую, держись.\n\nЕсли захочешь пересчитать, нажми /showMeDrugs.";
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