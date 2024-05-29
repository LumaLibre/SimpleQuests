package dev.jsinco.simplequests.hooks.papi

import dev.jsinco.simplequests.SimpleQuests
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PapiManager(val plugin: SimpleQuests) : PlaceholderExpansion() {

    companion object {
        private val placeHolders: MutableMap<String, Placeholder> = mutableMapOf()
    }

    init {
        placeHolders["totalcompletedquests"] = TotalCompletedQuestsPlaceholder()
        placeHolders["activequests"] = ActiveQuestsPlaceholder()
    }

    override fun getIdentifier(): String {
        return "simplequests"
    }

    override fun getAuthor(): String {
        return "Jsinco"
    }

    override fun getVersion(): String {
        return "1.0-SNAPSHOT"
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val args: List<String> = params.split("_")

        return placeHolders[args[0]]?.onReceivedRequest(plugin, player, args)
    }
}