package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class QuestProgressionCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${Util.prefix}Usage: /simplequests progression <player>")
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val questPlayer = QuestManager.getQuestPlayer(player.uniqueId)

        val strBuilder = StringBuilder()
        for (activeQuest in questPlayer.activeQuests) {
            strBuilder.append(Util.colorText("&6${activeQuest.id}: ${Util.createProgressBar(activeQuest)}\n"))
        }
        sender.sendMessage(Util.colorText("Completed Quests:\n ${questPlayer.completedQuests.joinToString("&f, ") { "&a$it" }}"))
        sender.sendMessage(Util.colorText("&rAchievements:\n ${questPlayer.achievementIds.joinToString("&f, ") { "&a$it" }}"))
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

}