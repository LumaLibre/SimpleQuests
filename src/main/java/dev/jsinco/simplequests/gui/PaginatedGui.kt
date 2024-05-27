package dev.jsinco.simplequests.gui

import dev.jsinco.simplequests.Util
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class PaginatedGui (
    val name: String,
    private val base: Inventory,
    itemStacks: List<ItemStack>,
    startEndSlots: Pair<Int, Int>,
    ignoredSlots: List<Int>,
) {

    val pages: MutableList<Inventory> = mutableListOf()
    val isEmpty = itemStacks.isEmpty()
    var size : Int = 0
        private set


    init {
        var currentPage = newPage()
        var currentItem = 0
        var currentSlot = startEndSlots.first
        while (currentItem < itemStacks.size) {
            if (ignoredSlots.contains(currentSlot)) {
                currentSlot++
                continue
            }

            if (currentSlot == startEndSlots.second) {
                currentPage = newPage()
                currentSlot = startEndSlots.first
            }

            if (currentPage.getItem(currentSlot) == null) {
                currentPage.setItem(currentSlot, itemStacks[currentItem])
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