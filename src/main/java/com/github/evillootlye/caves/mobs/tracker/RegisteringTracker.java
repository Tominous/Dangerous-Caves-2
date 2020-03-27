package com.github.evillootlye.caves.mobs.tracker;

import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.mobs.TickingMob;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class RegisteringTracker extends AbstractTracker implements Listener {
    private final Map<World, MobRegistry> mobs;

    public RegisteringTracker() {
        mobs = new WeakHashMap<>();
    }

    @Override
    public void scan(BiConsumer<TickingMob, LivingEntity> action) {
        for(World world : getWorlds()) {
            MobRegistry registry = mobs.get(world);
            if(registry == null) continue;
            for(Map.Entry<TickingMob, Set<LivingEntity>> entry : registry) {
                Iterator<LivingEntity> iterator = entry.getValue().iterator();
                while(iterator.hasNext()) {
                    LivingEntity entity = iterator.next();
                    if(entity.isDead()) {
                        iterator.remove();
                        continue;
                    }
                    action.accept(entry.getKey(), entity);
                }
            }
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        mobs.remove(event.getWorld());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        if(checkWorld(world)) {
            MobRegistry registry = mobs.get(world);
            if(registry == null) {
                registry = new MobRegistry();
                mobs.put(world, registry);
            }
            for(Entity entity : event.getChunk().getEntities())
                registry.put(entity);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        World world = event.getWorld();
        if(checkWorld(world)) {
            MobRegistry registry = mobs.get(world);
            if(registry == null) {
                registry = new MobRegistry();
                mobs.put(world, registry);
            }
            for(Entity entity : event.getChunk().getEntities())
                registry.remove(entity);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        World world = event.getEntity().getWorld();
        if(checkWorld(world)) {
            MobRegistry registry = mobs.get(world);
            if(registry == null) {
                registry = new MobRegistry();
                mobs.put(world, registry);
            }
            registry.remove(event.getEntity());
        }
    }

    private class MobRegistry implements Iterable<Map.Entry<TickingMob, Set<LivingEntity>>> {
        private final Map<TickingMob, Set<LivingEntity>> tickingMobs;

        public MobRegistry() {
            tickingMobs = new HashMap<>();
            getTickingTypes().forEach(t -> tickingMobs.put(t, new HashSet<>()));
        }

        public void put(Entity entity) {
            String typeStr = CustomMob.getCustomType(entity);
            if(typeStr != null) getOrPut(typeStr).add((LivingEntity)entity);
        }

        public void remove(Entity entity) {
            String typeStr = CustomMob.getCustomType(entity);
            if(typeStr != null) getOrPut(typeStr).remove(entity);
        }

        private Set<LivingEntity> getOrPut(String type) {
            return tickingMobs.computeIfAbsent(getTickingMob(type), k -> new HashSet<>());

        }

        @Override
        public Iterator<Map.Entry<TickingMob, Set<LivingEntity>>> iterator() {
            return tickingMobs.entrySet().iterator();
        }
    }
}
