package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.gui.QuestsGui
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        sender as Player
        val catGui = QuestsGui(QuestManager.getQuestPlayer(sender.uniqueId), "farming")
        catGui.generatePage()
        sender.openInventory(catGui.inventory)
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String? {
        return null
    }

    override fun playerOnly(): Boolean {
        return true
    }
}