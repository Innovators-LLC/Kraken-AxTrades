package com.artillexstudios.axtrade.hooks.currency;

import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.artillexstudios.axtrade.AxTrade.HOOKS;

public class KingdomsXHook implements CurrencyHook {

    private Object getKingdom(UUID player) {
        try {
            Class<?> kingdomPlayer = Class.forName("org.kingdoms.constants.player.KingdomPlayer");
            Object data = kingdomPlayer.getMethod("getKingdomPlayer", UUID.class).invoke(null, player);
            return data.getClass().getMethod("getKingdom").invoke(data);
        } catch (Exception ex) {
            return null;
        }
    }

    private double getResourcePoints(Object kingdom) {
        try {
            Object points = kingdom.getClass().getMethod("getResourcePoints").invoke(kingdom);
            return points instanceof Number ? ((Number) points).doubleValue() : 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean addResourcePoints(Object kingdom, long amount) {
        try {
            kingdom.getClass().getMethod("addResourcePoints", long.class).invoke(kingdom, amount);
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
        return "KingdomsX";
    }

    @Override
    public Map<String, Object> getSettings() {
        return HOOKS.getSection("currencies." + getName()).getStringRouteMappedValues(true);
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public boolean usesDouble() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public double getBalance(@NotNull UUID player) {
        Object kingdom = getKingdom(player);
        if (kingdom == null) return 0.0D;
        return getResourcePoints(kingdom);
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(@NotNull UUID player, double amount) {
        Object kingdom = getKingdom(player);
        return CompletableFuture.completedFuture(kingdom != null && addResourcePoints(kingdom, (long) amount));
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(@NotNull UUID player, double amount) {
        Object kingdom = getKingdom(player);
        return CompletableFuture.completedFuture(kingdom != null && addResourcePoints(kingdom, (long) (amount * -1)));
    }
}
