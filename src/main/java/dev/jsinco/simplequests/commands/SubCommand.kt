package dev.jsinco.simplequests.commands

import dev.jsinco.simplequests.SimpleQuests
import org.bukkit.command.CommandSender

interface SubCommand {

    fun execute(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>)

    fun tabComplete(plugin: SimpleQuests, sender: CommandSender, args: Array<out String>): List<String>?

    fun permission(): String?

    fun playerOnly(): Boolean
}