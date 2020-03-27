package com.github.evillootlye.caves.mobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class TickingMob extends AbstractMob {
    public TickingMob(EntityType base, String id) {
        super(base, id);
    }

    public abstract void tick(LivingEntity entity);
}
