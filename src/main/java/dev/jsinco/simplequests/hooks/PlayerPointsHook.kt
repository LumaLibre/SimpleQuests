package dev.jsinco.simplequests.hooks

import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit

object PlayerPointsHook {
    private var playerPointsAPI: PlayerPointsAPI? = null

    @JvmStatic
    fun getApi(): PlayerPointsAPI? {
        if (playerPointsAPI == null) {
            if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
                playerPointsAPI = PlayerPoints.getInstance().api
            }
        }
        return playerPointsAPI
    }
}