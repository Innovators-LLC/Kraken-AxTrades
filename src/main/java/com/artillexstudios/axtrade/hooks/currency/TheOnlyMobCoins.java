package com.artillexstudios.axtrade.hooks.currency;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class TheOnlyMobCoins implements CurrencyHook {

    private Object getPlayerData(UUID player) {
        try {
            Class<?> api = Class.forName("me.aglerr.mobcoins.api.MobCoinsAPI");
            return api.getMethod("getPlayerData", org.bukkit.entity.Player.class).invoke(null, Bukkit.getPlayer(player));
        } catch (Exception ex) {
            return null;
        }
    }

    private double getCoins(Object data) {
        try {
            return ((Number) data.getClass().getMethod("getCoins").invoke(data)).doubleValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean updateCoins(Object data, String method, double amount) {
        try {
            data.getClass().getMethod(method, double.class).invoke(data, amount);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void setup() {
    }

    @Override
    public String getName() {
        return "TheOnly-MobCoins";
    }

    @Override
    public Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    @Override
    public boolean usesDouble() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        Object data = getPlayerData(player);
        if (data == null) return 0;
        return getCoins(data);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        Object data = getPlayerData(player);
        return CompletableFuture.completedFuture(data != null && updateCoins(data, "addCoins", amount));
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        Object data = getPlayerData(player);
        return CompletableFuture.completedFuture(data != null && updateCoins(data, "reduceCoins", amount));
    }
}
