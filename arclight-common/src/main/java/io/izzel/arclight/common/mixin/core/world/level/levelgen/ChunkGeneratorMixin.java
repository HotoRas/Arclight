package io.izzel.arclight.common.mixin.core.world.level.levelgen;

import io.izzel.arclight.common.bridge.core.world.IWorldBridge;
import io.izzel.arclight.common.bridge.core.world.WorldBridge;
import io.izzel.arclight.common.bridge.core.world.level.levelgen.ChunkGeneratorBridge;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.bukkit.craftbukkit.v.generator.CraftLimitedRegion;
import org.bukkit.generator.BlockPopulator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin implements ChunkGeneratorBridge {

    // @formatter:off
    @Shadow public abstract void applyBiomeDecoration(WorldGenRegion p_62168_, StructureFeatureManager p_62169_);
    @Shadow @Final @Mutable protected BiomeSource biomeSource;
    @Shadow @Final @Mutable protected BiomeSource runtimeBiomeSource;
    // @formatter:on

    public void addDecorations(WorldGenRegion region, StructureFeatureManager structureManager, boolean vanilla) {
        if (vanilla) {
            this.applyBiomeDecoration(region, structureManager);
        }
        org.bukkit.World world = ((WorldBridge) ((IWorldBridge) region).bridge$getMinecraftWorld()).bridge$getWorld();
        // only call when a populator is present (prevents unnecessary entity conversion)
        if (world.getPopulators().size() != 0) {
            CraftLimitedRegion limitedRegion = new CraftLimitedRegion(region);
            int x = region.getCenter().x;
            int z = region.getCenter().z;
            for (BlockPopulator populator : world.getPopulators()) {
                WorldgenRandom random = new WorldgenRandom();
                random.setDecorationSeed(region.getSeed(), x, z);
                populator.populate(world, random, x, z, limitedRegion);
            }
            limitedRegion.saveEntities();
            limitedRegion.breakLink();
        }
    }

    public void buildBedrock(ChunkAccess chunkAccess, Random random) {
        throw new UnsupportedOperationException("Methode not overridden");
    }

    public WorldgenRandom buildSurface(WorldGenRegion region, ChunkAccess chunkAccess) {
        throw new UnsupportedOperationException("Methode not overridden");
    }

    @Override
    public void bridge$addDecorations(WorldGenRegion region, StructureFeatureManager structureManager, boolean vanilla) {
        addDecorations(region, structureManager, vanilla);
    }

    @Override
    public void bridge$buildBedrock(ChunkAccess chunkAccess, Random random) {
        buildBedrock(chunkAccess, random);
    }

    @Override
    public WorldgenRandom bridge$buildSurface(WorldGenRegion region, ChunkAccess chunkAccess) {
        return buildSurface(region, chunkAccess);
    }

    @Override
    public void bridge$setBiomeSource(BiomeSource biomeSource) {
        this.biomeSource = this.runtimeBiomeSource = biomeSource;
    }
}