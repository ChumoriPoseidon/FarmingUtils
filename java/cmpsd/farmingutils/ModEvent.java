package cmpsd.farmingutils;

import org.lwjgl.opengl.GL11;

import com.google.common.base.MoreObjects;

import cmpsd.farmingutils.tileentity.TileEntity_Kakashi;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEvent {

	// グローブを持ったときに対象のカカシが見ているブロックを強調
//	@SubscribeEvent
//	@SideOnly(Side.CLIENT)
//	public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
//		EntityPlayer player = event.getPlayer();
//		RayTraceResult rayTraceResult = event.getTarget();
//		if(rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
//			return;
//		}
//		ItemStack heldItem = player.getHeldItemMainhand();
//		if(heldItem.isEmpty()) {
//			heldItem = player.getHeldItemOffhand();
//			if(heldItem.isEmpty()) {
//				return;
//			}
//		}
//		World world = player.world;
//		BlockPos pos = rayTraceResult.getBlockPos();
//	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawBlockHighlightOnHeldGloves(RenderWorldLastEvent event) {
		EntityPlayer player = Main.proxy.getPlayer();
		ItemStack itemStack = player.getHeldItemMainhand();
		if(!itemStack.isEmpty() && itemStack.getItem() == ModItem.gloves) {
			NBTTagCompound nbt = (NBTTagCompound)MoreObjects.firstNonNull(itemStack.getTagCompound(), new NBTTagCompound());
			if(nbt.getBoolean("ModeSettingKakashi")) {
				BlockPos pos = NBTUtil.getPosFromTag(nbt.getCompoundTag("KakashiPos"));
				World world = player.world;
				IBlockState blockState = world.getBlockState(pos);
				if(blockState.getBlock() == ModBlock.kakashi) {
					TileEntity_Kakashi te_kakashi = (TileEntity_Kakashi)world.getTileEntity(pos);
					BlockPos posTarget = te_kakashi.getTargetPos();
					this.renderBlockHighlight(player, posTarget, event.getPartialTicks(), 0xFFFFFFFF);
				}
				this.renderBlockHighlight(player, pos, event.getPartialTicks(), 0xFF00FF00);
			}
		}
//		RayTraceResult rayTraceResult = Minecraft.getMinecraft().objectMouseOver;
//		if(rayTraceResult != null && rayTraceResult.getBlockPos() != null && rayTraceResult.sideHit != null) {
//			this.renderBlockHighlight(player, rayTraceResult.getBlockPos(), event.getPartialTicks());
//		}
	}

	private void renderBlockHighlight(EntityPlayer player, BlockPos pos, float partialTicks, int color) {
		double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		double posX = pos.getX();
		double posY = pos.getY();
		double posZ = pos.getZ();

		GlStateManager.pushAttrib();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		GlStateManager.pushMatrix();
		GlStateManager.translate(-dx, -dy, -dz);
		{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			GlStateManager.glLineWidth(3.0F);
			{
				this.renderLine(buffer, posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D, color);
				tessellator.draw();
			}
		}
		GlStateManager.popMatrix();

		GlStateManager.depthMask(true);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.popAttrib();
	}

	private void renderLine(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
		float red = (float)(color >> 16 & 255) / 255.0F;
		float green = (float)(color >> 8 & 255) / 255.0F;
		float blue = (float)(color & 255) / 255.0F;
		float alpha = (float)(color >> 24 & 255) / 255.0F;

		buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();


		buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();


		buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();


		buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();


		buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

		buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
	}

	private void renderBlock() {

	}

	private void renderBox() {

	}
}
