package dev.jsinco.simplequests.gui

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.gui.tools.AbstractGui
import dev.jsinco.simplequests.gui.tools.PaginatedGui
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Archived(val questPlayer: QuestPlayer) : AbstractGui {
    // TODO: Make this more efficient by having each page be loaded when a player wants to actually view it rather than pre-loading all pages
    companion object {
        /*val inventoryMap: List<Int> = listOf(
            0,9,18,27,36,45,
            1,10,19,28,37,46,
            2,11,20,29,38,47,
            3,12,21,30,39,48,
            4,13,22,31,40,49,
            5,14,23,32,41,50,
            6,15,24,33,42,51,
            7,16,25,34,43,52,
            8,17,26,35,44,53
        )*/

        val inventoryMap: List<Int> = listOf(
            0,9,18,27,36,
            1,10,19,28,37,
            2,11,20,29,38,
            3,12,21,30,39,
            4,13,22,31,40,
            5,14,23,32,41,
            6,15,24,33,42,
            7,16,25,34,43,
            8,17,26,35,44
        )
    }

    val paginatedGui: PaginatedGui

    init {
        val start = System.currentTimeMillis()
        val items: MutableList<ItemStack> = mutableListOf()
        for (quest in QuestManager.getQuests()) {
            val item = ItemStack(quest.menuItem ?: Material.WHITE_STAINED_GLASS)
            if (questPlayer.hasCompletedQuest(quest)) {
                item.addUnsafeEnchantment(Enchantment.LUCK, 1)
            }
            items.add(item)
        }

        val initItems = mapOf(
            Util.basicItem(Material.GREEN_STAINED_GLASS_PANE) to listOf(0, 8, 36, 44),
            Util.basicItem(Material.SHORT_GRASS) to listOf(1, 7, 37, 43),
            Util.basicItem(Material.FERN) to listOf(2, 6, 38, 42),
            Util.basicItem(Material.PINK_TULIP) to listOf(3, 5, 39, 41),
            Util.basicItem(Material.LILY_PAD) to listOf(4, 40)
        )
        val inv = Bukkit.createInventory(this, 45, "Q")

        for (item in initItems) {
            for (slot in item.value) {
                inv.setItem(slot, item.key)
            }
        }
        /*listOf(0, 8, 36, 44,1, 7, 37, 43,2, 6, 38, 42,3, 5,4, 40, 39, 41)*/
        paginatedGui = PaginatedGui(Util.colorText("&2Quests"), inv, items, Pair(0, 45), listOf(), inventoryMap)

        println("Time to load quests gui: ${System.currentTimeMillis() - start}ms")
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
    }

    override fun onInventoryClose(event: InventoryCloseEvent) {
    }

    override fun getInventory(): Inventory {
        return paginatedGui.pages.random()
    }
}