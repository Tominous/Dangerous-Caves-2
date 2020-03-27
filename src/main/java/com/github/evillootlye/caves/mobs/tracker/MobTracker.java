package com.github.evillootlye.caves.mobs.tracker;

import com.github.evillootlye.caves.mobs.TickingMob;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiConsumer;

public interface MobTracker {
    void scan(BiConsumer<TickingMob, LivingEntity> action);

    void register(TickingMob mob);
}
