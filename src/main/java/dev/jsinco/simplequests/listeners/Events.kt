package dev.jsinco.simplequests.listeners

import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.enums.QuestAction
import dev.jsinco.simplequests.guis.tools.AbstractGui
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val questPlayer: QuestPlayer = QuestManager.getQuestPlayer(event.player.uniqueId) ?: return
        if (questPlayer.activeQuests.isNotEmpty()) {
            QuestManager.cacheQuestPlayer(questPlayer)
        }
    }

    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        val questPlayer: QuestPlayer = QuestManager.questPlayerFromCache(event.player.uniqueId) ?: return
        SimpleQuests.getDataManager().saveQuestPlayer(questPlayer)
        QuestManager.uncacheQuestPlayer(event.player.uniqueId)
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        QuestManager.questPlayerFromCache(event.player.uniqueId)?.updateQuests(event.block.type.name, QuestAction.BREAK, 1) ?: return
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        QuestManager.questPlayerFromCache(event.player.uniqueId)?.updateQuests(event.block.type.name, QuestAction.PLACE, 1) ?: return
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onCraftItem(event: CraftItemEvent) {
        // TODO: Won't count crafting if player shift clicks into inv while not holding proper material type/material type is at max
        val resultMaterial: Material = event.recipe.result.type
        val cursor: ItemStack = event.cursor
        if ((cursor.type == resultMaterial && cursor.amount < cursor.maxStackSize) || cursor.isEmpty) {
            QuestManager.questPlayerFromCache(event.whoClicked.uniqueId)?.updateQuests(resultMaterial.name, QuestAction.CRAFT, 1) ?: return
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDeath(event: EntityDeathEvent) {
        QuestManager.questPlayerFromCache(event.entity.killer?.uniqueId ?: return)?.updateQuests(event.entity.type.name, QuestAction.KILL, 1) ?: return
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerFish(event: PlayerFishEvent) {
        val caught: Item = event.caught as? Item ?: return
        QuestManager.questPlayerFromCache(event.player.uniqueId)?.updateQuests(caught.itemStack.type.name, QuestAction.FISH, 1) ?: return
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onFurnaceSmelt(event: FurnaceSmeltEvent) {
        val materialName = event.recipe?.input?.type?.name ?: return
        for (player in event.block.location.getNearbyPlayers(25.0)) {
            QuestManager.questPlayerFromCache(player.uniqueId)?.updateQuests(materialName, QuestAction.SMELT, 1) ?: continue
        }
    }

    /*@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onFurnaceInventoryClick(event: InventoryClickEvent) {
        if (event.inventory.type != InventoryType.FURNACE && event.inventory.type != InventoryType.BLAST_FURNACE && event.inventory.type != InventoryType.SMOKER) return
        QuestManager.questPlayerFromCache(event.whoClicked.uniqueId)?.updateQuests(event.currentItem?.type?.name, QuestAction.SMELT, 1)
    }*/
}