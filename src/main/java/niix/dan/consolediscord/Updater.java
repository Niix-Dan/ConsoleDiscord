package niix.dan.consolediscord;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.kohsuke.github.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Updater {
    private JavaPlugin plugin;
    private String owner = "Niix-Dan";
    private String repo = "Resources";
    private String[] libs = {"JDA.jar", "SLF4J.jar", "LOG4J.jar"};
    private Http http = new Http();

    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;
        createWorkspace();
        checkAndUpdateLibs();
    }

    private void createWorkspace() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File libsFolder = new File(dataFolder, "Libs");
        if (!libsFolder.exists()) {
            libsFolder.mkdirs();
        }
    }

    private void downloadLib(String libName) {
        String downloadUrl = String.format("https://github.com/%s/%s/releases/latest/download/%s", owner, repo, libName);
        String destinationPath = new File(plugin.getDataFolder(), "Libs/" + libName).getPath();

        http.download(downloadUrl, destinationPath);
    }

    private void checkAndUpdateLibs() {
        File libsFolder = new File(plugin.getDataFolder(), "Libs");

        // Verificar se todas as libs estão presentes
        boolean allLibsPresent = true;
        for (String lib : libs) {
            File libFile = new File(libsFolder, lib);
            if (!libFile.exists()) {
                allLibsPresent = false;
                break;
            }
        }

        if(!allLibsPresent || plugin.getConfig().getBoolean("Config.LibsUpdater", false)) {
            GitHub github;
            try {
                github = new GitHubBuilder().build();
                GHRelease latestRelease = github.getRepository(owner + "/" + repo).getLatestRelease();

                if (latestRelease != null) {
                    String latestVersion = latestRelease.getTagName();
                    String storedVersion = plugin.getConfig().getString("Config.Libs_version", "");

                    if (!latestVersion.equals(storedVersion)) {
                        plugin.getConfig().set("Config.Libs_version", latestVersion);

                        for (String lib : libs) {
                            downloadLib(lib);
                        }

                        plugin.saveConfig();
                        plugin.getLogger().log(Level.parse(plugin.getConfig().getString("Config.LogLevel", "INFO").replaceAll("DEBUG", "CONFIG")), ChatColor.GREEN + "Libraries updated to version " + latestVersion);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
