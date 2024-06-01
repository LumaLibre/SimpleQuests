package dev.jsinco.simplequests.storage

import dev.jsinco.abstractjavafilelib.schemas.JsonSavingSchema
import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import dev.jsinco.simplequests.objects.StorableQuest
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

class FlatFileStorage : DataManager {

    private val savesFile = JsonSavingSchema("saves.json")

    override fun getCompletedQuests(uuid: UUID): List<String> {
        return savesFile.get("$uuid.completedQuests") as? List<String> ?: emptyList()
    }

    override fun setCompletedQuests(uuid: UUID, questIds: List<String>) {
        savesFile.set("$uuid.completedQuests", questIds)
        savesFile.save()
    }

    override fun getAchievementIds(uuid: UUID): List<String> {
        return savesFile.get("$uuid.achievementIds") as? List<String> ?: emptyList()
    }

    override fun setAchievementIds(uuid: UUID, achievementIds: List<String>) {
        savesFile.set("$uuid.achievementIds", achievementIds)
        savesFile.save()
    }

    override fun getActiveQuests(uuid: UUID): ConcurrentLinkedQueue<ActiveQuest> {
        val activeQuestList: ConcurrentLinkedQueue<ActiveQuest> = ConcurrentLinkedQueue()

        val list: Any? = savesFile.get("$uuid.activeQuests")

        if (list !is List<*>) {
            return activeQuestList
        }

        return Util.getActiveQuestsList(list, uuid)
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
        return QuestPlayer(uuid, getCompletedQuests(uuid), getAchievementIds(uuid), getActiveQuests(uuid), savesFile.getBoolean("$uuid.showActionBarProgress"))
    }

    override fun saveQuestPlayer(questPlayer: QuestPlayer) {
        //         if (questPlayer.activeQuests.isEmpty() && questPlayer.completedQuests.isEmpty() && savesFile.contains(questPlayer.uuid.toString())) {
        //            savesFile.remove(questPlayer.uuid.toString())
        //            savesFile.save()
        //            Util.debugLog("Removed QuestPlayer: ${questPlayer.uuid}")
        //            return
        //        }

        savesFile.set("${questPlayer.uuid}.completedQuests", questPlayer.completedQuests)
        savesFile.set("${questPlayer.uuid}.achievementIds", questPlayer.achievementIds)
        savesFile.set("${questPlayer.uuid}.activeQuests", StorableQuest.serializeToStorableQuests(questPlayer.activeQuests))
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