package com.artillexstudios.axtrade.utils;

import co.solecloth7.krakenmc.api.KrakenApi;
import co.solecloth7.krakenmc.api.KrakenApis;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class KrakenUtils {
    private static final NamespacedKey LEVEL_REQUIREMENT_KEY = new NamespacedKey("krakenmc", "level_requirement");

    public static int getLevelRequirement(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) return 0;
        Integer level = item.getItemMeta().getPersistentDataContainer().get(LEVEL_REQUIREMENT_KEY, PersistentDataType.INTEGER);
        return level == null ? 0 : Math.max(level, 0);
    }

    public static boolean canReceive(Player player, @Nullable ItemStack item) {
        int levelRequirement = getLevelRequirement(item);
        if (levelRequirement == 0) return true;
        KrakenApi api = KrakenApis.INSTANCE.get();
        return api != null && api.masteryLevel(player.getUniqueId()) >= levelRequirement;
    }
}
