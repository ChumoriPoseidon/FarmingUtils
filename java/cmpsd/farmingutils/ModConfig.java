package cmpsd.farmingutils;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfig {

	public static Configuration config;
	public static ModConfig instance;

	public static int configInt;
	public static float configFloat;
	public static boolean configBoolean;


	//Unbreakable tools with enchantment of "Unbreaking 3"
	public static boolean unbreakbleTools;
	//Rate of growth acceleration with watering can
	public static int rateGrowthAcceleration;

	public static void init(File file) {
		config = new Configuration(file);
		syncConfig();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(Reference.MODID)) {
			syncConfig();
		}
	}

	public static void syncConfig() {
		String category;

		category = "main";
		config.addCustomCategoryComment(category, "This category for all things Farming Utils.");

		configInt = config.getInt("config_int", category, 777, 0, 1000, "Sample integer config.");
		configFloat = config.getFloat("config_float", category, 123.4F, 0.0F, 1000.0F, "Sample float config.");
		configBoolean = config.getBoolean("config_boolean", category, false, "Sample boolean config");

		unbreakbleTools = config.getBoolean("unbreakable_tools", category, true, "Unbreakable tools with enchantment of \"Unbreaking 3\". (Excluding Seed Bag)");
		rateGrowthAcceleration = config.getInt("rate_growth_acceleration", category, 3, 1, 10, "Rate of growth acceleration with watering can.\n(e.g. 1: 10%, 2: 20%)");

		if(config.hasChanged()) {
			config.save();
		}
	}
}
