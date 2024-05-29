package dev.jsinco.simplequests.gui.tools

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryHolder

interface AbstractGui : InventoryHolder {
    fun onInventoryClick(event: InventoryClickEvent)
    fun onInventoryClose(event: InventoryCloseEvent)
}