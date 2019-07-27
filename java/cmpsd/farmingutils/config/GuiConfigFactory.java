package cmpsd.farmingutils.config;

import java.util.ArrayList;
import java.util.List;

import cmpsd.farmingutils.ModConfig;
import cmpsd.farmingutils.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactory extends GuiConfig {

	//GuiConfig
	public GuiConfigFactory(GuiScreen parent) {
		super(parent, getConfigElements(), Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(ModConfig.config.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		for(String category : ModConfig.config.getCategoryNames()) {
			list.add(new ConfigElement(ModConfig.config.getCategory(category)));
		}
		return list;
	}
}
