package dev.jsinco.simplequests.objects;

import java.util.List;

public record StorableQuest(String category, String id, int progression) {

    public static List<StorableQuest> serializeToStorableQuests(List<ActiveQuest> activeQuests) {
        return activeQuests.stream().map(activeQuest -> new StorableQuest(activeQuest.getCategory(), activeQuest.getId(), activeQuest.getProgress())).toList();
    }
}
