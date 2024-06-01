package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.enums.RewardType;
import dev.jsinco.simplequests.managers.QuestManager;
import dev.jsinco.simplequests.managers.Util;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quest extends AbstractRewardable {

    private final String category;
    private final String id;
    private final String name;
    private final String type;
    private final QuestAction questAction;
    private final int amount;
    private final List<String> description;
    @Nullable private final Material menuItem;
    @Nullable private final String requiredCompletedQuest;



    public Quest(String category, String id, String name, String type, QuestAction questAction, int amount, @Nullable List<String> description, @Nullable RewardType rewardType, @Nullable Object rewardValue, @Nullable Material menuItem, @Nullable String requiredCompletedQuest) {
        super(rewardType, rewardValue);
        this.category = category;
        this.id = id;
        this.name = name;
        this.type = type;
        this.questAction = questAction;
        this.amount = amount;
        this.menuItem = menuItem;
        this.requiredCompletedQuest = requiredCompletedQuest;

        // Must be done last
        this.description = description != null ? Util.formatDescription(description, this) : Util.getDefaultQuestDescription(this);
    }

    public Quest(String category, String id, String name, String type, QuestAction questAction, int amount, @Nullable List<String> description, @Nullable String rewardTypeStr, @Nullable Object rewardValue, @Nullable String menuItemStr, @Nullable String requiredCompletedQuest) {
        super(rewardTypeStr != null ? RewardType.valueOf(rewardTypeStr.toUpperCase()) : null, rewardValue);
        this.category = category;
        this.id = id;
        this.name = name;
        this.type = type;
        this.questAction = questAction;
        this.amount = amount;
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


    public String fullIdentifier() {
        return category + ":" + id;
    }
}
