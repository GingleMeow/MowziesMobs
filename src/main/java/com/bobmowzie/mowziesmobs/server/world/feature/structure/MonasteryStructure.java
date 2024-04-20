package com.bobmowzie.mowziesmobs.server.world.feature.structure;

import com.bobmowzie.mowziesmobs.MowziesMobs;
import com.bobmowzie.mowziesmobs.server.config.ConfigHandler;
import com.bobmowzie.mowziesmobs.server.world.feature.ConfiguredFeatureHandler;
import com.bobmowzie.mowziesmobs.server.world.feature.structure.jigsaw.MowzieJigsawManager;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

// Based on Telepathicgrunt's tutorial class: https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.18.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/structures/RunDownHouseStructure.java
public class MonasteryStructure extends MowzieStructure {

    public static final Set<String> MUST_CONNECT_POOLS = Set.of(MowziesMobs.MODID + ":monastery/path_pool", MowziesMobs.MODID + ":monastery/path_connector_pool");
    public static final Set<String> REPLACE_POOLS = Set.of(MowziesMobs.MODID + ":monastery/path_pool");
    public static final String STRAIGHT_POOL = MowziesMobs.MODID + ":monastery/dead_end_connect_pool";

    public MonasteryStructure(StructureSettings settings) {
        // Create the pieces layout of the structure and give it to the game
        super(settings, ConfigHandler.COMMON.MOBS.SCULPTOR.generationConfig, ConfiguredFeatureHandler.SCULPTOR_BIOMES, true, true, true);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_DECORATION;
    }

    /**
     * The StructureSpawnListGatherEvent event allows us to have mobs that spawn naturally over time in our structure.
     * No other mobs will spawn in the structure of the same entity classification.
     * The reason you want to match the classifications is so that your structure's mob
     * will contribute to that classification's cap. Otherwise, it may cause a runaway
     * spawning of the mob that will never stop.
     *
     * We use Lazy so that if you classload this class before you register your entities, you will not crash.
     * Instead, the field and the entities inside will only be referenced when StructureSpawnListGatherEvent
     * fires much later after entity registration.
     */
    private static final Lazy<List<MobSpawnSettings.SpawnerData>> STRUCTURE_MONSTERS = Lazy.of(() -> ImmutableList.of(
            new MobSpawnSettings.SpawnerData(EntityType.ILLUSIONER, 100, 4, 9),
            new MobSpawnSettings.SpawnerData(EntityType.VINDICATOR, 100, 4, 9)
    ));
    private static final Lazy<List<MobSpawnSettings.SpawnerData>> STRUCTURE_CREATURES = Lazy.of(() -> ImmutableList.of(
            new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 30, 10, 15),
            new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 100, 1, 2)
    ));

    // Hooked up in StructureTutorialMain. You can move this elsewhere or change it up.
    /*public static void setupStructureSpawns(final StructureSpawnListGatherEvent event) {
        // TODO
//        if(event.getStructure() == FeatureHandler.MONASTERY.get()) {
//            event.addEntitySpawns(MobCategory.MONSTER, STRUCTURE_MONSTERS.get());
//            event.addEntitySpawns(MobCategory.CREATURE, STRUCTURE_CREATURES.get());
//        }
    }*/
    
    public static Optional<Structure.GenerationStub> createPiecesGenerator(Predicate<Structure.GenerationContext> canGeneratePredicate, Structure.GenerationContext context) {

        if (!canGeneratePredicate.test(context)) {
            return Optional.empty();
        }

        Structure.GenerationContext newContext = new Structure.GenerationContext(
                context.registryAccess(),
                context.chunkGenerator(),
                context.biomeSource(),
                context.randomState(),
                context.structureTemplateManager(),
                context.random(),
                context.seed(),
                context.chunkPos(),
                context.heightAccessor(),
                context.validBiome()
        );

        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

        Optional<Structure.GenerationStub> structurePiecesGenerator =
                MowzieJigsawManager.addPieces(
                        newContext, // Used for JigsawPlacement to get all the proper behaviors done.
                        Holder.direct(context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL)
                                .get(new ResourceLocation(MowziesMobs.MODID, "monastery/start_pool"))), blockpos, // Position of the structure. Y value is ignored if last parameter is set to true.
                        false,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                        // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                        true, // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        140,
                        "mowziesmobs:monastery/path",
                        "mowziesmobs:monastery/interior",
                        MUST_CONNECT_POOLS, REPLACE_POOLS, STRAIGHT_POOL, 23
                );

        if(structurePiecesGenerator.isPresent()) {
            MowziesMobs.LOGGER.log(Level.DEBUG, "Monastery at " + blockpos);
        }
        return structurePiecesGenerator;
    }
    
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
    	return createPiecesGenerator(t -> checkLocation(t), context);
    }

	@Override
	public StructureType<?> type() {
		//TODO
		return null;
	}
}
