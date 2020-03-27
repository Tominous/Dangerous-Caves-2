package com.github.evillootlye.caves.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public abstract class AbstractMob implements CustomMob {
    private final EntityType type;
    private final String id;

    public AbstractMob(EntityType base, String id) {
        this.type = base.isAlive() ? base : EntityType.ZOMBIE;
        this.id = id;
    }

    @Override
    public final boolean isThis(Entity entity) {
        return id.equals(CustomMob.getCustomType(entity));
    }

    @Override
    public final EntityType getType() {
        return type;
    }

    @Override
    public final String getCustomType() {
        return id;
    }

    @Override
    public final void spawn(Location loc) {
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
        entity.getPersistentDataContainer().set(MOB_TYPE_KEY, PersistentDataType.STRING, id);
        setup(entity);
    }
}
