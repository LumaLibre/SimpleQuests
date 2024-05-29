package dev.jsinco.simplequests.commands.subcommands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.commands.SubCommand
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand {
    override fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>) {
        sender.sendMessage("${Util.prefix}Reloading...")
        val start = System.currentTimeMillis()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            for (questPlayer: QuestPlayer in QuestManager.getQuestPlayers()) {
                SimpleQuests.getDataManager().saveQuestPlayer(questPlayer)
            }
            SimpleQuests.loadData()
            QuestManager.loadQuests()
            Util.prefix = Util.colorText(SimpleQuests.getConfigFile().getString("prefix") ?: "&8[&6SimpleQuests&8]&r ")
            sender.sendMessage(Util.colorText("${Util.prefix}Finished reloading in &6${System.currentTimeMillis() - start}ms"))
        })
    }

    override fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>? {
        return null
    }

    override fun permission(): String? {
        return "simplequests.command.reload"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}