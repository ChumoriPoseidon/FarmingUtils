package cmpsd.farmingutils;

import java.util.ArrayList;
import java.util.List;

import cmpsd.farmingutils.entity.Entity_Kakashi;
import cmpsd.farmingutils.renderer.Render_Kakashi;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntity {

	public static List<EntityEntry> ENTITIES = new ArrayList<>();

	public static void register() {
		registerEntity(Entity_Kakashi.class, "kakashi", "kakashi", 0, 128, 3, true, 0x000000, 0xFFFFFF);
	}

	private static void registerEntity(Class<? extends Entity> entity, String key, String name, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, key), entity, name, id, Main.INSTANCE, trackingRange, updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
	}

	public static void registerModel() {
		RenderingRegistry.registerEntityRenderingHandler(Entity_Kakashi.class, new Render_Kakashi());
	}
}
