package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.enums.RewardType;
import dev.jsinco.simplequests.managers.QuestManager;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ActiveQuest extends Quest {

    private int progress;

    public ActiveQuest(int progress, String category, String id, String name, List<String> type, QuestAction questAction, int amount, @Nullable List<String> description, @Nullable RewardType rewardType, @Nullable Object rewardValue, @Nullable Material menuItem, @Nullable String requiredCompletedQuest) {
        super(category, id, name, type, questAction, amount,  description, rewardType, rewardValue, menuItem, requiredCompletedQuest);
        this.progress = progress;
    }

    public ActiveQuest(Quest quest) {
        super(quest.getCategory(), quest.getId(), quest.getName(), quest.getType(), quest.getQuestAction(), quest.getAmount(), quest.getDescription(), quest.getRewardType(), quest.getRewardValue(), quest.getMenuItem(), quest.getRequiredCompletedQuest());
        this.progress = 0;
    }

    public ActiveQuest(Quest quest, int progress) {
        super(quest.getCategory(), quest.getId(), quest.getName(), quest.getType(), quest.getQuestAction(), quest.getAmount(), quest.getDescription(), quest.getRewardType(), quest.getRewardValue(), quest.getMenuItem(), quest.getRequiredCompletedQuest());
        this.progress = progress;
    }

    public ActiveQuest(StorableQuest storableQuest) {
        this(Objects.requireNonNull(QuestManager.getQuest(storableQuest.category(), storableQuest.id())), storableQuest.progress());
    }

    public ActiveQuest(String category, String id, int progress) {
        this(Objects.requireNonNull(QuestManager.getQuest(category, id)), progress);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
