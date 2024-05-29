package dev.jsinco.simplequests.gui

import dev.jsinco.abstractjavafilelib.ConfigurationSection
import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.gui.tools.AbstractGui
import dev.jsinco.simplequests.gui.tools.PaginatedGui
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CategoriesGui : AbstractGui {

    companion object {
        val initItems = mapOf(
            Util.basicItem(Material.GREEN_STAINED_GLASS_PANE) to listOf(0, 8, 45, 53),
            Util.basicItem(Material.SHORT_GRASS) to listOf(1, 7, 46, 52),
            Util.basicItem(Material.FERN) to listOf(2, 6, 47, 51),
            Util.basicItem(Material.PINK_TULIP) to listOf(3, 5),
            Util.basicItem(Material.LILY_PAD) to listOf(4, 49),
            Util.basicItem(Material.PAPER, 10000).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "previous") } to listOf(48),
            Util.basicItem(Material.PAPER, 10001).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "next") } to listOf(50)
        )
    }


    val paginatedGui: PaginatedGui

    init {
        val inv = Bukkit.createInventory(this, 54, "SimpleQuests Categories")
        for (item in initItems) {
            for (slot in item.value) {
                inv.setItem(slot, item.key)
            }
        }

        val categoryItems: MutableList<ItemStack> = mutableListOf()
        val configSection: ConfigurationSection = SimpleQuests.getConfigFile().getConfigurationSection("category-icons")

        for (cat in SimpleQuests.getQuestsFile().keys) {
            val itemStack = ItemStack(Material.getMaterial(configSection.getString(cat) ?: "WHITE_STAINED_GLASS_PANE") ?: Material.WHITE_STAINED_GLASS_PANE)
            val meta = itemStack.itemMeta
            meta.setDisplayName(Util.format(cat))
            itemStack.itemMeta = meta

            Util.setGuiItemData(itemStack, GuiItemType.CATEGORY, cat)
            categoryItems.add(itemStack)
        }

        paginatedGui = PaginatedGui(Util.colorText("&#f498f6&lQuest Categories"), inv, categoryItems, Pair(19, 35), listOf(26, 27), null)
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val clickedItem = event.currentItem ?: return
        val meta = clickedItem.itemMeta ?: return
        val player = event.whoClicked as Player

        val itemData = Util.getGuiItemData(meta) ?: return

        when (itemData.first) {
            GuiItemType.CATEGORY -> {
                val questsGui = QuestsGui(QuestManager.getQuestPlayer(player.uniqueId), itemData.second)
                questsGui.generatePage()
                player.openInventory(questsGui.inventory)
            }
            GuiItemType.PAGE_SWITCHER -> {
                val inv = event.inventory
                when (itemData.second) {
                    "previous" -> {
                        if (paginatedGui.indexOf(inv) == 0) return
                        player.openInventory(paginatedGui.getPage(paginatedGui.indexOf(inv) - 1))
                    }
                    "next" -> {
                        if (paginatedGui.indexOf(inv) == paginatedGui.pages.size - 1) return
                        player.openInventory(paginatedGui.getPage(paginatedGui.indexOf(inv) + 1))
                    }
                }
            }

            else -> {}
        }
    }

    override fun onInventoryClose(event: InventoryCloseEvent) {
    }

    override fun getInventory(): Inventory {
        return paginatedGui.getPage(0)
    }
}