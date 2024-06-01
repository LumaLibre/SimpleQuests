package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.guis.AchievementsGui
import dev.jsinco.simplequests.managers.QuestManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Debug : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        sender as Player
        val questPlayer = QuestManager.getQuestPlayer(sender.uniqueId)
        sender.openInventory(AchievementsGui(questPlayer).inventory)
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String? {
        return "simplequests.debug"
    }

    override fun playerOnly(): Boolean {
        return true
    }

}