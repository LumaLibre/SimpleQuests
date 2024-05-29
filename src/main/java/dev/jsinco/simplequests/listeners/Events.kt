package dev.jsinco.simplequests.listeners

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.enums.QuestAction
import dev.jsinco.simplequests.guis.tools.AbstractGui
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class Events : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder: AbstractGui = event.inventory.getHolder(false) as? AbstractGui ?: return
        holder.onInventoryClick(event)
    }
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder: AbstractGui = event.inventory.getHolder(false) as? AbstractGui ?: return
        holder.onInventoryClose(event)
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val questPlayer = QuestManager.getQuestPlayer(event.player.uniqueId) ?: return
        questPlayer.updateQuests(event.block.type.name, QuestAction.BREAK, 1)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val questPlayer = QuestManager.getQuestPlayer(event.player.uniqueId) ?: return
        questPlayer.updateQuests(event.block.type.name, QuestAction.PLACE, 1)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onCraftItem(event: CraftItemEvent) {
        val questPlayer = QuestManager.getQuestPlayer(event.whoClicked.uniqueId) ?: return
        questPlayer.updateQuests(event.recipe.result.type.name, QuestAction.CRAFT, 1)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDeath(event: EntityDeathEvent) {
        val questPlayer = QuestManager.getQuestPlayer(event.entity.killer?.uniqueId ?: return) ?: return
        questPlayer.updateQuests(event.entity.type.name, QuestAction.KILL, 1)
    }

}