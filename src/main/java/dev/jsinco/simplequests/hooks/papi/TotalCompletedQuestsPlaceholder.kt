package dev.jsinco.simplequests.hooks.papi

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.OfflinePlayer

class TotalCompletedQuestsPlaceholder : Placeholder {
    override fun onReceivedRequest(plugin: SimpleQuests, player: OfflinePlayer?, args: List<String>): String? {
        val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(player?.uniqueId ?: return null)
        return questPlayer.completedQuests.size.toString()
    }
}