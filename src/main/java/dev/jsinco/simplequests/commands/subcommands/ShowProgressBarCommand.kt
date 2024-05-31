package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.commands.SubCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ShowProgressBarCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        sender as Player
        val questPlayer = QuestManager.getQuestPlayer(sender.uniqueId)
        questPlayer.isShowActionBarProgress = !questPlayer.isShowActionBarProgress
        sender.sendMessage("${Util.prefix}Show progress bar set to ${questPlayer.isShowActionBarProgress}")
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String {
       return "simplequests.command.showprogressbar"
    }

    override fun playerOnly(): Boolean {
        return true
    }
}