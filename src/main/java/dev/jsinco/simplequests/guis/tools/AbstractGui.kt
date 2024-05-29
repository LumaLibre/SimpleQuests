package dev.jsinco.simplequests.guis.tools

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryHolder

abstract class AbstractGui : InventoryHolder {

    open fun onInventoryClick(event: InventoryClickEvent) {
    }

    open fun onInventoryClose(event: InventoryCloseEvent) {
    }
}