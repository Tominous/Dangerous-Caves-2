package com.github.evillootlye.caves.mobs;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.github.evillootlye.caves.DangerousCaves;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.tracker.MobTracker;
import com.github.evillootlye.caves.mobs.tracker.RegisteringTracker;
import com.github.evillootlye.caves.mobs.tracker.SearchTracker;
import com.github.evillootlye.caves.ticks.TickLevel;
import com.github.evillootlye.caves.ticks.Tickable;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.AliasMethod;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configurable.Path("mobs")
public class MobsManager implements Listener, Tickable, Configurable {
    private final DangerousCaves plugin;
    private final Map<String, CustomMob> mobs;
    private final Set<String> worlds;
    private MobTracker tracker;
    private AliasMethod<CustomMob> mobsPool;
    private int yMin;
    private int yMax;
    private double chance;
    private boolean blockRename;

    public MobsManager(DangerousCaves plugin) {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            Bukkit.getPluginManager().registerEvents(new PaperCreatureSpawnListener(), plugin);
        } catch (ClassNotFoundException e) {
            Bukkit.getPluginManager().registerEvents(new SpigotCreatureSpawnListener(), plugin);
        }
        this.plugin = plugin;
        mobs = new HashMap<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        if(tracker == null) {
            if(cfg.getBoolean("alternative-tracker", false))
                tracker = new RegisteringTracker();
            else
                tracker = new SearchTracker();
        }
        chance = cfg.getDouble("try-chance", 50) / 100;
        blockRename = cfg.getBoolean("restrict-rename", false);
        yMin = cfg.getInt("y-min", 0);
        yMax = cfg.getInt("y-max", 64);
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
        if(chance > 0) recalculate();
    }

    public void recalculate() {
        Set<CustomMob> mobsSet = new HashSet<>();
        mobs.values().forEach(m -> {if(m.getWeight() > 0) mobsSet.add(m);});
        if(mobsSet.isEmpty())
            chance = 0;
        else
            mobsPool = new AliasMethod<>(mobsSet, CustomMob::getWeight);
    }

    public void register(CustomMob mob) {
        if(!mobs.containsKey(mob.getCustomType())) {
            mobs.put(mob.getCustomType(), mob);
            if(mob instanceof TickingMob)
                tracker.register((TickingMob) mob);
            if(mob instanceof Listener)
                Bukkit.getPluginManager().registerEvents((Listener)mob, plugin);
            if(mob instanceof Configurable)
                plugin.getConfiguration().register((Configurable)mob);
            if(mob instanceof Tickable)
                plugin.getDynamics().subscribe((Tickable)mob);
        }
    }

    @Override
    public void tick() {
        tracker.scan(TickingMob::tick);
    }

    @Override
    public TickLevel getTickLevel() {
        return TickLevel.ENTITY;
    }

    public boolean summon(String type, Location loc) {
        CustomMob mob = mobs.get(type.toLowerCase());
        if(mob == null) return false;
        mob.spawn(loc);
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        if(blockRename && CustomMob.isCustomMob(event.getRightClicked())) event.setCancelled(true);
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if(chance <= 0 || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
                loc.getBlockY() > yMax || loc.getBlockY() < yMin ||
                !worlds.contains(loc.getWorld().getName()) ||
                !Locations.isCave(loc) ||
                !Rnd.chance(chance))
            return false;
        CustomMob mob = mobsPool.next();
        if(mob.canSpawn(type, loc)) {
            mob.spawn(loc);
            return true;
        }
        return false;
    }

    private class PaperCreatureSpawnListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(PreCreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getType(), event.getSpawnLocation(), event.getReason()))
                event.setCancelled(true);
        }
    }

    private class SpigotCreatureSpawnListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onSpawn(CreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
                event.setCancelled(true);
        }
    }
}
