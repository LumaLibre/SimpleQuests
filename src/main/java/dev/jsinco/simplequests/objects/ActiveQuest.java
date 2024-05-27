package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.enums.QuestAction;
import dev.jsinco.simplequests.enums.RewardType;
import org.jetbrains.annotations.Nullable;

public class ActiveQuest extends Quest {

    private int progress;

    public ActiveQuest(int progress, String category, String id, String name, String type, QuestAction questAction, int amount, @Nullable RewardType rewardType, @Nullable Object rewardValue) {
        super(category, id, name, type, questAction, amount, rewardType, rewardValue);
        this.progress = progress;
    }

    public ActiveQuest(Quest quest) {
        super(quest.getCategory(), quest.getId(), quest.getName(), quest.getType(), quest.getQuestAction(), quest.getAmount(), quest.getRewardType(), quest.getRewardValue());
        this.progress = 0;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
