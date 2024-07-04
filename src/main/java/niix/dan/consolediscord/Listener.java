package niix.dan.consolediscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class Listener extends ListenerAdapter {
    private ConsoleDiscord plugin;
    private JDA jda;

    public Listener(ConsoleDiscord plugin, JDA j) {
        this.plugin = plugin;
        this.jda = j;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().getId().equalsIgnoreCase(jda.getSelfUser().getId())) return;
        if(plugin.getConfig().getString("Config.LogLevel", "INFO").equalsIgnoreCase("DEBUG")) { logDebugMessage(event); }
        if(event.getAuthor().isBot()) return;
        if(!event.getChannelType().isGuild()) return;
        if(!event.getChannel().getId().equalsIgnoreCase(plugin.getConfig().getString("Discord-Bot.Channel"))) return;

        if (plugin.getConfig().getBoolean("Discord-Bot.Whitelist.Enabled") && !isUserWhitelisted(event.getAuthor().getId())) {
            event.getMessage().reply(plugin.getConfig().getString("Discord-Bot.Whitelist.Message")).submit();
            return;
        }

        String messageContent = event.getMessage().getContentRaw();
        if(plugin.getConfig().getBoolean("Config.Filter.Enabled", false)) {
            messageContent = messageContent.replaceAll(
                    plugin.getConfig().getString("Config.Filter.RegEx", ""),
                    plugin.getConfig().getString("Config.Filter.Replace", "")
            );
        }

        String msg = messageContent;
        (new BukkitRunnable() {
            public void run() {
                plugin.getLogger().log(Level.parse(plugin.getConfig().getString("Config.LogLevel").replaceAll("DEBUG", "CONFIG")), event.getAuthor().getName() + " -> " + msg);
                Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), msg);
            }
        }).runTask(plugin);
    }

    private void logDebugMessage(MessageReceivedEvent event) {
        plugin.getLogger().log(Level.parse(plugin.getConfig().getString("Config.LogLevel").replaceAll("DEBUG", "CONFIG")),
                ChatColor.GRAY +
                        "\n[DEBUG] MessageReceived: "
                        + "\n - Channel: " + event.getChannel().getId()
                        + "\n - IsBot: " + event.getAuthor().isBot()
                        + "\n - MessageContent: " + event.getMessage().getContentRaw()
        );
    }

    private boolean isUserWhitelisted(String userId) {
        return plugin.getConfig().getStringList("Discord-Bot.Whitelist.List").contains(userId);
    }
}
