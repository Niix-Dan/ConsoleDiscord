package me.daniel.consolediscord;


import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author battl
 */
public class ConsoleDiscord extends JavaPlugin {
    public static ConsoleDiscord plugin;
    public String token = "";
    public String channelId = "";
    public appender appender;
    public JDA jda;
    @Override
    public void onEnable() {
        plugin = this;
        loadConfig();
        download();
        
        if(token.isEmpty() || channelId.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§c[ConsoleDiscord] §c§lPor favor, Coloque §a§ltodas§c§l as informações do bot!");
        } else {
            try {
                this.jda = JDABuilder.createDefault(this.token, GatewayIntent.GUILD_MESSAGES).build();
                jda.addEventListener(new Object[] { new Listener(this, this.jda) });
                Bukkit.getConsoleSender().sendMessage("§a§l[ConsoleDiscord] §a§lLogin feito com sucesso! Capturando console...");
            } catch (LoginException ex) {
                Bukkit.getConsoleSender().sendMessage("§c§l[ConsoleDiscord] §c§lNão foi possível fazer login com o discord.");
            }

            logger();
        }
    }
    @Override
    public void onDisable() {
        plugin = null;
        this.jda.shutdown();
        appender.jda.shutdown();
        this.appender.stop();
    }
    
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        
        this.token = getConfig().getString("Token");
        this.channelId = getConfig().getString("Canal");
    }
  
    
    private void download() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder().getPath() + "/libs");
        if (!file.exists()) {
            file.mkdir();
        }
        Http http = new Http();
        File jdaJarfile = new File(getDataFolder().getPath() + "/libs/JDA-4.2.0_168-withDependencies.jar");
        File slfJarfile = new File(getDataFolder().getPath() + "/libs/slf4j-simple-1.7.26.jar");
        File log4jJarfile = new File(getDataFolder().getPath() + "/libs/log4j-core-2.17.1.jar");
        
        if (!jdaJarfile.exists()) {
            System.out.println("§c§l[ConsoleDiscord] JDA API não encontrada! Baixando....");
            http.get("https://github.com/Daniel-code15/Resources/releases/download/DiscordConsole/JDA-4.2.0_168-withDependencies.jar", getDataFolder().getPath().toString() + "/libs/JDA-4.2.0_168-withDependencies.jar");
            System.out.println("§a&l[ConsoleDiscord] JDA API baixada com sucesso!");
        } 
        if (!slfJarfile.exists()) {
            System.out.println("§c§l[ConsoleDiscord] SLF4J não encontrado! Baixando...");
            http.get("https://github.com/Daniel-code15/Resources/releases/download/DiscordConsole/slf4j-simple-1.7.26.jar", getDataFolder().getPath().toString() + "/libs/slf4j-simple-1.7.26.jar");
            System.out.println("§a§l[ConsoleDiscord] SLF4J baixado com sucesso!");
        } 
        if (!log4jJarfile.exists()) {
            System.out.println("§c§l[ConsoleDiscord] LO4J não encontrado! Baixando...");
            http.get("https://github.com/Daniel-code15/Resources/releases/download/DiscordConsole/log4j-core-2.17.1.jar", getDataFolder().getPath().toString() + "/libs/log4j-core-2.17.1.jar");
            System.out.println("§a§l[ConsoleDiscord] LO4J baixado com sucesso!!");
        }
    }
  
    private void logger() {
        this.appender = new appender(plugin, jda, channelId);
        try {
            Logger logger = (Logger)LogManager.getRootLogger();
            logger.addAppender((Appender)this.appender);
        } catch (Exception exception) {
            // Erro
        }
        this.appender.sendMessages();
    }
}
