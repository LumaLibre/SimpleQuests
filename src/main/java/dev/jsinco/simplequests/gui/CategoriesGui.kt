package dev.jsinco.simplequests.gui

import dev.jsinco.abstractjavafilelib.ConfigurationSection
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.gui.tools.AbstractGui
import dev.jsinco.simplequests.gui.tools.PaginatedGui
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CategoriesGui : AbstractGui {

    val paginatedGui: PaginatedGui

    init {
        val categoryItems: MutableList<ItemStack> = mutableListOf()


        val configSection: ConfigurationSection = SimpleQuests.getConfigFile().getConfigurationSection("category-icons")
        for (cat in SimpleQuests.getQuestsFile().keys) {
            val itemStack = ItemStack(Material.getMaterial(configSection.getString(cat) ?: "WHITE_STAINED_GLASS") ?: Material.WHITE_STAINED_GLASS)
            val meta = itemStack.itemMeta
            meta.setDisplayName(Util.format(cat))
            itemStack.itemMeta = meta

            Util.setGuiItemData(itemStack, GuiItemType.CATEGORY, cat)
            categoryItems.add(itemStack)
        }

        val inv = Bukkit.createInventory(this, 54, "SimpleQuests Categories")
        paginatedGui = PaginatedGui(Util.colorText("&6SimpleQuests Categories"), inv, categoryItems, Pair(19, 35), listOf(26, 27), null)
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val meta = clickedItem.itemMeta ?: return

        val itemData = Util.getGuiItemData(meta) ?: return
        println("${itemData.first} ${itemData.second}")
    //when(itemData.first) {
        //            GuiItemType.PAGE_SWITCHER -> {
        //
        //            }
        //        }
    }

    override fun onInventoryClose(event: InventoryCloseEvent) {
    }

    override fun getInventory(): Inventory {
        return paginatedGui.getPage(0)
    }
}