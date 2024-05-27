package dev.jsinco.simplequests;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.simplequests.commands.CommandManager;
import dev.jsinco.simplequests.listeners.Events;
import dev.jsinco.simplequests.objects.QuestPlayer;
import dev.jsinco.simplequests.storage.DataManager;
import dev.jsinco.simplequests.storage.FlatFileStorage;
import dev.jsinco.simplequests.storage.SQLiteStorage;
import dev.jsinco.simplequests.enums.StorageMethod;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleQuests extends JavaPlugin {

    private static SnakeYamlConfig questsFile;
    private static SnakeYamlConfig configFile;
    private static DataManager dataManager;
    private static SimpleQuests instance;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        FileLibSettings.set(getDataFolder(), getLogger());
        instance = this;
        loadData();


        QuestManager.asyncCacheManager().runTaskTimerAsynchronously(this, 0L, 1200L);
        getServer().getScheduler().runTaskAsynchronously(this, QuestManager::loadQuests);

        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("simplequests").setExecutor(new CommandManager(this));
    }


    @Override
    public void onDisable() {
        for (QuestPlayer questPlayer : QuestManager.getQuestPlayers()) {
            dataManager.saveQuestPlayer(questPlayer);
        }
    }

    public static void loadData() {
        configFile = new SnakeYamlConfig("config.yml");
        questsFile = new SnakeYamlConfig("quests.yml");


        switch (StorageMethod.valueOf(configFile.getString("storage-method").toUpperCase())) {
            case FLATFILE -> dataManager = new FlatFileStorage();
            case SQLITE -> dataManager = new SQLiteStorage();
        }
    }

    public static SnakeYamlConfig getConfigFile() {
        return configFile;
    }

    public static SnakeYamlConfig getQuestsFile() {
        return questsFile;
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static SimpleQuests getInstance() {
        return instance;
    }

    // Load all quests from quests.yml
    // While player is online, load all quests that have been started, their progression, and all completed quests
    // When an action is performed, check if the player has any active quests that match the action and update the progression
    // When a quest is completed, remove it from the active quests and add it to the completed quests
}