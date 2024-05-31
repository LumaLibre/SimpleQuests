package dev.jsinco.simplequests.objects;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public record StorableQuest(String category, String id, int progress) {

    public static List<StorableQuest> serializeToStorableQuests(ConcurrentLinkedQueue<ActiveQuest> activeQuests) {
        return activeQuests.stream().map(activeQuest -> new StorableQuest(activeQuest.getCategory(), activeQuest.getId(), activeQuest.getProgress())).toList();
    }
}
