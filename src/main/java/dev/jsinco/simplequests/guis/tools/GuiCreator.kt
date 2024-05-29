package dev.jsinco.simplequests.guis.tools

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GuiCreator (
    val inv: Inventory,
    itemStacks: List<ItemStack>,
    startEndSlots: Pair<Int, Int>,
    ignoredSlots: List<Int>,
    inventoryMap: List<Int>?
) {


    init {
        if (inventoryMap != null && inventoryMap.size != inv.size) throw IllegalArgumentException("Invalid inventory map. List size must be equal to total inventory size.")

        var currentItem = 0
        var currentSlot = startEndSlots.first
        while (currentItem < itemStacks.size) {
            val slot = inventoryMap?.get(currentSlot) ?: currentSlot
            if (ignoredSlots.contains(slot)) {
                currentSlot++
                continue
            }

            if (currentSlot == startEndSlots.second) {
                break
            }

            if (inv.getItem(slot) == null) {
                inv.setItem(slot, itemStacks[currentItem])
                currentItem++
            }
            currentSlot++
        }
    }

}