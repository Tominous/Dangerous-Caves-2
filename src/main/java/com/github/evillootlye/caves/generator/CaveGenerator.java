package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.util.Materials;
import com.github.evillootlye.caves.util.Utils;
import com.github.evillootlye.caves.util.random.AliasMethod;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Configurable.Path("generator")
public class CaveGenerator extends BlockPopulator implements Configurable {

    private final Configuration cfg;
    private double chance;
    private int maxTries;

    private final Map<String, StructureGroup> structures;
    private AliasMethod<StructureGroup> structuresPool;

    public CaveGenerator(Configuration cfg) {
        this.cfg = cfg;
        structures = new HashMap<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("chance", 50) / 100;
        maxTries = cfg.getInt("max-tries", 3);

        Set<String> worlds = new HashSet<>();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);

        for(World world : Bukkit.getWorlds()) {
            List<BlockPopulator> populators = world.getPopulators();
            populators.remove(this);
            if(chance > 0 && worlds.contains(world.getName())) populators.add(this);
        }
        List<Material> items = new ArrayList<>();

        List<String> itemsCfg = cfg.getStringList("chest-items");
        for(String materialStr : itemsCfg) {
            Material material = Material.getMaterial(materialStr.toUpperCase());
            if(material != null) items.add(material);
        }
        AbstractGroup.setItems(items);
        if(chance > 0) recalculate();
    }

    public void recalculate() {
        Set<StructureGroup> structuresSet = new HashSet<>();
        structures.values().forEach(g -> {if(g.getWeight() > 0) structuresSet.add(g);});
        if(structuresSet.isEmpty())
            chance = 0;
        else
            structuresPool = new AliasMethod<>(structuresSet, StructureGroup::getWeight);
    }

    public void register(AbstractGroup group) {
        if(!structures.containsKey(group.getId())) {
            structures.put(group.getId(), group);
            if(group instanceof Configurable)
                cfg.register((Configurable)group);
        }
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if(chance > random.nextDouble()) {
            Block block = null;
            int tries = 0;
            while(tries++ < maxTries && block == null) {
                block = getClosestAir(chunk, random.nextInt(16), random.nextInt(16));
            }
            if(block != null) structuresPool.next().generate(random, chunk, block);
        }
    }

    private static Block getClosestAir(Chunk chunk, int x, int z) {
        for(int y = 4; y < 55; y++) {
            Block block = chunk.getBlock(x, y, z);
            if(Materials.isAir(block.getType())
                && Materials.isAir(block.getRelative(BlockFace.UP).getType())
                && Materials.isCave(block.getRelative(BlockFace.DOWN).getType()))
                return block;
        }
        return null;
    }
}
