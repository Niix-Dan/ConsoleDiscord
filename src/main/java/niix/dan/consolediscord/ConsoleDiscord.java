package niix.dan.consolediscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;

import java.util.logging.Level;


public final class ConsoleDiscord extends JavaPlugin {
    public static ConsoleDiscord plugin;
    public boolean canRun = true;
    public JDA jda;
    public niix.dan.consolediscord.Appender appender;

    @Override
    public void onEnable() {
        plugin = this;

      /*.---.  .----. .-. .-. .----. .----. .-.   .----.   .----. .-. .----. .---.  .----. .----. .----.
       /  ___}/  {}  \|  `| |{ {__  /  {}  \| |   | {_     | {}  \| |{ {__  /  ___}/  {}  \| {}  }| {}  \
       \     }\      /| |\  |.-._} }\      /| `--.| {__    |     /| |.-._} }\     }\      /| .-. \|     /
        `---'  `----' `-' `-'`----'  `----' `----'`----'   `----' `-'`----'  `---'  `----' `-' `-'`----'*/

        registerConfigs();
        //registerCmds();

        checkWarns();
        new Updater(this);

        Bot();
    }

    @Override
    public void onDisable() {
        plugin = null;
        try {
            this.jda.shutdown();
            this.appender.jda.shutdown();
            this.appender.stop();
        } catch(Exception ex) {
            // Failed shutdown
        }
    }

    private void registerConfigs() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void checkWarns() {
        if(getConfig().getString("Discord-Bot.Token").isEmpty()) {
            plugin.getLogger().log(Level.WARNING, ChatColor.RED + "Discord-Bot.Token is empty");
            canRun = false;
        }

        if(getConfig().getString("Discord-Bot.Channel").isEmpty()) {
            plugin.getLogger().log(Level.WARNING, ChatColor.RED + "Discord-Bot.Channel is empty");
            canRun = false;
        }
    }


    public void Bot() {
        if(canRun) {
            try {
                this.jda = JDABuilder.createDefault(getConfig().getString("Discord-Bot.Token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).build();
                jda.addEventListener(new Object[] { new Listener(this, this.jda) });
                plugin.getLogger().log(Level.parse("INFO"), ChatColor.GREEN+"JDA Login Successfully - Bot Online");
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, ChatColor.RED+"JDA Login Error: "+e.getMessage());
            }

            try {
                RunAppender();
            } catch(Exception e) {

                plugin.getLogger().log(Level.WARNING, ChatColor.RED+"JDA Login Error: "+e.getMessage());
            }
        }
    }

    public void RunAppender() {
        this.appender = new niix.dan.consolediscord.Appender(plugin, jda);

        try {
            Logger log = (Logger) LogManager.getRootLogger();
            log.addAppender((Appender) this.appender);
        } catch(Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
        }

        this.appender.sendMessages();
    }
}
