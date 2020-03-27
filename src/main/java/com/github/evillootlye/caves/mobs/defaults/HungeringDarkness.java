package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.AbstractMob;
import com.github.evillootlye.caves.util.PlayerAttackedEvent;
import com.github.evillootlye.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.hungering-darkness")
public class HungeringDarkness extends AbstractMob implements Listener, Configurable {
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false);
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3, false, false);
    private int weight;
    private String name;
    private double damage;
    private boolean remove;

    public HungeringDarkness() {
        super(EntityType.HUSK, "hungering-darkness");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", ""));
        damage = cfg.getDouble("damage", 200);
        remove = cfg.getBoolean("remove-on-light", true);
    }

    @Override
    public boolean canSpawn(EntityType type, Location location) {
        return location.getBlock().getLightLevel() == 0;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.setCustomNameVisible(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setCanPickupItems(false);
        entity.setCustomNameVisible(false);
        entity.setCollidable(false);

        entity.addPotionEffect(INVISIBILITY);
        entity.addPotionEffect(SLOW);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if(!isThis(event.getEntity()) || event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER) return;
        if(event.getTarget().getLocation().getBlock().getLightLevel() > 0) {
            if(remove) {
                event.setCancelled(true);
                event.getEntity().remove();
            } else event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker())) return;
        if(event.getPlayer().getLocation().getBlock().getLightLevel() > 0) {
            event.setCancelled(true);
            if(remove) event.getAttacker().remove();
        } else event.setDamage(damage);
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
