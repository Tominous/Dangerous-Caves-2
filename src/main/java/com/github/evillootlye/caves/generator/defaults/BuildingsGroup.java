package com.github.evillootlye.caves.generator.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.generator.AbstractGroup;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Random;

public class BuildingsGroup extends AbstractGroup implements Configurable {
    private int weight;

    public BuildingsGroup() {
        super("buildings");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("buildings");
    }

    @Override
    public void generate(Random random, Chunk chunk, Block block) {
        Location loc = block.getLocation();
        switch (random.nextInt(10)) {
            default:
                setType(loc, Material.CHEST);
                fillInventory(loc.getBlock());
                break;

            case 1:
                generateStructure(random, OldStructures.chests3, loc.subtract(0, 1, 0));
                break;

            case 2:
                generateStructure(random, OldStructures.chests2, loc.subtract(0, 1, 0));
                break;

            case 3:
                generateStructure(random, OldStructures.chests1, loc);
                break;

            case 4:
                setType(loc, Material.SKELETON_SKULL);
                break;

            case 5:
                setType(loc.clone().add(1, 0, 0), Material.COBBLESTONE_SLAB);
                setType(loc.clone().subtract(1, 0, 0), Material.COBBLESTONE_SLAB);
                setType(loc.clone().add(0, 0, 1), Material.COBBLESTONE_SLAB);
                setType(loc.clone().subtract(0, 0, 1), Material.COBBLESTONE_SLAB);
                setType(loc.clone().subtract(0, 1, 0), Material.NETHERRACK);
                setType(loc, Material.FIRE);
                break;

            case 6:
                Location tempL1 = loc.clone().add(1, -1, 0);
                Location tempL2 = loc.clone().add(-1, -1, 0);
                Location tempL3 = loc.clone().add(0, -1, 1);
                Location tempL4 = loc.clone().add(0, -1, -1);
                loc.getBlock().setType(Material.CRAFTING_TABLE);
                if(tempL1.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL2.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL3.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                if(tempL4.getBlock().getType().isSolid() && random.nextInt(3) == 1) {
                    setType(tempL1.add(0, 1, 0), Material.REDSTONE_WIRE);
                }
                break;

            case 7:
                generateStructure(random, OldStructures.sfishs1, loc);
                break;

            case 8:
                generateStructure(random, OldStructures.sfishs2, loc);
                break;

            case 9:
                generateStructure(random, OldStructures.sfishs3, loc);
                break;
        }
    }

    private void generateStructure(Random random, int[][][] rock, Location loc) {
        int xMod = random.nextBoolean() ? -1 : 1;
        int zMod = random.nextBoolean() ? -1 : 1;
        for(int y = 0; y < rock[0].length; y++) for(int x = -1; x < rock.length-1; x++) for(int z = -1; z < rock[0][0].length-1; z++) {
            decideBlock(random, rock[x+1][y][z+1], loc.clone().add(x * xMod, y, z * zMod));
        }
    }

    private void decideBlock(Random random, int type, Location loc) {
        //1 == wood decide 2 == chest 3 == torch 4 == random utility 5 == door 6 = wood stay 7 == Random Ore 8 == Snow Block 9 == Spawner 10 = silverfish stone
        switch (type) {
            case 1:
                if(random.nextInt(3) > 0) setType(loc, Material.OAK_PLANKS);
                break;

            case 2:
                setType(loc, Material.CHEST);
                fillInventory(loc.getBlock());
                break;

            case 3:
                setType(loc, Material.TORCH);
                break;

            case 4:
                switch (random.nextInt(15)) {
                    case 0:
                        setType(loc, Material.FURNACE);
                        break;

                    case 1:
                        setType(loc, Material.CHEST);
                        fillInventory(loc.getBlock());
                        break;

                    case 2:
                        setType(loc, Material.CRAFTING_TABLE);
                        break;

                    case 3:
                        setType(loc, Material.CAULDRON);
                        break;

                    case 4:
                        setType(loc, Material.ANVIL);
                        break;

                    default:
                        break;
                }
                break;

            case 5:
                setType(loc, Material.OAK_PLANKS);
                break;

            case 6:
                setType(loc, Material.SPRUCE_LOG);
                break;

            case 7:
                switch (random.nextInt(3)) {
                    default:
                        setType(loc, Material.STONE);
                        break;

                    case 1:
                        setType(loc, Material.COAL_ORE);
                        break;

                    case 2:
                        setType(loc, Material.IRON_ORE);
                        break;
                }
                break;

            case 8:
                setType(loc, Material.SNOW_BLOCK);
                break;

            case 9:
                setType(loc, Material.SPAWNER);
                BlockState blockState = loc.getBlock().getState();
                CreatureSpawner spawner = ((CreatureSpawner) blockState);
                spawner.setSpawnedType(EntityType.SILVERFISH);
                blockState.update();
                break;

            case 10:
                setType(loc, Material.INFESTED_STONE);
                break;

            default:
                break;
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
