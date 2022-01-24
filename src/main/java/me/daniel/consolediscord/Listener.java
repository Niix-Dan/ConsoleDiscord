package me.daniel.consolediscord;

import java.util.Arrays;
import java.util.Date;
import java.util.function.BiConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
/**
 *
 * @author battl
 */
public class Listener extends ListenerAdapter {
    private ConsoleDiscord plugin;
    private JDA jda;

    public Listener(ConsoleDiscord plugin, JDA j) {
      this.plugin = plugin;
      this.jda = j;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!event.getChannelType().isGuild()) return;
        if(!event.getChannel().getId().equalsIgnoreCase(plugin.channelId)) return;
        
        (new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), event.getMessage().getContentRaw());
            }
        }).runTask(ConsoleDiscord.plugin); 
        
    }

}
