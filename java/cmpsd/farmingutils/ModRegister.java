package cmpsd.farmingutils;

import cmpsd.farmingutils.tileentity.TileEntity_Kakashi;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModRegister {

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		ModItem.register();
		event.getRegistry().registerAll(ModItem.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlock.register();
		event.getRegistry().registerAll(ModBlock.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ModEntity.register();
		event.getRegistry().registerAll(ModEntity.ENTITIES.toArray(new EntityEntry[0]));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModItem.registerModel();
		ModBlock.registerModel();
		ModEntity.registerModel();
	}

//	@SubscribeEvent
//	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
//	}

	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntity_Kakashi.class, new ResourceLocation(Reference.MODID, "te_kakashi"));
	}
}
