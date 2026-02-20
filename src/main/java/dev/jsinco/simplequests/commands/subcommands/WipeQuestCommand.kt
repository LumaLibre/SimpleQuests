package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class WipeQuestCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${Util.prefix}Usage: /simplequests wipe <player> <category:questId>")
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(player.uniqueId)

        Bukkit.getAsyncScheduler().runNow(plugin) {
            if (questPlayer.completedQuests.remove(args[2])) {
                sender.sendMessage(Util.colorText("${Util.prefix}Wiped quest &a\"${args[2]}&a\"&r from ${player.name}'s completed record."))
            } else {
                sender.sendMessage("${Util.prefix}Player ${player.name} has not completed this quest.")
            }
        }
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        if (args.size == 2) {
            return null
        } else if (args.size == 3) {
            val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(Bukkit.getOfflinePlayer(args[1]).uniqueId)
            return questPlayer.completedQuests
        }
        return null
    }

    override fun permission(): String? {
        return "simplequests.command.wipe"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}