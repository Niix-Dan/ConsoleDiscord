package niix.dan.consolediscord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;


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
        registerCmds();

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

    private void registerCmds() {
        getCommand("consolediscord").setExecutor(this);
    }

    private void checkWarns() {
        if(getConfig().getString("Discord-Bot.Token").isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Discord-Bot.Token is empty");
            canRun = false;
        }

        if(getConfig().getString("Discord-Bot.Channel").isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Discord-Bot.Channel is empty");
            canRun = false;
        }
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender.hasPermission("consolediscord.cd") || sender.isOp())) {
            sender.sendMessage(getConfig().getString("Messages.NoPermMsg").replaceAll("&", "§")); return true;
        }

        if(args.length <= 0) {
            sender.sendMessage(ChatColor.RED+cmd.getUsage()); return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE+"Reloading config...");
            reloadConfig();
            sender.sendMessage(ChatColor.LIGHT_PURPLE+"Done!");
        }

        if(args[0].equalsIgnoreCase("jda")) {
            if(args.length <= 1) {sender.sendMessage(ChatColor.RED+cmd.getUsage()); return true;}

            if(args[1].equalsIgnoreCase("restart")) {
                try {
                    sender.sendMessage(ChatColor.RED+"Restarting JDA & Appender...");
                    this.jda.shutdownNow();
                    this.appender.jda.shutdownNow();
                    this.appender.stop();

                    Bot();
                    sender.sendMessage(ChatColor.GREEN+"Done! JDA Restated Successfully");
                    sender.sendMessage(ChatColor.GREEN+"Done! Appender Restated Successfully");
                } catch(Exception exx) {
                    sender.sendMessage(ChatColor.RED+"Error! Please check the logs.");
                }
            }
        }

        return false;
    }


    public void Bot() {
        if(canRun) {
            try {
                this.jda = JDABuilder.createDefault(getConfig().getString("Discord-Bot.Token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).build();
                jda.addEventListener(new Object[] { new Listener(this, this.jda) });
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"JDA Login Successfully - Bot Online");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"JDA Login Error: "+e.getMessage());
            }

            try {
                RunAppender();
            } catch(Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"JDA Login Error: "+e.getMessage());
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
