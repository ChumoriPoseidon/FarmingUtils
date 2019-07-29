package cmpsd.farmingutils;

import java.util.ArrayList;
import java.util.List;

import cmpsd.farmingutils.item.FarmersHoe;
import cmpsd.farmingutils.item.FarmersSeedBag;
import cmpsd.farmingutils.item.FarmersSickle;
import cmpsd.farmingutils.item.FarmersWateringCan;
import cmpsd.farmingutils.item.Gloves;
import cmpsd.farmingutils.item.GrowthSoil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ModItem {

	public static List<Item> ITEMS = new ArrayList<>();

	public static Item gloves;

	public static Item farmersSeedBag;
	public static Item farmersHoe;
	public static Item farmersSickle;
	public static Item farmersWateringCan;

	public static Item growthSoil;



//	public static Item soilFragment;
//	public static Item fertilizerFragment;
//	public static Item fertilizer;

	public static void register() {

		gloves = new Gloves();

		farmersSeedBag = new FarmersSeedBag();
		farmersHoe = new FarmersHoe();
		farmersSickle = new FarmersSickle();
		farmersWateringCan = new FarmersWateringCan();

		growthSoil = new GrowthSoil();

//		elementSoil = new ElementSoil();
//		elementFertilizer = new ElementFertilizer();
//		fertilizer = new Fertilizer();
	}

	public static void registerModel() {

		registerItemRender(gloves);

		registerItemRender(farmersSeedBag);
		registerItemRender(farmersHoe);
		registerItemRender(farmersSickle);
		registerItemRender(farmersWateringCan);

		registerItemRender(growthSoil);

//		registerItemRender(elementSoil);
//		registerItemRender(elementFertilizer);
//		registerItemRender(fertilizer);
	}

	public static void registerItemRender(Item item, String[] subName) {
		for(int meta = 0; meta < subName.length; meta++) {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName() + "_" + subName[meta], "Inventory"));
		}
	}

	public static void registerItemRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "Inventory"));
	}
}
