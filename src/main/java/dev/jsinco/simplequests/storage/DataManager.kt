package dev.jsinco.simplequests.storage

import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

interface DataManager {

    fun getCompletedQuestIds(uuid: UUID): List<String>

    fun setCompletedQuestIds(uuid: UUID, questIds: List<String>)

    fun getActiveQuests(uuid: UUID): ConcurrentLinkedQueue<ActiveQuest>

    fun setActiveQuests(uuid: UUID, activeQuests: ConcurrentLinkedQueue<ActiveQuest>)

    fun showActionBarProgress(uuid: UUID): Boolean

    fun setShowActionBarProgress(uuid: UUID, showActionBarProgress: Boolean)

    fun loadQuestPlayer(uuid: UUID): QuestPlayer

    fun saveQuestPlayer(questPlayer: QuestPlayer)

    fun getStorageMethod(): StorageMethod
}