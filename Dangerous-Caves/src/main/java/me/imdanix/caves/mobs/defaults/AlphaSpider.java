package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.alpha-spider")
public class AlphaSpider extends CustomMob implements Listener, Configurable {
    private static final PotionEffect POISON = new PotionEffect(PotionEffectType.POISON, 75, 1);
    private static final PotionEffect REGENERATION = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, true);

    private int weight;
    private String name;
    private double cobwebChance;
    private double minionChance;

    public AlphaSpider() {
        super(EntityType.SPIDER, "alpha-spider");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Alpha Spider"));
        cobwebChance = cfg.getDouble("cobweb-chance", 14.29) / 100;
        minionChance = cfg.getDouble("minion-chance", 6.67) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(REGENERATION);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!isThis(event.getDamager())) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        Entity damager = event.getDamager();
        if (Rnd.nextBoolean()) {
            if (minionChance > 0 && Rnd.chance(minionChance))
                damager.getWorld().spawnEntity(damager.getLocation(), EntityType.CAVE_SPIDER);

            if (cobwebChance > 0) {
                Location loc = event.getEntity().getLocation();
                loc.getBlock().setType(VMaterial.COBWEB.get());
                entity.getEyeLocation().getBlock().setType(VMaterial.COBWEB.get());

                Locations.loop(3, loc, l -> {
                    if (Compatibility.isAir(l.getBlock().getType()) && Rnd.chance(cobwebChance)) {
                        l.getBlock().setType(VMaterial.COBWEB.get());
                    }}
                );
            }
        } else entity.addPotionEffect(POISON);
    }
}
