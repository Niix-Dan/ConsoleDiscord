
package me.daniel.consolediscord;


import com.google.gson.JsonElement;
import java.time.LocalTime;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;


/**
 *
 * @author battl
 */
public class appender extends AbstractAppender {
    private ConsoleDiscord plugin;
    public String messages = "";
    public JDA jda;
    public String channelId;
  
    public appender(ConsoleDiscord plugin, JDA j, String channel) {
        super("MyLogAppender", null, null);
        this.plugin = plugin;
        this.jda = j;
        this.channelId = channel;
    }
  
    public void append(LogEvent event) {
      String message = event.getMessage().getFormattedMessage();
      message = "[" + LocalTime.now() + " " + event.getLevel().toString() + "]: " + message + "\n";
      this.messages += message;
    }
    
    
    public void sendMessages() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    if(appender.this.messages.length() != 0) {
                        appender.this.messages = appender.this.messages.replaceAll("\033\\[[;\\d]*m", "");
                        if(appender.this.messages.length() > 2000) {
                            String messageTooLong = "\n\nEssa mensagem excedeu o limite de 2000 caracteres do discord. Para ver o log completo olhe no console!";
                            appender.this.messages = appender.this.messages.substring(0, 1999 - messageTooLong.length() - 6);
                            appender.this.messages = appender.this.messages + messageTooLong;
                            
                        }
                        appender.this.jda.getTextChannelById(plugin.channelId).sendMessage("```" + appender.this.messages + "```").submit();
                    }
                } catch (NullPointerException nullPointerException) {
                    // Erro
                }
                
                appender.this.messages = "";
            }
        }, 0L, 20L);
    }
}
