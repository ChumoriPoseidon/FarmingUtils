package cmpsd.farmingutils;

import net.minecraftforge.fml.common.Loader;

public class ModPlugin {

	public static boolean loadedBotania = false;

	public static void init() {
		if(Loader.isModLoaded("botania")) {
			loadedBotania = true;
		}
	}
}
