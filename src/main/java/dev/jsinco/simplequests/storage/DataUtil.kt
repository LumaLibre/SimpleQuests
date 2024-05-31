package dev.jsinco.simplequests.storage

import com.google.gson.internal.LinkedTreeMap
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.StorableQuest
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Level

object DataUtil {

    fun getActiveQuestsList(list: List<*>, uuid: UUID): ConcurrentLinkedQueue<ActiveQuest> {
        val activeQuests: ConcurrentLinkedQueue<ActiveQuest> = ConcurrentLinkedQueue()
        for (obj: Any? in list) {
            when (obj) {
                is StorableQuest -> activeQuests.add(ActiveQuest(obj))
                is LinkedTreeMap<*, *> -> activeQuests.add(ActiveQuest(obj["category"] as String, obj["id"] as String, (obj["progress"] as Double).toInt()))
                else -> SimpleQuests.getInstance().logger.log(Level.SEVERE, "Unknown object in activeQuests list for uuid: $uuid value was: $obj")
            }
        }
        return activeQuests
    }

}