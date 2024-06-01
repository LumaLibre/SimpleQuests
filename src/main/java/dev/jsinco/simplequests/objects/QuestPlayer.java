package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.SimpleQuests;
import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.managers.QuestManager;
import dev.jsinco.simplequests.managers.Util;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
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
    private final List<String> achievementIds;
    private final ConcurrentLinkedQueue<ActiveQuest> activeQuests;
    private boolean showActionBarProgress;

    public QuestPlayer(UUID uuid, List<String> completedQuests, List<String> achievementIds, ConcurrentLinkedQueue<ActiveQuest> activeQuests, boolean showActionBarProgress) {
        this.uuid = uuid;
        this.completedQuests = new ArrayList<>(completedQuests);
        this.achievementIds = new ArrayList<>(achievementIds);
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

    public List<String> getAchievementIds() {
        return achievementIds;
    }

    public ConcurrentLinkedQueue<ActiveQuest> getActiveQuests() {
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
                        MiniMessage.miniMessage().deserialize("<gold>Quest Progress<gray>: " + Util.createProgressBar(activeQuest, 25, "<#f498f6>|", "<#F7FFC9>|") + " <gray>(" + String.format("%.1f",Util.fractionToDecimal(activeQuest.getProgress(), activeQuest.getAmount())) +"%)")
                );
            }

            if (activeQuest.getAmount() <= activeQuest.getProgress()) {
                activeQuests.remove(activeQuest);
                completedQuests.add(activeQuest.fullIdentifier());

                activeQuest.executeReward(getPlayer());
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have completed the quest: " + activeQuest.getName() + "&r!"));
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
            Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You must complete the quest: " + quest.getRequiredCompletedQuestObject().getName() + "&r!"));
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
        Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have started the quest: " + quest.getName() + "&r!"));
        return true;
    }

    public boolean dropQuest(Quest quest) {
        for (ActiveQuest activeQuest : activeQuests) {
            if (activeQuest.fullIdentifier().equals(quest.fullIdentifier())) {
                activeQuests.remove(activeQuest);
                Objects.requireNonNull(getPlayer()).sendMessage(Util.colorText(Util.getPrefix() + "You have dropped the quest: " + quest.getName() + "&r!"));
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
        //final String permStr = "simplequests.maxquests." + category + ".";
        final List<String> list = new ArrayList<>();

        for (String perm : getPlayer().getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).toList()) {
            if (perm.startsWith("simplequests.maxquests.")) {
                list.add(perm.replace("simplequests.maxquests.", ""));
            }
        }

        int finalAmount = -1;

        for (String reducedPerm : list) {
            final String[] split = reducedPerm.split("\\.");
            if (split.length == 2) {
                if (split[0].equals(category) || split[0].equals("all")) {
                    final int amount = Integer.parseInt(split[1]);
                    if (amount > finalAmount) finalAmount = amount;

                }
            }
        }

        return finalAmount == -1 ? SimpleQuests.getConfigFile().getInt("categories." + category + ".default-max-quests") : finalAmount;
        // return permissions.stream()
        //                .filter(permission -> permission.getPermission().startsWith(permStr))
        //                .map(permission -> Integer.parseInt(permission.getPermission().replace(permStr, "")))
        //                .max(Integer::compareTo)
        //                .orElse(SimpleQuests.getConfigFile().getInt("categories." + category + ".default-max-quests"));
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

    public boolean addAchievement(Achievement achievement) {
        if (achievementIds.contains(achievement.getId())) {
            getPlayer().sendMessage(Util.getPrefix() + "You have already unlocked this achievement!");
            return false;
        }

        switch (achievement.getAchievementType()) {
            case NUMERICAL_QUESTS_COMPLETED -> {
                if (completedQuests.size() < (int) achievement.getValue()) {
                    getPlayer().sendMessage(Util.getPrefix() + Util.colorText("You have not completed enough quests to unlock this achievement! &7(") + completedQuests.size() + "/" + achievement.getValue() + ")");
                    return false;
                }
            }
            case QUESTS_COMPLETED -> {
                final List<String> questsNeededToBeCompleted = (List<String>) achievement.getValue();
                for (String quest : questsNeededToBeCompleted) {
                    if (!completedQuests.contains(quest)) {
                        getPlayer().sendMessage(Util.getPrefix() + "You have not completed all the required quests to unlock this achievement!");
                        return false;
                    }
                }
            }
            case CATEGORY_COMPLETED -> {
                final String category = (String) achievement.getValue();
                if (completedQuests.stream().filter(quest -> quest.startsWith(category)).count() < Objects.requireNonNull(QuestManager.getQuests(category)).size()) {
                    getPlayer().sendMessage(Util.getPrefix() + "You have not completed all the quests in the required category to unlock this achievement!");
                    return false;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + achievement.getAchievementType());
        }

        getPlayer().sendMessage(Util.getPrefix() + "You have unlocked the achievement: &a" + achievement.getName() + "&r!");
        achievementIds.add(achievement.getId());
        achievement.executeReward(getPlayer());
        return true;
    }

    public boolean hasAchievement(String id) {
        return achievementIds.contains(id);
    }
    public boolean hasAchievement(Achievement achievement) {
        return achievementIds.contains(achievement.getId());
    }

    public String getCategoryCompletion(String category) {
        final List<Quest> quests = QuestManager.getQuests(category);
        if (quests == null) return "0%";
        final int totalQuests = quests.size();
        final int completedQuests = (int) this.completedQuests.stream().filter(quest -> quest.startsWith(category)).count();
        return String.format("%.1f", Util.fractionToDecimal(completedQuests, totalQuests)) + "%";
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
