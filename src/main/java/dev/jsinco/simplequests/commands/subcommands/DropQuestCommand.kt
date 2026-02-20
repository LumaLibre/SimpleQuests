package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.Quest
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class DropQuestCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
    if (args.size < 2) {
        sender.sendMessage("${Util.prefix}Usage: /simplequests drop <player> <category:questId>")
        return
    }

    val player = Bukkit.getOfflinePlayer(args[1])
    val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(player.uniqueId)

    val questString = args[2].split(":")
    Bukkit.getAsyncScheduler().runNow(plugin) {
        val quest: Quest = QuestManager.getQuest(questString[0], questString[1]) ?: run {
            sender.sendMessage("${Util.prefix}Quest not found.")
            return@runNow
        }

        if (questPlayer.dropQuest(quest)) {
            sender.sendMessage(Util.colorText("${Util.prefix}Dropped quest &a\"${quest.name}\"&r for ${player.name}."))
        } else {
            sender.sendMessage("${Util.prefix}Player ${player.name} has not started this quest.")
        }
    }
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        if (args.size == 2) {
            return null
        } else if (args.size == 3) {
            val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(Bukkit.getOfflinePlayer(args[1]).uniqueId)
            return questPlayer.activeQuests.map { "${it.category}:${it.id}" }
        }
        return null
    }

    override fun permission(): String {
        return "simplequests.command.drop"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}