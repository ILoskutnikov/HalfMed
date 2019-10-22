import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class HalfMedBot extends AbilityBot {

    public HalfMedBot(){
        this(Constans.BOT_TOKEN, Constans.BOT_USERNAME);
    }

    public HalfMedBot(String botToken, String botUsername) {
        super(botToken, botUsername);
    }

    @Override
    public int creatorId() {
        return 0;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {

    }

    @Override
    public void onClosing() {

    }
}
