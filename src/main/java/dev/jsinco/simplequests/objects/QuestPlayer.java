package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.QuestManager;
import dev.jsinco.simplequests.SimpleQuests;
import dev.jsinco.simplequests.Util;
import dev.jsinco.simplequests.enums.QuestAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QuestPlayer {

    @Nullable
    private Player player = null;
    private final UUID uuid;
    private final List<String> completedQuests;
    private final ConcurrentLinkedQueue<ActiveQuest> activeQuests;
    private final int maxActiveQuests;

    public QuestPlayer(UUID uuid, List<String> completedQuests, List<ActiveQuest> activeQuests) {
        this.uuid = uuid;
        this.completedQuests = new ArrayList<>(completedQuests);
        this.activeQuests = new ConcurrentLinkedQueue<>(activeQuests);
        this.maxActiveQuests = Objects.requireNonNull(getPlayer()).getEffectivePermissions().stream()
                    .filter(permission -> permission.getPermission().startsWith("simplequests.maxquests."))
                    .map(permission -> Integer.parseInt(permission.getPermission().replace("simplequests.maxquests.", "")))
                    .max(Integer::compareTo)
                    .orElse(SimpleQuests.getConfigFile().getInt("max-default-quests"));
    }

    @Nullable
    public Player getPlayer() {
        if (player == null) {
            final Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                player = p;
            }
        }
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<String> getCompletedQuests() {
        return completedQuests;
    }

    public List<ActiveQuest> getActiveQuests() {
        return List.copyOf(activeQuests);
    }

    public int getMaxActiveQuests() {
        return maxActiveQuests;
    }

    public void updateQuests(String type, QuestAction action, int amount) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (!activeQuest.getType().equals(type) || activeQuest.getQuestAction() != action) continue;
            activeQuest.setProgress(activeQuest.getProgress() + amount);

            if (activeQuest.getAmount() <= activeQuest.getProgress()) {
                activeQuests.remove(activeQuest);
                completedQuests.add(activeQuest.simpleIdentifier());

                activeQuest.executeReward(getPlayer());
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have completed the quest: &a\"" + activeQuest.getName() + "\"&r!"));
                Util.debugLog(uuid + " completed quest: " + activeQuest.getId());
            }
        }
    }

    public boolean startQuest(Quest quest) {
        if (activeQuests.size() >= maxActiveQuests) {
            return false;
        }

        final ActiveQuest activeQuest = new ActiveQuest(quest);

        for (ActiveQuest aQ : activeQuests) {
            if (aQ.simpleIdentifier().equals(quest.simpleIdentifier())) {
                return false;
            }
        }

        if (completedQuests.contains(quest.simpleIdentifier())) {
            return false;
        }

        activeQuests.add(activeQuest);
        QuestManager.cacheQuestPlayer(this);
        return true;
    }

    public boolean hasCompletedQuest(Quest quest) {
        return completedQuests.contains(quest.simpleIdentifier());
    }

    @Nullable
    public ActiveQuest getInProgressQuest(Quest quest) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (activeQuest.simpleIdentifier().equals(quest.simpleIdentifier())) {
                return activeQuest;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "QuestPlayer{" +
                "uuid=" + uuid +
                ", completedQuestIds=" + completedQuests +
                ", activeQuests=" + activeQuests +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuestPlayer that = (QuestPlayer) obj;
        return uuid.equals(that.uuid);
    }
}
