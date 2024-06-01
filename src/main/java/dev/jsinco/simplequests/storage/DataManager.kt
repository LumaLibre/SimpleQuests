package dev.jsinco.simplequests.storage

import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

interface DataManager {

    // Completed quests
    fun getCompletedQuests(uuid: UUID): List<String>
    fun setCompletedQuests(uuid: UUID, questIds: List<String>)

    // Achievements
    fun getAchievementIds(uuid: UUID): List<String>
    fun setAchievementIds(uuid: UUID, achievementIds: List<String>)

    // Active quests
    fun getActiveQuests(uuid: UUID): ConcurrentLinkedQueue<ActiveQuest>
    fun setActiveQuests(uuid: UUID, activeQuests: ConcurrentLinkedQueue<ActiveQuest>)

    // Progress nar
    fun showActionBarProgress(uuid: UUID): Boolean
    fun setShowActionBarProgress(uuid: UUID, showActionBarProgress: Boolean)

    // Load & save QuestPlayer as efficient as possible
    fun loadQuestPlayer(uuid: UUID): QuestPlayer
    fun saveQuestPlayer(questPlayer: QuestPlayer)

    // Active storage method
    fun getStorageMethod(): StorageMethod
}