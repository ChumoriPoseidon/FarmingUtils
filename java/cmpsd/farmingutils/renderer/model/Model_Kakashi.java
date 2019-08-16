package cmpsd.farmingutils.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Model_Kakashi extends ModelBase {

	public int textureWidth = 64;
	public int textureHeight = 32;

	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer arm_right;
	public ModelRenderer arm_left;
	public ModelRenderer leg;

	public Model_Kakashi() {
		// origin: (8, 24, 8)
		this.head = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight); // ModelRenderer(model[ModelBase], offsetX[int], offsetY[int])
		this.head.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8); // addBox(offX[float], offY[float], offZ[float], width[int], height[int], depth[int])
		this.body = new ModelRenderer(this, 0, 16).setTextureSize(textureWidth, textureHeight);
		this.body.addBox(-6.0F, 4.0F, -3.5F, 12, 8, 7);
		this.arm_right = new ModelRenderer(this, 40, 0).setTextureSize(textureWidth, textureHeight);
		this.arm_right.addBox(-12.0F, 5.0F, -1.0F, 6, 2, 2);
		this.arm_left = new ModelRenderer(this, 40, 4).setTextureSize(textureWidth, textureHeight);
		this.arm_left.addBox(6.0F, 5.0F, -1.0F, 6, 2, 2);
		this.leg = new ModelRenderer(this, 40, 8).setTextureSize(textureWidth, textureHeight);
		this.leg.addBox(-1.0F, 12.0F, -1.0F, 2, 12, 2);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
//		GlStateManager.translate(0.0F, 0.0F, 0.0F);

		this.head.render(scale);
		this.body.render(scale);
		this.arm_right.render(scale);
		this.arm_left.render(scale);
		this.leg.render(scale);
	}
}
