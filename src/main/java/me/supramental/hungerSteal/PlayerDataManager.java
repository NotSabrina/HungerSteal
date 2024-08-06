package me.supramental.hungerSteal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {
    private final HungerSteal plugin;
    private final File playerDataFile;
    private FileConfiguration playerDataConfig;

    public PlayerDataManager(HungerSteal plugin) {
        this.plugin = plugin;
        playerDataFile = new File(plugin.getDataFolder(), "Players.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        playerDataConfig.set(uuid.toString() + ".name", player.getName());
        playerDataConfig.set(uuid.toString() + ".hunger", player.getFoodLevel());
        saveConfig();
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerDataConfig.contains(uuid.toString() + ".hunger")) {
            int hunger = playerDataConfig.getInt(uuid.toString() + ".hunger");
            player.setFoodLevel(hunger);
        }
    }

    private void saveConfig() {
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllPlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayerData(player);
        }
    }
}