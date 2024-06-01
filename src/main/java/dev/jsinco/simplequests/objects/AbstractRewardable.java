package dev.jsinco.simplequests.objects;

import dev.jsinco.simplequests.enums.RewardType;
import dev.jsinco.simplequests.hooks.PlayerPointsHook;
import dev.jsinco.simplequests.hooks.VaultHook;
import dev.jsinco.simplequests.managers.Util;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRewardable {

    @Nullable protected final RewardType rewardType;
    @Nullable protected final Object rewardValue;

    public AbstractRewardable(@Nullable RewardType rewardType, @Nullable Object rewardValue) {
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
    }

    public void executeReward(OfflinePlayer player) {
        if (rewardType == null || rewardValue == null) return;
        final Player onlinePlayer = player.getPlayer();
        switch (rewardType) {
            case MONEY -> {
                final Economy econ = VaultHook.getEconomy();
                econ.depositPlayer(player, Double.parseDouble(rewardValue.toString()));

                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received &a$" + rewardValue + "&r!"));
                }
            }
            case COMMAND -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardValue.toString().replace("%player%", player.getName()));
                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received a reward!"));
                }
            }
            case POINTS -> {
                final PlayerPointsAPI playerPointsAPI = PlayerPointsHook.getApi();
                playerPointsAPI.give(player.getUniqueId(), Integer.parseInt(rewardValue.toString()));
                if (onlinePlayer != null) {
                    onlinePlayer.sendMessage(Util.getPrefix() + Util.colorText("You have received &a" + rewardValue + "&rLumins!"));
                }
            }
        }
    }

    @Nullable
    public RewardType getRewardType() {
        return rewardType;
    }

    @Nullable
    public Object getRewardValue() {
        return rewardValue;
    }

}
