package dev.jsinco.simplequests.commands

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.commands.subcommands.ClearQuestCommand
import dev.jsinco.simplequests.commands.subcommands.DropQuestCommand
import dev.jsinco.simplequests.commands.subcommands.PluginInfoCommand
import dev.jsinco.simplequests.commands.subcommands.QuestProgressionCommand
import dev.jsinco.simplequests.commands.subcommands.ReloadCommand
import dev.jsinco.simplequests.commands.subcommands.SaveCommand
import dev.jsinco.simplequests.commands.subcommands.ShowProgressBarCommand
import dev.jsinco.simplequests.commands.subcommands.StartQuestCommand
import dev.jsinco.simplequests.commands.subcommands.WipePlayerCommand
import dev.jsinco.simplequests.commands.subcommands.WipeQuestCommand
import dev.jsinco.simplequests.guis.CategoriesGui
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CommandManager(private val plugin: SimpleQuests) : TabExecutor {

    private val commands: MutableMap<String, SubCommand> = mutableMapOf()

    init {
        commands["start"] = StartQuestCommand()
        commands["save"] = SaveCommand()
        commands["progression"] = QuestProgressionCommand()
        commands["reload"] = ReloadCommand()
        commands["clear"] = ClearQuestCommand()
        commands["drop"] = DropQuestCommand()
        commands["plugininfo"] = PluginInfoCommand()
        commands["wipe"] = WipeQuestCommand()
        commands["showprogressbar"] = ShowProgressBarCommand()
        commands["wipeplayer"] = WipePlayerCommand()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender is Player) {
                sender.openInventory(CategoriesGui(QuestManager.getQuestPlayer(sender.uniqueId)).inventory)
            }
            return true
        }

        val subCommand = commands[args[0]] ?: return false

        if (subCommand.playerOnly() && sender !is Player) return false
        else if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission()!!)) return false

        subCommand.execute(plugin, sender, args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        if (args.size == 1) return commands.keys.toList()

        val subCommand = commands[args[0]] ?: return null

        if (subCommand.playerOnly() && sender !is Player) return null
        else if (subCommand.permission() != null && !sender.hasPermission(subCommand.permission()!!)) return null

        return subCommand.tabComplete(plugin, sender, args)
    }
}