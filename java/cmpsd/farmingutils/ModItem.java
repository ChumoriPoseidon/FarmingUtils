package cmpsd.farmingutils;

import java.util.ArrayList;
import java.util.List;

import cmpsd.farmingutils.item.FarmersHoe;
import cmpsd.farmingutils.item.FarmersSickle;
import cmpsd.farmingutils.item.FarmersWateringCan;
import cmpsd.farmingutils.item.IUpgradable.Tier;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ModItem {

	public static List<Item> ITEMS = new ArrayList<>();

	public static Item farmersHoe_amateur;
	public static Item farmersHoe_apprentice;
	public static Item farmersHoe_adult;
	public static Item farmersHoe_expert;
	public static Item farmersHoe_master;
	public static Item farmersWateringCan_amateur;
	public static Item farmersWateringCan_apprentice;
	public static Item farmersWateringCan_adult;
	public static Item farmersWateringCan_expert;
	public static Item farmersWateringCan_master;
	public static Item farmersSickle_amateur;
	public static Item farmersSickle_apprentice;
	public static Item farmersSickle_adult;
	public static Item farmersSickle_expert;
	public static Item farmersSickle_master;

	public static Item elementSoil;
	public static Item elementFertilizer;
	public static Item fertilizer;

	public static void register() {
		farmersHoe_amateur = new FarmersHoe(Tier.AMATEUR);
		farmersHoe_apprentice = new FarmersHoe(Tier.APPRENTICE);
		farmersHoe_adult = new FarmersHoe(Tier.ADULT);
		farmersHoe_expert = new FarmersHoe(Tier.EXPERT);
		farmersHoe_master = new FarmersHoe(Tier.MASTER);
		farmersWateringCan_amateur = new FarmersWateringCan(Tier.AMATEUR);
		farmersWateringCan_apprentice = new FarmersWateringCan(Tier.APPRENTICE);
		farmersWateringCan_adult = new FarmersWateringCan(Tier.ADULT);
		farmersWateringCan_expert = new FarmersWateringCan(Tier.EXPERT);
		farmersWateringCan_master = new FarmersWateringCan(Tier.MASTER);
		farmersSickle_amateur = new FarmersSickle(Tier.AMATEUR);
		farmersSickle_apprentice = new FarmersSickle(Tier.APPRENTICE);
		farmersSickle_adult = new FarmersSickle(Tier.ADULT);
		farmersSickle_expert = new FarmersSickle(Tier.EXPERT);
		farmersSickle_master = new FarmersSickle(Tier.MASTER);
//
//		elementSoil = new ElementSoil();
//		elementFertilizer = new ElementFertilizer();
//		fertilizer = new Fertilizer();
	}

	public static void registerModel() {
		registerItemRender(farmersHoe_amateur);
		registerItemRender(farmersHoe_apprentice);
		registerItemRender(farmersHoe_adult);
		registerItemRender(farmersHoe_expert);
		registerItemRender(farmersHoe_master);
		registerItemRender(farmersWateringCan_amateur);
		registerItemRender(farmersWateringCan_apprentice);
		registerItemRender(farmersWateringCan_adult);
		registerItemRender(farmersWateringCan_expert);
		registerItemRender(farmersWateringCan_master);
		registerItemRender(farmersSickle_amateur);
		registerItemRender(farmersSickle_apprentice);
		registerItemRender(farmersSickle_adult);
		registerItemRender(farmersSickle_expert);
		registerItemRender(farmersSickle_master);

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
