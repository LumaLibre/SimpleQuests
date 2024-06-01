package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.Quest
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class StartQuestCommand : SubCommand {

    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${Util.prefix}Usage: /simplequests start <player> <category:questId>")
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(player.uniqueId)

        val questString = args[2].split(":")
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val quest: Quest = QuestManager.getQuest(questString[0], questString[1]) ?: run {
                sender.sendMessage("${Util.prefix}Quest not found.")
                return@Runnable
            }

            if (questPlayer.startQuest(quest)) {
                sender.sendMessage(Util.colorText("${Util.prefix}Started quest &a\"${quest.name}\"&r for ${player.name}."))
            } else {
                sender.sendMessage("${Util.prefix}Player ${player.name} has either already completed or started this quest.")
            }
        })
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        if (args.size == 2) return null
        else if (args.size == 3)  return QuestManager.getQuests().map { "${it.category}:${it.id}" }
        return null
    }

    override fun permission(): String {
        return "simplequests.command.start"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}