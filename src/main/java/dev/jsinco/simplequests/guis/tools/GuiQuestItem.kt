package dev.jsinco.simplequests.guis.tools

import dev.jsinco.simplequests.objects.Quest
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GuiQuestItem(val quest: Quest) {

    val itemStack: ItemStack

    init {
        itemStack = ItemStack(quest.menuItem ?: Material.WHITE_STAINED_GLASS)
        val meta = itemStack.itemMeta
    }
}