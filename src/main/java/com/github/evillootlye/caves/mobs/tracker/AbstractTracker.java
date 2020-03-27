package com.github.evillootlye.caves.mobs.tracker;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickingMob;
import com.github.evillootlye.caves.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configurable.Path("mobs")
public abstract class AbstractTracker implements MobTracker, Configurable {
    private final Set<String> worlds;
    private final Map<String, TickingMob> mobs;

    public AbstractTracker() {
        worlds = new HashSet<>();
        mobs = new HashMap<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    public TickingMob getTickingMob(String type) {
        return mobs.get(type);
    }

    public Collection<TickingMob> getTickingTypes() {
        return mobs.values();
    }

    @Override
    public void register(TickingMob mob) {
        mobs.put(mob.getCustomType(), mob);
    }

    public boolean checkWorld(World world) {
        return worlds.contains(world.getName());
    }

    public final Collection<World> getWorlds() {
        Set<World> worlds = new HashSet<>();
        for(String worldStr : this.worlds) {
            World world = Bukkit.getWorld(worldStr);
            if(world != null) worlds.add(world);
        }
        return worlds;
    }
}
