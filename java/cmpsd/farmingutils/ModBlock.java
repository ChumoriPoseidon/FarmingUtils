package cmpsd.farmingutils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ModBlock {

	public static List<Block> BLOCKS = new ArrayList<Block>();

	public static void register() {

	}

	public static void registerModel() {

	}

	public static void registerBlockRender(Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "Inventory"));
	}
}
