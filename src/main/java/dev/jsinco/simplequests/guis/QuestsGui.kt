package dev.jsinco.simplequests.guis

import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.guis.tools.AbstractGui
import dev.jsinco.simplequests.guis.tools.GuiCreator
import dev.jsinco.simplequests.managers.QuestManager
import dev.jsinco.simplequests.managers.Util
import dev.jsinco.simplequests.objects.Quest
import dev.jsinco.simplequests.objects.QuestPlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class QuestsGui(val questPlayer: QuestPlayer, val category: String) : AbstractGui() {

    companion object {
        val initItems = mapOf(
            Util.basicItem(Material.GREEN_STAINED_GLASS_PANE) to listOf(0, 8, 36, 44),
            Util.basicItem(Material.SHORT_GRASS) to listOf(1, 7, 37, 43),
            Util.basicItem(Material.FERN) to listOf(2, 6, 38, 42),
            Util.basicItem(Material.PINK_TULIP) to listOf(3, 5),
            Util.basicItem(Material.LILY_PAD) to listOf(4),
            Util.basicItem(Material.PAPER, "&#C08EFA&lPrevious", 10000).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "previous") } to listOf(39),
            Util.basicItem(Material.PAPER, "&#C08EFA&lNext", 10001).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "next") } to listOf(41),
            Util.basicItem(Material.BARRIER, "&c&lReturn", null).also { Util.setGuiItemData(it, GuiItemType.RETURN, "return") } to listOf(40),
        )
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

    val generatedPages: MutableList<Inventory> = mutableListOf()
    var lastQueriedQuestIndex: Int = 0

    fun generatePage(): Boolean {
        val quests = QuestManager.getQuests(category) ?: return false
        if (lastQueriedQuestIndex >= quests.size) {
            return false
        }


        val inv = Bukkit.createInventory(this, 45, MiniMessage.miniMessage().deserialize("<bold><gradient:#F670F1:#AC8EFF:#7D9AFF>${Util.format(category)} Quests"))
        for (item in initItems) {
            for (slot in item.value) {
                inv.setItem(slot, item.key)
            }
        }

        val items: MutableList<ItemStack> = mutableListOf()

        for (i in 0 until 27) {
            if (lastQueriedQuestIndex >= quests.size) {
                break
            }

            val quest = quests[lastQueriedQuestIndex]
            val item = Util.basicItem(quest.menuItem ?: Material.WHITE_STAINED_GLASS_PANE).also { Util.setGuiItemData(it, GuiItemType.QUEST, quest.id) }
            updateQuestGuiItem(quest, item)

            items.add(item)
            lastQueriedQuestIndex++
        }


        generatedPages.add(GuiCreator(inv, items, Pair(0, 45), listOf(), inventoryMap).inv)
        return true
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val item = event.currentItem ?: return
        val itemData = Util.getGuiItemData(item.itemMeta) ?: return
        val player = event.whoClicked as Player
        val inv = event.inventory


        when (itemData.first) {
            GuiItemType.QUEST -> {
                val quest = QuestManager.getQuest(category, itemData.second) ?: return
                if (event.isLeftClick) {
                    questPlayer.startQuest(quest)
                } else {
                    questPlayer.dropQuest(quest)
                }
                updateQuestGuiItem(quest, item)
            }

            GuiItemType.PAGE_SWITCHER -> {
                when (itemData.second) {
                    "previous" -> {
                        if (generatedPages.indexOf(inv) == 0) {
                            return
                        }
                        player.openInventory(generatedPages[generatedPages.indexOf(inv) - 1])
                    }
                    "next" -> {
                        if (generatedPages.indexOf(inv) == generatedPages.size - 1) {
                            if (generatePage()) {
                                player.openInventory(generatedPages.last())
                            }
                        } else {
                            player.openInventory(generatedPages[generatedPages.indexOf(inv) + 1])
                        }
                    }
                }
            }

            GuiItemType.RETURN -> {
                player.openInventory(CategoriesGui(questPlayer).inventory)
            }

            else -> {}
        }
    }

    override fun getInventory(): Inventory {
        return generatedPages.last()
    }

    private fun updateQuestGuiItem(quest: Quest, item: ItemStack) {
        val meta = item.itemMeta
        meta.setDisplayName(Util.colorText("&#7280FF&l${quest.name}"))
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES)

        if (!questPlayer.hasCompletedQuest(quest.requiredCompletedQuest)) {
            val name = ChatColor.stripColor(Util.colorText(quest.requiredCompletedQuestObject?.name ?: "Unknown"))
            meta.lore = listOf("", "&#F7FFC9You must complete &6\"${name}\"", "&#F7FFC9to view or start this quest!").map { Util.colorText(it) }
            item.itemMeta = meta; return
        } else if (questPlayer.hasCompletedQuest(quest)) {
            meta.lore = listOf("", Util.colorText("&dQuest completed!"))
            item.type = Material.PAPER
            meta.addEnchant(Enchantment.LUCK, 1, true)
            item.itemMeta = meta
            item.itemMeta = meta; return
        }


        val activeQuest = questPlayer.getInProgressQuest(quest)
        meta.lore = quest.description.toMutableList().also {
            it.add(0, "")
            if (activeQuest != null) {
                it.add("")
                it.add("&6Progress&7: ${Util.createProgressBar(activeQuest)} &7(${String.format("%.1f", Util.fractionToDecimal(activeQuest.progress, activeQuest.amount))}%)")
            }
            it.addAll(listOf("",
                "&6• &eLeft click to begin this quest",
                "&6• &eRight click to drop this quest"))
        }.map { Util.colorText(it) }


        if (activeQuest != null) {
            meta.addEnchant(Enchantment.LUCK, 1, true)
        } else {
            meta.removeEnchantments()
        }

        item.itemMeta = meta
    }
}