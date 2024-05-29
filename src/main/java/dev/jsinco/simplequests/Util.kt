package dev.jsinco.simplequests

import dev.jsinco.simplequests.enums.GuiItemType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object Util {

    private val plugin: SimpleQuests = SimpleQuests.getInstance()
    @JvmStatic var prefix: String = colorText(SimpleQuests.getConfigFile().getString("prefix") ?: "&8[&6SimpleQuests&8]")
    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"

    @JvmStatic
    fun debugLog(msg: String) {
        Bukkit.getConsoleSender().sendMessage(colorText("[SimpleQuests] &6DEBUG: $msg"))
    }

    @JvmStatic
    fun colorText(text: String): String {
        val texts = text.split(String.format(WITH_DELIMITER, "&").toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if (texts[i].equals("&", ignoreCase = true)) {
                i++
                if (texts[i][0] == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)).toString() + texts[i].substring(7))
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                }
            } else {
                finalText.append(texts[i])
            }
            i++
        }
        return finalText.toString()
    }

    @JvmStatic
    fun format(m: String): String {
        var name = m.lowercase().replace("_", " ")
        name = name.substring(0, 1).uppercase() + name.substring(1)
        for (i in name.indices) {
            if (name[i] == ' ' && i + 2 < name.length) {
                name = name.substring(0, i) + " " + name[i + 1].toString().uppercase() + name.substring(i + 2) // Capitalize first letter of each word
            }
        }
        return name
    }

    fun setGuiItemData(item: ItemStack, guiItemType: GuiItemType, data: String) {
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin, guiItemType.name), PersistentDataType.STRING, data)
        item.itemMeta = meta
    }

    fun getGuiItemData(meta: ItemMeta): Pair<GuiItemType, String>? {
        val dataContainer = meta.persistentDataContainer
        dataContainer.keys.forEach {
            if (it.namespace == "simplequests") {
                val key = GuiItemType.getItemType(it.key.uppercase()) ?: return null
                return Pair(key, dataContainer.get(NamespacedKey(plugin, key.name), PersistentDataType.STRING) ?: return null)
            }
        }
        return null
    }

    fun basicItem(m: Material): ItemStack {
        return createGuiItem(m, "&0", listOf(), false)
    }

    fun basicItem(m: Material, customModelData: Int?): ItemStack {
        return createGuiItem(m, "&0", listOf(), false, customModelData)
    }


    fun createGuiItem(material: Material, name: String, lore: List<String>, glow: Boolean, customModelData: Int? = null): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta!!
        meta.setDisplayName(colorText(name))
        meta.lore = lore.map { colorText(it) }
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        item.itemMeta = meta
        return item
    }
}