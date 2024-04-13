package niix.dan.consolediscord;

import org.bukkit.ChatColor;

import java.io.File;

public class Updater {
    private ConsoleDiscord plugin;
    private String JDA_Lib = "https://github.com/Niix-Dan/Resources/releases/download/DiscordConsole/JDA.jar";
    private String SLF4J_Lib = "https://github.com/Niix-Dan/Resources/releases/download/DiscordConsole/SLF4J.jar";
    private String LOG4J_Lib = "https://github.com/Niix-Dan/Resources/releases/download/DiscordConsole/LOG4J.jar";


    Updater(ConsoleDiscord plugin) {
        this.plugin = plugin;

        CreateWorkspace();
        CheckUpdates();
    }

    public void CreateWorkspace() {
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder().getPath() + "/Libs");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void DownloadLibs(boolean update) {
        Http http = new Http();
        String path = plugin.getDataFolder().getPath().toString();

        if (!new File(path + "/Libs/JDA.jar").exists()) {
            if(!update) System.out.println(ChatColor.RED + "/Libs/JDA.jar not found! downloading...");
            else System.out.println(ChatColor.RED + "Updating /Libs/JDA.jar [...]");
            http.download(JDA_Lib, path + "/Libs/JDA.jar");
        }
        if (!new File(path + "/Libs/SLF4J.jar").exists()) {
            if(!update) System.out.println(ChatColor.RED + "/Libs/SLF4J.jar not found! downloading...");
            else System.out.println(ChatColor.RED + "Updating /Libs/SLF4J.jar [...]");
            http.download(SLF4J_Lib, path + "/Libs/SLF4J.jar");
        }
        if (!new File(path + "/Libs/LOG4J.jar").exists()) {
            if(!update) System.out.println(ChatColor.RED + "/Libs/LOG4J.jar not found! downloading...");
            else System.out.println(ChatColor.RED + "Updating /Libs/LOG4J.jar [...]");
            http.download(LOG4J_Lib, path + "/Libs/LOG4J.jar");
        }
    }


    // Ugly way to check updates, but im sleepy
    public void CheckUpdates() {
        boolean isUpdate = false;
        if(plugin.getConfig().getBoolean("Config.LibsUpdater")) {
            String version = plugin.getConfig().getString("Config.Libs_version");

            Http http = new Http();
            String[] infos = http.get("https://raw.githubusercontent.com/Niix-Dan/Resources/main/ConsoleDiscord-Data").split("-");
            if(!infos[0].equalsIgnoreCase(version)) {
                plugin.getConfig().set("Config.Libs_version", infos[0]);
                String path = plugin.getDataFolder().getPath().toString();

                if(infos[1].equalsIgnoreCase("1") && new File(path + "/Libs/JDA.jar").exists()) {isUpdate = true;new File(path + "/Libs/JDA.jar").delete();}
                if(infos[2].equalsIgnoreCase("1") && new File(path + "/Libs/SLF4J.jar").exists()) {isUpdate = true;new File(path + "/Libs/SLF4J.jar").delete();}
                if(infos[3].equalsIgnoreCase("1") && new File(path + "/Libs/LOG4J.jar").exists()) {isUpdate = true;new File(path + "/Libs/LOG4J.jar").delete();}
            }
        }
        DownloadLibs(isUpdate);
    }
}
