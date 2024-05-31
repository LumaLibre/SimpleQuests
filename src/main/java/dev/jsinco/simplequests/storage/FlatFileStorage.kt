package dev.jsinco.simplequests.storage

import dev.jsinco.abstractjavafilelib.schemas.JsonSavingSchema
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import dev.jsinco.simplequests.objects.StorableQuest
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

class FlatFileStorage : DataManager {

    private val savesFile = JsonSavingSchema("saves.json")

    override fun getCompletedQuestIds(uuid: UUID): List<String> {
        return savesFile.get("$uuid.completedQuests") as? List<String> ?: emptyList()
    }

    override fun setCompletedQuestIds(uuid: UUID, questIds: List<String>) {
        savesFile.set("$uuid.completedQuests", questIds)
        savesFile.save()
    }

    override fun getActiveQuests(uuid: UUID): ConcurrentLinkedQueue<ActiveQuest> {
        val activeQuestList: ConcurrentLinkedQueue<ActiveQuest> = ConcurrentLinkedQueue()

        val list: Any? = savesFile.get("$uuid.activeQuests")

        if (list !is List<*>) {
            return activeQuestList
        }

        return DataUtil.getActiveQuestsList(list, uuid)
    }

    override fun setActiveQuests(uuid: UUID, activeQuests: ConcurrentLinkedQueue<ActiveQuest>) {
        savesFile.set("$uuid.activeQuests", StorableQuest.serializeToStorableQuests(activeQuests))
        savesFile.save()
    }

    override fun showActionBarProgress(uuid: UUID): Boolean {
        return savesFile.getBoolean("$uuid.showActionBarProgress")
    }

    override fun setShowActionBarProgress(uuid: UUID, showActionBarProgress: Boolean) {
        savesFile.set("$uuid.showActionBarProgress", showActionBarProgress)
        savesFile.save()
    }

    override fun loadQuestPlayer(uuid: UUID): QuestPlayer {
        return QuestPlayer(uuid, getCompletedQuestIds(uuid), getActiveQuests(uuid), savesFile.getBoolean("$uuid.showActionBarProgress"))
    }

    override fun saveQuestPlayer(questPlayer: QuestPlayer) {
        if (questPlayer.activeQuestsQueue.isEmpty() && questPlayer.completedQuests.isEmpty() && savesFile.contains(questPlayer.uuid.toString())) {
            savesFile.remove(questPlayer.uuid.toString())
            savesFile.save()
            Util.debugLog("Removed QuestPlayer: ${questPlayer.uuid}")
            return
        }

        savesFile.set("${questPlayer.uuid}.completedQuests", questPlayer.completedQuests)
        savesFile.set("${questPlayer.uuid}.activeQuests", StorableQuest.serializeToStorableQuests(questPlayer.activeQuestsQueue))
        savesFile.set("${questPlayer.uuid}.showActionBarProgress", questPlayer.isShowActionBarProgress)
        savesFile.save()
        Util.debugLog("Saved QuestPlayer: ${questPlayer.uuid}")
    }


    override fun getStorageMethod(): StorageMethod {
        return StorageMethod.FLATFILE
    }

    fun getFile(): JsonSavingSchema {
        return savesFile
    }
}