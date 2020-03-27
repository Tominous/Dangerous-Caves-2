package com.github.evillootlye.caves;

import com.github.evillootlye.caves.generator.AbstractGroup;
import com.github.evillootlye.caves.generator.CaveGenerator;
import com.github.evillootlye.caves.generator.defaults.BouldersGroup;
import com.github.evillootlye.caves.generator.defaults.BuildingsGroup;
import com.github.evillootlye.caves.generator.defaults.PillarsGroup;
import com.github.evillootlye.caves.generator.defaults.TrapsGroup;

public enum DefaultStructures {
    BOULDERS(new BouldersGroup()),
    BUILDINGS(new BuildingsGroup()),
    PILLARS(new PillarsGroup()),
    TRAPS(new TrapsGroup());

    private final AbstractGroup group;

    DefaultStructures(AbstractGroup group) {
        this.group = group;
    }

    public static void registerAll(CaveGenerator generator) {
        for(DefaultStructures struct : DefaultStructures.values())
            generator.register(struct.group);
    }
}
