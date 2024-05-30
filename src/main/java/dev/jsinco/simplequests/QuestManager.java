package dev.jsinco.simplequests;

import dev.jsinco.abstractjavafilelib.ConfigurationSection;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.objects.Quest;
import dev.jsinco.simplequests.objects.QuestPlayer;
import dev.jsinco.simplequests.storage.DataManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public final class QuestManager {

    private static final ConcurrentLinkedQueue<Quest> quests = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<String, List<Quest>> mappedQuests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, QuestPlayer> questPlayersCache = new ConcurrentHashMap<>();
    private static final DataManager dataManager = SimpleQuests.getDataManager();
    private static final SimpleQuests instance = SimpleQuests.getInstance();

    public static BukkitRunnable asyncCacheManager() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (final QuestPlayer questPlayer : questPlayersCache.values()) {
                    dataManager.saveQuestPlayer(questPlayer);
                    if (questPlayer.getActiveQuests().isEmpty() || questPlayer.getPlayer() == null || !questPlayer.getPlayer().isOnline()) {
                        questPlayersCache.remove(questPlayer.getUuid());
                        Util.debugLog("Uncaching QuestPlayer: " + questPlayer.getUuid());
                    }
                }
            }
        };
    }

    public static void loadQuests() { // async
        quests.clear();
        mappedQuests.clear();
        final SnakeYamlConfig questsFile = SimpleQuests.getQuestsFile();

        for (final String category : questsFile.getKeys()) {
            final ConfigurationSection categorySection = questsFile.getConfigurationSection(category);

            for (final String id : categorySection.getKeys()) {
                try {
                    final ConfigurationSection questSection = categorySection.getConfigurationSection(id);
                    final QuestAction questAction = QuestAction.valueOf(questSection.getString("action"));
                    final List<String> description = questSection.get("description") != null ? (List<String>) questSection.get("description") : null;

                    final Quest quest = new Quest(
                            category,
                            id,
                            questSection.getString("name"),
                            questSection.getString("type").toUpperCase(),
                            questAction,
                            questSection.getInt("amount"),
                            description,
                            questSection.getString("reward.type"),
                            questSection.get("reward.value"),
                            questSection.getString("menu-item"),
                            questSection.getString("required")
                    );

                    quests.add(quest);
                    mappedQuests.computeIfAbsent(category, k -> new ArrayList<>()).add(quest);
                } catch (Exception e) {
                    instance.getLogger().log(Level.SEVERE, "Failed to load quest: " + category + ":" + id, e);
                }
            }

        }

        for (Map.Entry<String, List<Quest>> entry : mappedQuests.entrySet()) {
            Util.debugLog("Loaded " + entry.getValue().size() + " quests for category: " + entry.getKey());
        }
        Util.debugLog("Finished loading " + quests.size() + " quests");
    }

    @Nullable
    public static Quest getQuest(String category, String id) { // async?
        return quests.stream()
                .filter(quest -> quest.getCategory().equals(category) && quest.getId().equals(id))
                .findFirst()
                .orElse(null);
    }


    public static QuestPlayer getQuestPlayer(UUID uuid) {
        if (questPlayersCache.containsKey(uuid)) {
            return questPlayersCache.get(uuid);
        }

        final QuestPlayer questPlayer = dataManager.loadQuestPlayer(uuid);
        if (!questPlayer.getActiveQuests().isEmpty()) {
            cacheQuestPlayer(questPlayer);
        }
        return questPlayer;
    }

    @Nullable
    public static QuestPlayer questPlayerFromCache(UUID uuid) {
        return questPlayersCache.get(uuid);
    }

    public static void uncacheQuestPlayer(UUID uuid) {
        questPlayersCache.remove(uuid);
        Util.debugLog("Uncached QuestPlayer: " + uuid);
    }

    public static void cacheQuestPlayer(QuestPlayer questPlayer) {
        if (!questPlayersCache.containsKey(questPlayer.getUuid())) {
            questPlayersCache.put(questPlayer.getUuid(), questPlayer);
            Util.debugLog("Cached QuestPlayer: " + questPlayer.getUuid());
        } else {
            Util.debugLog("QuestPlayer already cached: " + questPlayer.getUuid());
        }
    }

    public static List<QuestPlayer> getQuestPlayers() {
        return List.copyOf(questPlayersCache.values());
    }

    public static List<Quest> getQuests() {
        return List.copyOf(quests);
    }

    @Nullable
    public static List<Quest> getQuests(String category) {
        if (!mappedQuests.containsKey(category)) return null;
        return List.copyOf(mappedQuests.get(category));
    }
}
