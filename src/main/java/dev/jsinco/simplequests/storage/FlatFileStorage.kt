package dev.jsinco.simplequests.storage

import com.google.gson.internal.LinkedTreeMap
import dev.jsinco.abstractjavafilelib.schemas.JsonSavingSchema
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import dev.jsinco.simplequests.objects.StorableQuest
import java.util.UUID

class FlatFileStorage : DataManager {

    private val savesFile = JsonSavingSchema("saves.json")

    override fun getCompletedQuestIds(uuid: UUID): List<String> {
        return savesFile.get("$uuid.completedQuests") as? List<String> ?: emptyList()
    }

    override fun setCompletedQuestIds(uuid: UUID, questIds: List<String>) {
        savesFile.set("$uuid.completedQuests", questIds)
        savesFile.save()
    }

    @Suppress("UNCHECKED_CAST", "DuplicatedCode")
    override fun getActiveQuests(uuid: UUID): List<ActiveQuest> {
        val activeQuestList: MutableList<ActiveQuest> = mutableListOf()

        val list: List<LinkedTreeMap<*, *>> = savesFile.get("$uuid.activeQuests") as? List<LinkedTreeMap<*, *>> ?: emptyList()

        for (linkedTreeMap in list) {
            activeQuestList.add(ActiveQuest(linkedTreeMap["category"] as String, linkedTreeMap["id"] as String, (linkedTreeMap["progression"] as Double).toInt()))
        }

        return activeQuestList
    }

    override fun setActiveQuests(uuid: UUID, activeQuests: List<ActiveQuest>) {
        savesFile.set("$uuid.activeQuests", StorableQuest.serializeToStorableQuests(activeQuests))
        savesFile.save()
    }

    override fun loadQuestPlayer(uuid: UUID): QuestPlayer {
        return QuestPlayer(uuid, getCompletedQuestIds(uuid), getActiveQuests(uuid))
    }

    override fun saveQuestPlayer(questPlayer: QuestPlayer) {
        savesFile.set("${questPlayer.uuid}.completedQuests", questPlayer.completedQuests)
        savesFile.set("${questPlayer.uuid}.activeQuests", StorableQuest.serializeToStorableQuests(questPlayer.activeQuests))
        savesFile.save()
        Util.debugLog("Saved QuestPlayer: ${questPlayer.uuid}")
    }

    override fun getStorageMethod(): StorageMethod {
        return StorageMethod.FLATFILE
    }
}