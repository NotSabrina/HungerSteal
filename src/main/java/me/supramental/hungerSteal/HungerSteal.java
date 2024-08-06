package me.supramental.hungerSteal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HungerSteal extends JavaPlugin implements Listener {

    private int hungerGain;
    private int hungerLoss;
    private int maxHunger;
    private List<String> disabledWorlds;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        playerDataManager = new PlayerDataManager(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                playerDataManager.saveAllPlayerData();
            }
        }.runTaskTimer(this, 20L, 20L); // 20 ticks = 1 second

        getLogger().info("HungerPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        playerDataManager.saveAllPlayerData();
        getLogger().info("HungerPlugin has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        hungerGain = config.getInt("hunger-gain", 4);
        hungerLoss = config.getInt("hunger-loss", 4);
        maxHunger = config.getInt("max-hunger", 20);
        disabledWorlds = config.getStringList("disabled-worlds");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            Player victim = event.getEntity();

            if (disabledWorlds.contains(killer.getWorld().getName())) {
                return;
            }

            int newHunger = Math.min(killer.getFoodLevel() + hungerGain, maxHunger);
            killer.setFoodLevel(newHunger);
            int newHungerVictim = Math.max(victim.getFoodLevel() - hungerLoss, 0);
            victim.setFoodLevel(newHungerVictim);
            playerDataManager.savePlayerData(killer);
            playerDataManager.savePlayerData(victim);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataManager.loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.savePlayerData(player);
    }
}