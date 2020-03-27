package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.DangerousCaves;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickingMob;
import com.github.evillootlye.caves.util.Locations;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Configurable.Path("mobs.mimic")
public class Mimic extends TickingMob implements Configurable, Listener {
    private static final NamespacedKey KEY = new NamespacedKey(DangerousCaves.PLUGIN, "mimic-hp");
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
    private final List<Material> items;
    private int weight;
    private String name;

    private static final ItemStack CHEST;
    private static final ItemStack CHESTPLATE;
    private static final ItemStack BOOTS;
    private static final ItemStack LEGGINGS;
    private static final ItemStack PLANKS;
    static {
        CHEST = new ItemStack(Material.CHEST);
        CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) CHESTPLATE.getItemMeta();
        meta.setColor(Color.fromRGB(194, 105, 18));
        CHESTPLATE.setItemMeta(meta);
        BOOTS = new ItemStack(Material.LEATHER_LEGGINGS);
        BOOTS.setItemMeta(meta);
        LEGGINGS = new ItemStack(Material.LEATHER_BOOTS);
        LEGGINGS.setItemMeta(meta);
        PLANKS = new ItemStack(Material.SPRUCE_PLANKS);
    }

    public Mimic() {
        super(EntityType.WITHER_SKELETON, "mimic");
        items = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("weight", 0);
        name = Utils.clr(cfg.getString("name", "&4Mimic"));

        items.clear();
        List<String> itemsCfg = cfg.getStringList("drop-items");
        for(String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if(material != null) items.add(material);
        }
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.setSilent(true);
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(PLANKS);
        equipment.setItemInOffHand(PLANKS);
        equipment.setChestplate(CHESTPLATE);    equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);        equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);              equipment.setBootsDropChance(0);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block.getType() == Material.CHEST) {
            PersistentDataContainer container = ((Chest) block.getState()).getPersistentDataContainer();
            Double health = container.get(KEY, PersistentDataType.DOUBLE);
            if(health == null) return;
            event.setCancelled(true);
            LivingEntity entity = (LivingEntity) block.getLocation().getWorld().spawnEntity(block.getLocation(), EntityType.WITHER_SKELETON);
            setup(entity);
            entity.setHealth(health);
            event.getPlayer().playSound(event.getPlayer().getEyeLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 0.5f);
            event.getPlayer().addPotionEffect(BLINDNESS);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(isThis(entity)) entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 0.2f);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(isThis(event.getEntity())) {
            event.setDeathSound(Sound.BLOCK_ENDER_CHEST_CLOSE);
            event.setDeathSoundPitch(0.2f);
            List<ItemStack> items = event.getDrops();
            items.clear();
            items.add(CHEST);
            items.add(new ItemStack(Rnd.randomItem(this.items)));
        }
    }

    @Override
    public void tick(LivingEntity entity) {
        Block block = entity.getLocation().getBlock();
        if(((Mob)entity).getTarget() == null && Materials.isAir(block.getType())) {
            block.setType(Material.CHEST, false);

            Directional directional = (Directional) block.getBlockData();
            directional.setFacing(Locations.HORIZONTAL_FACES[Rnd.nextInt(4)]);
            block.setBlockData(directional, false);

            Chest chest = (Chest) block.getState();
            chest.getPersistentDataContainer().set(KEY, PersistentDataType.DOUBLE, entity.getHealth());
            chest.update();

            entity.remove();
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
