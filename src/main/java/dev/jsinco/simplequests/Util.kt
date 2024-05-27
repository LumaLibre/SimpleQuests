package dev.jsinco.simplequests

import org.bukkit.Bukkit
import org.bukkit.ChatColor

object Util {

    private val plugin: SimpleQuests = SimpleQuests.getInstance()
    @JvmStatic var prefix: String = colorText(SimpleQuests.getConfigFile().getString("prefix") ?: "&8[&6SimpleQuests&8]")
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"

    @JvmStatic
    fun debugLog(msg: String) {
        Bukkit.getConsoleSender().sendMessage(colorText("[SimpleQuests] &6DEBUG: $msg"))
    }

    @JvmStatic
    fun colorText(text: String): String {
        val texts = text.split(String.format(WITH_DELIMITER, "&").toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if (texts[i].equals("&", ignoreCase = true)) {
                i++
                if (texts[i][0] == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)).toString() + texts[i].substring(7))
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                }
            } else {
                finalText.append(texts[i])
            }
            i++
        }
        return finalText.toString()
    }
}