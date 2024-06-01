package dev.jsinco.simplequests.guis

import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.guis.tools.AbstractGui
import dev.jsinco.simplequests.guis.tools.PaginatedGui
import dev.jsinco.simplequests.managers.AchievementsManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.Achievement
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class AchievementsGui(val questPlayer: QuestPlayer) : AbstractGui() {

    val paginatedGui: PaginatedGui

    init {
        val inv = Bukkit.createInventory(this, 54, "SimpleQuests Achievements")
        for (item in CategoriesGui.initItems) { // TODO
            for (slot in item.value) {
                inv.setItem(slot, item.key)
            }
        }
        inv.setItem(49, Util.basicItem(Material.BARRIER, "&c&lReturn", null).also { Util.setGuiItemData(it, GuiItemType.RETURN, "return") })

        val items: MutableList<ItemStack> = mutableListOf()

        for (achievement in AchievementsManager.getAchievements()) {
            val item = ItemStack(achievement.menuItem ?: Material.WHITE_STAINED_GLASS_PANE)
            val meta = item.itemMeta
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
            meta.setDisplayName(Util.colorText("&f${achievement.name}"))
            meta.lore = achievement.description.map { Util.colorText("&f$it") }.toMutableList().also { it.add(0, "") }

            if (questPlayer.hasAchievement(achievement)) {
                meta.addEnchant(Enchantment.LUCK, 1, true)
            }

            item.itemMeta = meta
            Util.setGuiItemData(item, GuiItemType.ACHIEVEMENT, achievement.id)
            items.add(item)
        }

        paginatedGui = PaginatedGui(
            Util.colorText(
                "&#F670F1&lA&#E975F4&lc&#DB7BF6&lh&#CE80F9&li&#C086FB&le&#B38BFE&lv&#A88FFF&le&#9F91FF&lm&#9793FF&le&#8E96FF&ln&#8698FF&lt&#7D9AFF&ls"
            ), inv, items, Pair(19, 35), listOf(27, 28), null)
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val itemData = Util.getGuiItemData(event.currentItem?.itemMeta ?: return) ?: return
        val player = event.whoClicked as Player

        when (itemData.first) {
            GuiItemType.ACHIEVEMENT -> {
                val achievement: Achievement = AchievementsManager.getAchievementById(itemData.second) ?: return
                questPlayer.addAchievement(achievement)
            }

            GuiItemType.RETURN -> {
                player.openInventory(CategoriesGui(questPlayer).inventory)
            }

            GuiItemType.PAGE_SWITCHER -> {
                val inv = event.inventory
                when (itemData.second) {
                    "previous" -> {
                        if (paginatedGui.indexOf(inv) == 0) return
                        player.openInventory(paginatedGui.getPage(paginatedGui.indexOf(inv) - 1))
                    }
                    "next" -> {
                        player.openInventory(paginatedGui.getPage(paginatedGui.indexOf(inv) + 1))
                    }
                }
            }

            else -> {
            }
        }
    }

    override fun getInventory(): Inventory {
        return paginatedGui.getPage(0)
    }
}