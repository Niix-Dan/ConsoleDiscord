package niix.dan.consolediscord;

import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;

import java.time.LocalTime;

public class Appender extends AbstractAppender {
    private ConsoleDiscord plugin;
    public JDA jda;
    private String messages = "";

    public Appender(ConsoleDiscord plugin, JDA j) {
        super("ConsoleDiscordLogAppender", null, null);
        this.plugin = plugin;
        this.jda = j;
    }

    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        boolean shouldIgnore = false;

        for(String txt : plugin.getConfig().getStringList("Config.IgnoreWords")) {
            if(message.contains(txt)) shouldIgnore = true;
        }

        if(!shouldIgnore) {
            message = "["+ LocalTime.now() + " " + event.getLevel().toString() + "]:" + message + "\n";
            this.messages+=message;
        }
    }

    public void sendMessages() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    String msg = messages;
                    if(!msg.isEmpty()) {
                        //msg = msg.replaceAll("\033\\[[;\\d]*m", "");
                        msg = msg.replaceAll("(?:[&§][a-fk-oru0-9])", ""); // Color codes filter
                        msg = msg.replaceAll("\\p{C}", ""); // Removing ESC char


                        if(msg.length() >= 2000) {
                            String messageTooLong = plugin.getConfig().getString("Messages.Discord.MessageLimit", "\n\nThis message exceeded discord's 2,000 character limit. To see the complete log look in the console!");

                            msg = msg.substring(0, 1999 - messageTooLong.length() - 8);
                            msg = msg + messageTooLong;
                        }

                        jda.getTextChannelById(plugin.getConfig().getString("Discord-Bot.Channel")).sendMessage("```" + msg + "```").submit();
                    }
                } catch(NullPointerException ex) {
                    Bukkit.getConsoleSender().sendMessage(ex.getMessage());
                }
                messages = "";
            }
        }, 0L, plugin.getConfig().getLong("Config.LogDelay", 20L));
    }
}
