package niix.dan.consolediscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class Listener extends ListenerAdapter {
    private ConsoleDiscord plugin;
    private JDA jda;

    public Listener(ConsoleDiscord plugin, JDA j) {
        this.plugin = plugin;
        this.jda = j;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent msg) {
        if(msg.getAuthor().getId().equalsIgnoreCase(jda.getSelfUser().getId())) return;
        if(plugin.getConfig().getBoolean("Config.Debug")) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GRAY +
                    "\n[DEBUG] MessageReceived: "
                    + "\n - Channel: "+msg.getChannel().getId()
                    + "\n - IsBot: "+msg.getAuthor().isBot()
                    + "\n - MessageContent: "+msg.getMessage().getContentRaw()
            );
        }

        if(msg.getAuthor().isBot()) return;
        if(!msg.getChannelType().isGuild()) return;
        if(!msg.getChannel().getId().equalsIgnoreCase(plugin.getConfig().getString("Discord-Bot.Channel"))) return;

        if(plugin.getConfig().getBoolean("Discord-Bot.Whitelist.Enabled") && !plugin.getConfig().getStringList("Discord-Bot.Whitelist.List").contains(msg.getAuthor().getId())) {
            msg.getMessage().reply(plugin.getConfig().getString("Discord-Bot.Whitelist.Message")).submit();
            return;
        }

        String message = msg.getMessage().getContentRaw();
        (new BukkitRunnable() {
           public void run() {
               String ms = message;
               if(plugin.getConfig().getBoolean("Config.Filter.Enabled")) ms = ms.replaceAll(plugin.getConfig().getString("Config.Filter.RegEx"), plugin.getConfig().getString("Config.Filter.Replace"));

               Bukkit.getConsoleSender().sendMessage(msg.getAuthor().getName()+ " -> "+ms);
               Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), ms);
           }
        }).runTask(plugin);
    }
}
