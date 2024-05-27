package dev.jsinco.simplequests.hooks

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit


object VaultHook {
    private var econ: Economy? = null

    @JvmStatic
    fun getEconomy(): Economy? {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null
        }

        if (econ == null) {
            val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
            if (rsp != null) {
                econ = rsp.provider
            }
        }
        return econ
    }
}