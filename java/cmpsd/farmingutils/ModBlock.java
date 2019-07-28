package cmpsd.farmingutils;

import java.util.ArrayList;
import java.util.List;

import cmpsd.farmingutils.block.Kakashi;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ModBlock {

	public static List<Block> BLOCKS = new ArrayList<Block>();

	public static Block kakashi;

	public static void register() {

		kakashi = new Kakashi();
	}

	public static void registerModel() {

		registerBlockRender(kakashi);
	}

	public static void registerBlockRender(Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "Inventory"));
	}
}
