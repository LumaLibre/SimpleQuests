package dev.jsinco.simplequests.guis

import dev.jsinco.abstractjavafilelib.ConfigurationSection
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.guis.tools.AbstractGui
import dev.jsinco.simplequests.guis.tools.PaginatedGui
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class CategoriesGui(val questPlayer: QuestPlayer) : AbstractGui() {

    companion object {
        val initItems = mapOf(
            Util.basicItem(Material.GREEN_STAINED_GLASS_PANE) to listOf(0, 8, 45, 53),
            Util.basicItem(Material.SHORT_GRASS) to listOf(1, 7, 46, 52),
            Util.basicItem(Material.FERN) to listOf(2, 6, 47, 51),
            Util.basicItem(Material.PINK_TULIP) to listOf(3, 5),
            Util.basicItem(Material.LILY_PAD) to listOf(4),
            Util.basicItem(Material.PAPER, "&#C08EFA&lPrevious",10000).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "previous") } to listOf(48),
            Util.basicItem(Material.PAPER, "&#C08EFA&lNext", 10001).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "next") } to listOf(50)
        )
    }


    private val paginatedGui: PaginatedGui

    init {
        val inv = Bukkit.createInventory(this, 54, "SimpleQuests Categories")
        for (item in initItems) {
            for (slot in item.value) {
                inv.setItem(slot, item.key)
            }
        }

        inv.setItem(45, Util.createGuiItem(Material.DRAGON_BREATH, "&#C08EFA&lShow Progress Bar&7: &#f498f6${questPlayer.isShowActionBarProgress}", listOf(), questPlayer.isShowActionBarProgress, null)
            .also { Util.setGuiItemData(it, GuiItemType.SHOW_PROGRESS_BAR, "") })
        inv.setItem(49, Util.getPlayerStatsIcon(questPlayer))

        val categoryItems: MutableList<ItemStack> = mutableListOf()
        val configSection: ConfigurationSection? = SimpleQuests.getConfigFile().getConfigurationSection("categories")  // TODO

        for (cat in SimpleQuests.getQuestsFile().keys) {
            val itemStack = ItemStack(Material.getMaterial(configSection?.getString("$cat.icon") ?: "WHITE_STAINED_GLASS_PANE") ?: Material.WHITE_STAINED_GLASS_PANE)
            val meta = itemStack.itemMeta
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)
            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.setDisplayName(Util.colorText( "&#f498f6&l${Util.format(cat)} Quests"))
            meta.lore = listOf("&7Click to view quests in the", "&#f498f6${Util.format(cat)} &7category!",
                "", "&7Category Completion: &#F7FFC9${questPlayer.getCategoryCompletion(cat)}").map { Util.colorText(it) }
            itemStack.itemMeta = meta

            Util.setGuiItemData(itemStack, GuiItemType.CATEGORY, cat)
            categoryItems.add(itemStack)
        }

        paginatedGui = PaginatedGui(
            Util.colorText(
            "&#F670F1&lQ&#EB74F3&lu&#E179F5&le&#D67DF7&ls&#CC81F9&lt &#C185FB&lC&#B78AFD&la&#AC8EFF&lt&#A590FF&le&#9F91FF&lg&#9893FF&lo&#9195FF&lr&#8A97FF&li&#8498FF&le&#7D9AFF&ls"
        ), inv, categoryItems, Pair(20, 34), listOf(25, 26, 27, 28), null)
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val clickedItem = event.currentItem ?: return
        val meta = clickedItem.itemMeta ?: return
        val player = event.whoClicked as Player

        val itemData = Util.getGuiItemData(meta) ?: return

        when (itemData.first) {
            GuiItemType.CATEGORY -> {
                val questsGui = QuestsGui(questPlayer, itemData.second)
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

            GuiItemType.ACHIEVEMENTS_GUI_OPENER -> {
                player.sendMessage("${Util.prefix}Quest Achievements are not viewable at this time.")
                //player.openInventory(AchievementsGui(questPlayer).inventory)
            }

            GuiItemType.SHOW_PROGRESS_BAR -> {
                questPlayer.isShowActionBarProgress = !questPlayer.isShowActionBarProgress
                clickedItem.let {
                    val meta = it.itemMeta
                    meta.setDisplayName(Util.colorText("&#C08EFA&lShow Progress Bar&7: &#f498f6${questPlayer.isShowActionBarProgress}"))
                    if (questPlayer.isShowActionBarProgress) {
                        meta.addEnchant(Enchantment.LUCK, 1, true)
                    } else {
                        meta.removeEnchant(Enchantment.LUCK)
                    }
                    it.itemMeta = meta
                }
            }

            else -> {}
        }
    }

    override fun getInventory(): Inventory {
        return paginatedGui.getPage(0)
    }
}