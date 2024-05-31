package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.objects.ActiveQuest
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class QuestProgressionCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("Usage: /simplequests progression <player>")
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val questPlayer = QuestManager.getQuestPlayer(player.uniqueId)

        val strBuilder = StringBuilder()
        for (activeQuest in questPlayer.activeQuests) {
            strBuilder.append(Util.colorText("&6${activeQuest.id}: ${createProgressBar(activeQuest)}\n"))
        }
        sender.sendMessage(Util.colorText("Completed Quests:\n ${questPlayer.completedQuests.joinToString("&f, ") { "&a$it" }}"))
        sender.sendMessage(Util.colorText("&rActive Quests:\n $strBuilder"))
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String {
        return "simplequests.command.progression"
    }

    override fun playerOnly(): Boolean {
        return false
    }

    fun createProgressBar(quest: ActiveQuest, totalBars: Int = 25): String {
        val completedBars = (quest.progress.toDouble() / quest.amount * totalBars).toInt()
        val completed = "&a|".repeat(completedBars)
        val remaining = "&7|".repeat(totalBars - completedBars)
        return completed + remaining
    }
}