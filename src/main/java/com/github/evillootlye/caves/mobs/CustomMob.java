package com.github.evillootlye.caves.mobs;

import com.github.evillootlye.caves.DangerousCaves;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public interface CustomMob {
    NamespacedKey MOB_TYPE_KEY = new NamespacedKey(DangerousCaves.PLUGIN, "mob-type");
//  NamespacedKey MOB_TYPE_KEY = new NamespacedKey("dangerouscaves", "mob-type");

    boolean isThis(Entity entity);

    EntityType getType();

    String getCustomType();

    void spawn(Location location);

    default boolean canSpawn(EntityType type, Location location) {
        return true;
    }

    void setup(LivingEntity entity);

    int getWeight();

    static boolean isCustomMob(Entity entity) {
        return entity.getPersistentDataContainer().has(MOB_TYPE_KEY, PersistentDataType.STRING);
    }

    static String getCustomType(Entity entity) {
        return entity.getPersistentDataContainer().get(MOB_TYPE_KEY, PersistentDataType.STRING);
    }
}
