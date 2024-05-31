package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.QuestManager;
import dev.jsinco.simplequests.Util;
import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.enums.RewardType;
import dev.jsinco.simplequests.hooks.PlayerPointsHook;
import dev.jsinco.simplequests.hooks.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quest {

    private final String category;
    private final String id;
    private final String name;
    private final String type;
    private final QuestAction questAction;
    private final int amount;
    private final List<String> description;
    @Nullable private final RewardType rewardType;
    @Nullable private final Object rewardValue;
    @Nullable private final Material menuItem;
    @Nullable private final String requiredCompletedQuest;



    public Quest(String category, String id, String name, String type, QuestAction questAction, int amount, @Nullable List<String> description, @Nullable RewardType rewardType, @Nullable Object rewardValue, @Nullable Material menuItem, @Nullable String requiredCompletedQuest) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.type = type;
        this.questAction = questAction;
        this.amount = amount;
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
        this.menuItem = menuItem;
        this.requiredCompletedQuest = requiredCompletedQuest;

        // Must be done last
        this.description = description != null ? Util.formatDescription(description, this) : Util.getDefaultQuestDescription(this);
    }

    public Quest(String category, String id, String name, String type, QuestAction questAction, int amount, @Nullable List<String> description, @Nullable String rewardTypeStr, @Nullable Object rewardValue, @Nullable String menuItemStr, @Nullable String requiredCompletedQuest) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.type = type;
        this.questAction = questAction;
        this.amount = amount;
        this.rewardValue = rewardValue;
        this.rewardType = rewardTypeStr != null ? RewardType.valueOf(rewardTypeStr.toUpperCase()) : null;
        this.menuItem = menuItemStr != null ? Material.getMaterial(menuItemStr.toUpperCase()) : null;
        this.requiredCompletedQuest = requiredCompletedQuest;

        // Must be done last
        this.description = description != null ? Util.formatDescription(description, this) : Util.getDefaultQuestDescription(this);
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public QuestAction getQuestAction() {
        return questAction;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getDescription() {
        return description;
    }

    @Nullable
    public RewardType getRewardType() {
        return rewardType;
    }

    @Nullable
    public Object getRewardValue() {
        return rewardValue;
    }

    @Nullable
    public Material getMenuItem() {
        return menuItem;
    }

    @Nullable
    public String getRequiredCompletedQuest() {
        return requiredCompletedQuest;
    }

    @Nullable public Quest getRequiredCompletedQuestObject() {
        if (requiredCompletedQuest == null) return null;
        final String[] split = requiredCompletedQuest.split(":");
        return QuestManager.getQuest(split[0], split[1]);
    }


    public void executeReward(OfflinePlayer player) {
        if (rewardType == null || rewardValue == null) return;
        final Player onlinePlayer = player.getPlayer();
        switch (rewardType) {
            case MONEY -> {
                final Economy econ = VaultHook.getEconomy();
                econ.depositPlayer(player, Double.parseDouble(rewardValue.toString()));

                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received &a$" + rewardValue + "&r!"));
                }
            }
            case COMMAND -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardValue.toString().replace("%player%", player.getName()));
                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received a reward for completing a quest!"));
                }
            }
            case POINTS -> {
                final PlayerPointsAPI playerPointsAPI = PlayerPointsHook.getApi();
                playerPointsAPI.give(player.getUniqueId(), Integer.parseInt(rewardValue.toString()));
                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received &a" + rewardValue + "&rLumins!"));
                }
            }
        }
    }

    public String fullIdentifier() {
        return category + ":" + id;
    }
}
