package dev.jsinco.simplequests.hooks.papi

import dev.jsinco.simplequests.SimpleQuests
import org.bukkit.OfflinePlayer

interface Placeholder {
    fun onReceivedRequest(plugin: SimpleQuests, player: OfflinePlayer?, args: List<String>): String?
}