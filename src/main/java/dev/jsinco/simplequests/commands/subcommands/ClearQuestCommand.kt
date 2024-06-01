package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class ClearQuestCommand : SubCommand {

    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${Util.prefix}Usage: /simplequests clear <player> <questId>")
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(player.uniqueId)

        var bool: Boolean = questPlayer.completedQuests.remove(args[2])
        if (!bool) bool = questPlayer.activeQuests.removeIf { it.id == args[2] }

        if (bool) {
            sender.sendMessage("${Util.prefix}Removed quest ${args[2]} from ${player.name}")
        } else {
            sender.sendMessage("${Util.prefix}Could not find quest ${args[2]} for ${player.name}")
        }
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        if (args.size == 2) {
            return null
        } else if (args.size == 3) {
            val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(Bukkit.getOfflinePlayer(args[1]).uniqueId)
            return questPlayer.completedQuests.also { it ->
                it.addAll(questPlayer.activeQuests.map { it.id })
            }
        }
        return null
    }

    override fun permission(): String {
        return "simplequests.command.clear"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}