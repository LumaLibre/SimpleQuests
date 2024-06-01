package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class SaveCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            for (questPlayer: QuestPlayer in QuestManager.getQuestPlayers()) {
                SimpleQuests.getDataManager().saveQuestPlayer(questPlayer)
            }
            sender.sendMessage("${Util.prefix}Saved all quest player data.")
        })
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String {
        return "simplequests.command.save"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}