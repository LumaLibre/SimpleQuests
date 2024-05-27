package dev.jsinco.simplequests.storage

import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import java.util.UUID

interface DataManager {

    fun getCompletedQuestIds(uuid: UUID): List<String>

    fun setCompletedQuestIds(uuid: UUID, questIds: List<String>)

    fun getActiveQuests(uuid: UUID): List<ActiveQuest>

    fun setActiveQuests(uuid: UUID, activeQuests: List<ActiveQuest>)

    fun loadQuestPlayer(uuid: UUID): QuestPlayer

    fun saveQuestPlayer(questPlayer: QuestPlayer)
}