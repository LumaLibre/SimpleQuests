package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import org.bukkit.command.CommandSender

class WipePlayerCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("Usage: /simplequests wipeplayer <username>")
            return
        }

        if (!args.contains("confirm")) {
            sender.sendMessage("Are you sure you want to wipe player ${args[1]}? This action cannot be undone.")
            sender.sendMessage("To confirm, type /simplequests wipeplayer ${args[1]} confirm")
            return
        }

        val player = plugin.server.getOfflinePlayer(args[1])
        val questPlayer = QuestManager.getQuestPlayer(player.uniqueId)
        questPlayer.activeQuestsQueue.clear()
        questPlayer.completedQuests.clear()
        questPlayer.isShowActionBarProgress = false

        sender.sendMessage("Player ${player.name} has been wiped.")
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String? {
        return "simplequests.command.wipeplayer"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}