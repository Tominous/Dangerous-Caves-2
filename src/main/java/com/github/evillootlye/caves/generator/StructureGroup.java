package com.github.evillootlye.caves.generator;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.Random;

public interface StructureGroup {
    String getId();

    void generate(Random random, Chunk currentChunk, Block startBlock);

    int getWeight();
}
