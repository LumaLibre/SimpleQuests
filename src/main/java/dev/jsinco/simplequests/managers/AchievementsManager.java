package dev.jsinco.simplequests.managers;

import dev.jsinco.abstractjavafilelib.ConfigurationSection;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.simplequests.enums.AchievementType;
import dev.jsinco.simplequests.enums.RewardType;
import dev.jsinco.simplequests.objects.Achievement;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class AchievementsManager {

    private static final List<Achievement> achievements = new ArrayList<>();


    public static void loadAchievements() { // do async
        achievements.clear();
        final SnakeYamlConfig file = new SnakeYamlConfig("achievements.yml");

        for (final String key : file.getKeys()) {
            final ConfigurationSection section = file.getConfigurationSection(key);

            achievements.add(new Achievement(
                    key,
                    section.getString("name"),
                    section.getStringList("description"),
                    AchievementType.valueOf(section.getString("type").toUpperCase()),
                    section.get("value"),
                    RewardType.valueOf(section.getString("reward.type").toUpperCase()),
                    section.get("reward.value"),
                    section.getString("menu-item") != null ? Material.getMaterial(section.getString("menu-item").toUpperCase()) : null
            ));
        }
        Util.debugLog("Loaded " + achievements.size() + " achievements.");
    }

    public static List<Achievement> getAchievements() {
        return achievements;
    }

    @Nullable
    public static Achievement getAchievementById(String id) {
        id = id.toLowerCase();
        for (Achievement achievement : achievements) {
            if (achievement.getId().toLowerCase().equals(id)) {
                return achievement;
            }
        }
        return null;
    }
}
