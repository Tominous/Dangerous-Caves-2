package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Configurable.Path("generator.structures")
public abstract class AbstractGroup implements StructureGroup {
    private static final List<Material> chestItems = new ArrayList<>();
    private final String id;

    public AbstractGroup(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    public static Material randomStone(Random random) {
        return random.nextBoolean() ? Material.STONE : Material.COBBLESTONE;
    }

    public static void setType(Location loc, Material material) {
        loc.getBlock().setType(material, false);
    }

    public static void fillInventory(Block block) {
        if(chestItems.isEmpty()) return;
        if(!(block.getState() instanceof Container)) return;
        Inventory inventory = ((Container)block.getState()).getInventory();
        int itemsCount = Rnd.nextInt(10) + 2;
        while(itemsCount-- > 0) {
            Material material = Rnd.randomItem(chestItems);
            inventory.setItem(
                    Rnd.nextInt(inventory.getSize()),
                    new ItemStack(material, material.getMaxStackSize() > 1 ? Rnd.nextInt(3) + 1 : 1)
            );
        }
    }

    public static void setItems(Collection<Material> chestItems) {
        AbstractGroup.chestItems.clear();
        AbstractGroup.chestItems.addAll(chestItems);
    }
}
