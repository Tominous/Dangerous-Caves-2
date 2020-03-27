package com.github.evillootlye.caves.mobs.tracker;

import com.github.evillootlye.caves.mobs.CustomMob;
import com.github.evillootlye.caves.mobs.TickingMob;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiConsumer;

public class SearchTracker extends AbstractTracker {
    @Override
    public void scan(BiConsumer<TickingMob, LivingEntity> action) {
        for(World world : getWorlds()) for(Entity entity : world.getEntities()) {
            if(!(entity instanceof LivingEntity)) continue;
            String type = CustomMob.getCustomType(entity);
            if(type == null) continue;
            TickingMob mob = getTickingMob(type);
            if(mob != null) action.accept(mob, (LivingEntity)entity);
        }
    }
}
