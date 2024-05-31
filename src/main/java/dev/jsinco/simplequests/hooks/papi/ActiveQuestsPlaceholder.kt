package dev.jsinco.simplequests.hooks.papi

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import org.bukkit.OfflinePlayer

class ActiveQuestsPlaceholder : Placeholder {
    override fun onReceivedRequest(plugin: SimpleQuests, player: OfflinePlayer?, args: List<String>): String? {
        val questPlayer = QuestManager.getQuestPlayer(player?.uniqueId ?: return null)
        return questPlayer.activeQuests.joinToString(", ") { it.name }
    }
}