package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickingMob;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.PlayerAttackedEvent;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.magma-monster")
public class MagmaMonster extends TickingMob implements Listener, Configurable {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
            Integer.MAX_VALUE, 1, false, false);
    private static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY,
            Integer.MAX_VALUE, 1, false, false);

    private static final ItemStack CHESTPLATE;
    private static final ItemStack BOOTS;
    private static final ItemStack LEGGINGS;
    private static final ItemStack POWDER;
    static {
        CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) CHESTPLATE.getItemMeta();
        meta.setColor(Color.fromRGB(252, 115, 69));
        CHESTPLATE.setItemMeta(meta);
        BOOTS = new ItemStack(Material.LEATHER_LEGGINGS);
        BOOTS.setItemMeta(meta);
        LEGGINGS = new ItemStack(Material.LEATHER_BOOTS);
        LEGGINGS.setItemMeta(meta);
        POWDER = new ItemStack(Material.BLAZE_POWDER);
    }

    private int weight;
    private String name;
    private double fireChance;
    private double magmaChance;

    public MagmaMonster() {
        super(EntityType.ZOMBIE, "magma-monster");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Magma Monster"));
        fireChance = cfg.getDouble("fire-chance", 7.14) / 100;
        magmaChance = cfg.getDouble("magma-chance", 3.57) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(FIRE_RESISTANCE);
        entity.addPotionEffect(INVISIBILITY);
        entity.setFireTicks(Integer.MAX_VALUE);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(POWDER);
        equipment.setItemInOffHand(POWDER);
        equipment.setChestplate(CHESTPLATE);    equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);        equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);              equipment.setBootsDropChance(0);
    }

    @EventHandler
    public void onAttack(PlayerAttackedEvent event) {
        if(!isThis(event.getAttacker()) || Rnd.nextBoolean()) return;
        event.getPlayer().setFireTicks(60);
    }

    @Override
    public void tick(LivingEntity entity) {
        if(fireChance > 0 && Rnd.chance(fireChance)) {
            Block block = entity.getLocation().getBlock();
            if(Materials.isAir(block.getType()))
                block.setType(Material.FIRE, false);
        }
        if(magmaChance > 0 && Rnd.chance(magmaChance)) {
            Block block = entity.getLocation().subtract(0, 1, 0).getBlock();
            if(block.getType() != Material.BEDROCK && Materials.isCave(block.getType()))
                block.setType(Material.MAGMA_BLOCK, false);
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
