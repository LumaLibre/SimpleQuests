package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.enums.AchievementType;
import dev.jsinco.simplequests.enums.RewardType;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Achievement extends AbstractRewardable {

    private final String id;
    private final String name;
    private final List<String> description;
    private final AchievementType achievementType;
    private final Object value;
    @Nullable private final Material menuItem;

    public Achievement(String id, String name, List<String> description, AchievementType achievementType, Object value, RewardType rewardType, Object rewardValue, @Nullable Material menuItem) {
        super(rewardType, rewardValue);
        this.id = id;
        this.name = name;
        this.description = description;
        this.achievementType = achievementType;
        this.value = value;
        this.menuItem = menuItem;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public AchievementType getAchievementType() {
        return achievementType;
    }

    public Object getValue() {
        return value;
    }

    @Nullable
    public Material getMenuItem() {
        return menuItem;
    }


}
