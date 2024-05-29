package dev.jsinco.simplequests.gui

import dev.jsinco.simplequests.QuestManager
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.GuiItemType
import dev.jsinco.simplequests.gui.tools.AbstractGui
import dev.jsinco.simplequests.gui.tools.Gui
import dev.jsinco.simplequests.objects.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class QuestsGui(val questPlayer: QuestPlayer, val category: String) : AbstractGui {

    companion object {
        val initItems = mapOf(
            Util.basicItem(Material.GREEN_STAINED_GLASS_PANE) to listOf(0, 8, 36, 44),
            Util.basicItem(Material.SHORT_GRASS) to listOf(1, 7, 37, 43),
            Util.basicItem(Material.FERN) to listOf(2, 6, 38, 42),
            Util.basicItem(Material.PINK_TULIP) to listOf(3, 5),
            Util.basicItem(Material.LILY_PAD) to listOf(4, 40),
            Util.basicItem(Material.PAPER, 10000).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "previous") } to listOf(39),
            Util.basicItem(Material.PAPER, 10001).also { Util.setGuiItemData(it, GuiItemType.PAGE_SWITCHER, "next") } to listOf(41)
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

    val generatedPages: MutableList<Gui> = mutableListOf()
    var lastQueriedQuestIndex: Int = 0

    fun generatePage(): Boolean {
        val quests = QuestManager.getQuests(category) ?: return false
        if (lastQueriedQuestIndex >= quests.size) {
            return false
        }


        val inv = Bukkit.createInventory(this, 45, "Quests")
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
            val item = Util.basicItem(quest.menuItem ?: Material.WHITE_STAINED_GLASS).also { Util.setGuiItemData(it, GuiItemType.QUEST, quest.id) }
            val meta = item.itemMeta
            meta.setDisplayName(Util.colorText("&a&l${quest.name}"))
            if (questPlayer.hasCompletedQuest(quest)) {
                meta.addEnchant(Enchantment.LUCK, 1, true)
            }
            meta.lore = listOf("", Util.colorText("&f${quest.questAction} ${quest.type} for ${quest.rewardType}"))
            item.itemMeta = meta

            items.add(item)
            lastQueriedQuestIndex++
        }

        val gui = Gui(
            inv,
            items,
            Pair(0, 45),
            listOf(),
            inventoryMap
        )

        generatedPages.add(gui)
        return true
    }

    override fun onInventoryClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val item = event.currentItem ?: return
        val itemData = Util.getGuiItemData(item.itemMeta) ?: return
        val player = event.whoClicked as Player


        when (itemData.first) {
            GuiItemType.QUEST -> {
                val quest = QuestManager.getQuest(category, itemData.second) ?: return
                questPlayer.startQuest(quest)
                player.closeInventory()
            }

            GuiItemType.PAGE_SWITCHER -> {
                when (itemData.second) {
                    "previous" -> {
                        if (generatedPages.indexOf(generatedPages.last()) == 0) {
                            return
                        }
                        player.openInventory(generatedPages[generatedPages.indexOf(generatedPages.last()) - 1].inv)
                    }
                    "next" -> { // TODO: redo this
                        var gui: Gui? = null
                        for (gui1 in generatedPages) {
                            if (gui1.inv == event.inventory) {
                                gui = gui1
                                break
                            }
                        }

                        if (generatedPages.indexOf(gui) == generatedPages.size - 1) {
                            if (generatePage()) {
                                player.openInventory(generatedPages.last().inv)
                            }
                        } else {
                            player.openInventory(generatedPages[generatedPages.indexOf(gui) + 1].inv)
                        }
                    }
                }
            }

            else -> {}
        }
    }

    override fun onInventoryClose(event: InventoryCloseEvent) {}

    override fun getInventory(): Inventory {
        return generatedPages.last().inv
    }
}