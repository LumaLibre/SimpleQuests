package dev.jsinco.simplequests.guis.tools

import dev.jsinco.simplequests.managers.Util
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class PaginatedGui (
    val name: String,
    private val base: Inventory,
    itemStacks: List<ItemStack>,
    startEndSlots: Pair<Int, Int>,
    ignoredSlots: List<Int>,
    inventoryMap: List<Int>?
) {

    val pages: MutableList<Inventory> = mutableListOf()
    val isEmpty: Boolean = itemStacks.isEmpty()
    var size : Int = 0
        private set


    init {
        if (inventoryMap != null && inventoryMap.size != base.size) throw IllegalArgumentException("Invalid inventory map. List size must be equal to total inventory size.")

        var currentPage = newPage()
        var currentItem = 0
        var currentSlot = startEndSlots.first
        while (currentItem < itemStacks.size) {
            val slot = inventoryMap?.get(currentSlot) ?: currentSlot

            if (ignoredSlots.contains(slot)) {
                currentSlot++
                continue
            }

            if (currentSlot == startEndSlots.second) {
                currentPage = newPage()
                currentSlot = startEndSlots.first
            }



            if (currentPage.getItem(slot) == null) {
                currentPage.setItem(slot, itemStacks[currentItem])
                currentItem++
            }
            currentSlot++
        }
        size = pages.size
    }

    private fun newPage(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(base.holder, base.size, Util.colorText(name))
        for (i in 0 until base.size) {
            inventory.setItem(i, base.getItem(i))
        }
        pages.add(inventory)
        return inventory
    }


    fun getPage(page: Int): Inventory {
        return pages[page]
    }

    fun indexOf(page: Inventory): Int {
        return pages.indexOf(page)
    }
}