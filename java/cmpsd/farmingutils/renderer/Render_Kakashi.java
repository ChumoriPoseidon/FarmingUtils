package cmpsd.farmingutils.renderer;

import cmpsd.farmingutils.Reference;
import cmpsd.farmingutils.entity.Entity_Kakashi;
import cmpsd.farmingutils.renderer.model.Model_Kakashi;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Render_Kakashi implements IRenderFactory<Entity_Kakashi> {

	public Render<? super Entity_Kakashi> createRenderFor(RenderManager manager){
		return new CustomRender(manager);
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/entities/mob/kakashi_64x32.png");

	private class CustomRender<T extends EntityLiving> extends RenderLiving<T> {

		public CustomRender(RenderManager managerIn) {
			super(managerIn, new Model_Kakashi(), 0.5F);
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return TEXTURE;
		}
	}
}
