package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.QuestManager;
import dev.jsinco.simplequests.SimpleQuests;
import dev.jsinco.simplequests.Util;
import dev.jsinco.simplequests.enums.QuestAction;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QuestPlayer {

    @Nullable private Player player = null;
    private final UUID uuid;
    private final List<String> completedQuests;
    private final ConcurrentLinkedQueue<ActiveQuest> activeQuests;
    private boolean showActionBarProgress;

    public QuestPlayer(UUID uuid, List<String> completedQuests, ConcurrentLinkedQueue<ActiveQuest> activeQuests, boolean showActionBarProgress) {
        this.uuid = uuid;
        this.completedQuests = new ArrayList<>(completedQuests);
        this.activeQuests = new ConcurrentLinkedQueue<>(activeQuests);
        this.showActionBarProgress = showActionBarProgress;
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

    public ConcurrentLinkedQueue<ActiveQuest> getActiveQuestsQueue() {
        return activeQuests;
    }

    public boolean isShowActionBarProgress() {
        return showActionBarProgress;
    }

    public void setShowActionBarProgress(boolean showActionBarProgress) {
        this.showActionBarProgress = showActionBarProgress;
    }

    public void updateQuests(String type, QuestAction action, int amount) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (!activeQuest.getType().equals(type) || activeQuest.getQuestAction() != action) continue;

            activeQuest.setProgress(activeQuest.getProgress() + amount);

            if (showActionBarProgress) {
                getPlayer().sendActionBar(
                        MiniMessage.miniMessage().deserialize("<gold>Quest Progress<gray>: " + Util.createProgressBar(activeQuest, 25, "<#f498f6>|", "<#F7FFC9>|"))
                );
            }

            if (activeQuest.getAmount() <= activeQuest.getProgress()) {
                activeQuests.remove(activeQuest);
                completedQuests.add(activeQuest.fullIdentifier());

                activeQuest.executeReward(getPlayer());
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have completed the quest: &a\"" + activeQuest.getName() + "&a\"&r!"));
                Util.debugLog(uuid + " completed quest: " + activeQuest.getId());
            }
        }
    }

    public boolean startQuest(Quest quest) {
        if (getNumOfQuestsMatchingCategory(quest.getCategory()) >= getMaxQuests(quest.getCategory())) {
            Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have too many active quests in this category!"));
            return false;
        }

        if (quest.getRequiredCompletedQuest() != null && !completedQuests.contains(quest.getRequiredCompletedQuest())) {
            Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You must complete the quest: &a\"" + quest.getRequiredCompletedQuestObject().getName() + "&a\"&r!"));
            return false;
        }

        final ActiveQuest activeQuest = new ActiveQuest(quest);

        for (ActiveQuest aQ : activeQuests) {
            if (aQ.fullIdentifier().equals(quest.fullIdentifier())) {
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have already started this quest!"));
                return false;
            }
        }

        if (completedQuests.contains(quest.fullIdentifier())) {
            Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have already completed this quest!"));
            return false;
        }

        activeQuests.add(activeQuest);
        QuestManager.cacheQuestPlayer(this);
        Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have started the quest: &a\"" + quest.getName() + "&a\"&r!"));
        return true;
    }

    public boolean dropQuest(Quest quest) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (activeQuest.fullIdentifier().equals(quest.fullIdentifier())) {
                activeQuests.remove(activeQuest);
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have dropped the quest: &a\"" + quest.getName() + "&a\"&r!"));
                return true;
            }
        }
        Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have not started this quest!"));
        return false;
    }

    public boolean hasCompletedQuest(Quest quest) {
        if (quest == null) return true;
        return completedQuests.contains(quest.fullIdentifier());
    }

    public boolean hasCompletedQuest(String fullIdentifier) {
        if (fullIdentifier == null) return true;
        return completedQuests.contains(fullIdentifier);
    }

    public int getMaxQuests(String category) {
        final String permStr = "simplequests.maxquests." + category + ".";

        return Objects.requireNonNull(getPlayer()).getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith(permStr))
                .map(permission -> Integer.parseInt(permission.getPermission().replace(permStr, "")))
                .max(Integer::compareTo)
                .orElse(SimpleQuests.getConfigFile().getInt("categories." + category + ".default-max-quests"));
    }

    @Nullable
    public ActiveQuest getInProgressQuest(Quest quest) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (activeQuest.fullIdentifier().equals(quest.fullIdentifier())) {
                return activeQuest;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "QuestPlayer{" +
                "uuid=" + uuid +
                ", completedQuests=" + completedQuests +
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

    private int getNumOfQuestsMatchingCategory(String category) {
        return (int) activeQuests.stream().filter(activeQuest -> activeQuest.getCategory().equals(category)).count();
    }
}
