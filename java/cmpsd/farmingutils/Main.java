package cmpsd.farmingutils;

import org.apache.logging.log4j.Logger;

import cmpsd.farmingutils.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME, acceptedMinecraftVersions = Reference.MC_VERSION, guiFactory = Reference.GUI_FACTORY)
public class Main {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static Logger LOGGER;

//	@Mod.EventHandler
//	public void construct(FMLConstructionEvent event) {
//	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		LOGGER = event.getModLog();

		//Setting configuration
		ModConfig.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ModConfig());

		//Register block, item, entity, (recipe)
		MinecraftForge.EVENT_BUS.register(new ModRegister());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);

		//Register recipe, tile_entity, mob_drop
		ModRegister.registerTileEntities();

		//Setting ore_dict, world_gen, event
		MinecraftForge.EVENT_BUS.register(new ModEvent());

		//Setting GUI
//		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);

		//Setting plug_in
		ModPlugin.init();
	}
}
