package com.github.evillootlye.caves.caverns;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.ticks.TickLevel;
import com.github.evillootlye.caves.ticks.Tickable;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Temperature with accumulation based on player's depth

@Configurable.Path("caverns.temperature")
public class DepthTemperature implements Tickable, Configurable {
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, 120, 1);
    private static final PotionEffect SLOW_DIGGING = new PotionEffect(PotionEffectType.SLOW_DIGGING, 55, 1);

    private final List<String> messages;
    private final Set<Material> coldItems;
    private final Set<String> worlds;

    private double chance;
    private int y;
    private boolean fireRes;

    public DepthTemperature() {
        coldItems = new HashSet<>();
        worlds = new HashSet<>();
        messages = new ArrayList<>();
    }


    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 0.8) / 100;
        y = cfg.getInt("y-max", 32);
        fireRes = cfg.getBoolean("fire-resistance", true);
        messages.clear();
        messages.addAll(Utils.clr(cfg.getStringList("messages")));
        coldItems.clear();
        for(String typeStr : cfg.getStringList("cold-items")) {
            Material type = Utils.getEnum(Material.class, typeStr);
            if(type != null) coldItems.add(type);
        }
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    @Override
    public void tick() {
        for(World world : Bukkit.getWorlds()) {
            if(!worlds.contains(world.getName())) continue;
            for(Player player : world.getPlayers()) {
                if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;
                if(fireRes && player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) continue;
                Location loc = player.getLocation();
                if(loc.getBlockY() > y || !Locations.isCave(loc)) continue;
                if(loc.getBlock().getType() == Material.WATER) continue;
                Inventory inv = player.getInventory();
                boolean check = true;
                for(Material item : coldItems) {
                    if(inv.contains(item)) {
                        check = false;
                        break;
                    }
                }
                if(check && Rnd.chance(chance)) {
                    player.addPotionEffect(SLOW);
                    player.addPotionEffect(SLOW_DIGGING);
                    if(!messages.isEmpty()) player.sendMessage(Rnd.randomItem(messages));
                }
            }
        }
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.ENTITY;
    }
}
