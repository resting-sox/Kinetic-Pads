package resting_sox.soxmodhack.block;

import resting_sox.soxmodhack.Main;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block KINETIC_PAD_SOFT = registerPad(PadTier.SOFT);
    public static final Block KINETIC_PAD      = registerPad(PadTier.NORMAL);
    public static final Block KINETIC_PAD_HARD = registerPad(PadTier.HARD);

    private static Block registerPad(PadTier tier) {
        Identifier id = Identifier.fromNamespaceAndPath(Main.MOD_ID,tier.id()); // your mod id

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey,
                new KineticPadBlock(
                        BlockBehaviour.Properties.of()
                                .setId(blockKey)
                                .strength(1.5f)
                                .sound(SoundType.METAL),
                        tier));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties()
                        .setId(itemKey)
                        .useBlockDescriptionPrefix()));

        return block;
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
                .register(output -> {
                    output.accept(KINETIC_PAD_SOFT);
                    output.accept(KINETIC_PAD);
                    output.accept(KINETIC_PAD_HARD);
                });    }
}
