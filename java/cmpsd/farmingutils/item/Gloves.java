package cmpsd.farmingutils.item;

import com.google.common.base.MoreObjects;

import cmpsd.farmingutils.ModBlock;
import cmpsd.farmingutils.ModItem;
import cmpsd.farmingutils.tileentity.TileEntity_Kakashi;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Gloves extends Item {

	public Gloves() {
		this.setRegistryName("item_gloves");
		this.setUnlocalizedName("gloves");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);

		ModItem.ITEMS.add(this);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if(!worldIn.isRemote) {
			return;
		}
		if(!isSelected) {
			return;
		}
		NBTTagCompound nbt = (NBTTagCompound)MoreObjects.firstNonNull(stack.getTagCompound(), new NBTTagCompound());
		if(nbt.getBoolean("ModeSettingKakashi")) {
//			this.drawTargetBlock(worldIn, entityIn, NBTUtil.getPosFromTag(nbt.getCompoundTag("KakashiPos")));
		}
	}

	//対象のカカシが見ているブロックを強調して表示(未解決)
	private void drawTargetBlock(World world, Entity entity, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		if(blockState.getBlock() == ModBlock.kakashi) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity != null && tileEntity instanceof TileEntity_Kakashi) {
				TileEntity_Kakashi te_kakashi = (TileEntity_Kakashi)tileEntity;
				BlockPos posTarget = te_kakashi.getTargetPos();
				IBlockState stateTarget = world.getBlockState(posTarget);
//				world.spawnParticle(EnumParticleTypes.HEART, posTarget.getX() + 0.5, posTarget.getY(), posTarget.getZ() + 0.5, 0.0D, 0.0D, 0.0D, 0);

				GlStateManager.disableAlpha();

				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.glLineWidth(5.0F);
				GlStateManager.disableTexture2D();
				GlStateManager.depthMask(false);

				if(world.getWorldBorder().contains(posTarget)) {
					Minecraft mc = Minecraft.getMinecraft();
					float partialTicks = mc.getRenderPartialTicks();

					double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
					double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
					double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

					RenderGlobal.drawSelectionBoundingBox(stateTarget.getSelectedBoundingBox(world, posTarget).grow(0.025D).offset(-dx, -dy, -dz), 0.0F, 0.0F, 0.0F, 0.4F);

					AxisAlignedBB aabb = stateTarget.getSelectedBoundingBox(world, posTarget).grow(0.025D).offset(-dx, -dy, -dz);
				}

				GlStateManager.depthMask(true);
				GlStateManager.enableTexture2D();
				GlStateManager.disableBlend();

				GlStateManager.enableAlpha();
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(playerIn.isSneaking()) {
			ItemStack itemStack = playerIn.getHeldItem(handIn);
			if(this.resetTargetPos(worldIn, playerIn, itemStack)) {
				return new ActionResult(EnumActionResult.SUCCESS, itemStack);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	private boolean resetTargetPos(World world, EntityPlayer player, ItemStack stack) {
		RayTraceResult rayTraceResult = this.rayTrace(world, player, false);
//		System.out.println("type: " + rayTraceResult.typeOfHit);
		if(rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			NBTTagCompound nbt = (NBTTagCompound)MoreObjects.firstNonNull(stack.getTagCompound(), new NBTTagCompound());
			if(nbt.getBoolean("ModeSettingKakashi")) {
				nbt.removeTag("KakashiPos");
				nbt.setBoolean("ModeSettingKakashi", false);
				stack.setTagCompound(nbt);
				return true;
			}
		}
		return false;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(player.isSneaking()) {
			ItemStack itemStack = player.getHeldItem(hand);
			if(this.setTargetPos(worldIn, pos, itemStack)) {
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	//カカシのターゲットを設定
	private boolean setTargetPos(World world, BlockPos pos, ItemStack stack) {
		IBlockState blockState = world.getBlockState(pos);
		NBTTagCompound nbt = (NBTTagCompound)MoreObjects.firstNonNull(stack.getTagCompound(), new NBTTagCompound());
		if(blockState.getBlock() == ModBlock.kakashi) {
			if(!nbt.getBoolean("ModeSettingKakashi")) {
				NBTTagCompound nbtPosKakashi = NBTUtil.createPosTag(pos);
				nbt.setTag("KakashiPos", nbtPosKakashi);
				nbt.setBoolean("ModeSettingKakashi", true);
				stack.setTagCompound(nbt);
				return true;
			}
		}
		else {
			if(nbt.getBoolean("ModeSettingKakashi")) {
				if(nbt.hasKey("KakashiPos")) {
					NBTTagCompound nbtPos = nbt.getCompoundTag("KakashiPos");
					BlockPos posKakashi = NBTUtil.getPosFromTag(nbtPos);
					IBlockState blockStateRemain = world.getBlockState(posKakashi);
					if(blockStateRemain.getBlock() == ModBlock.kakashi) {
						TileEntity tileEntity = world.getTileEntity(posKakashi);
						if(tileEntity != null && tileEntity instanceof TileEntity_Kakashi) {
							TileEntity_Kakashi te_kakashi = (TileEntity_Kakashi)tileEntity;
							BlockPos posTarget = te_kakashi.getTargetPos();
							IBlockState stateTarget = world.getBlockState(posTarget);
							if(te_kakashi.setTargetPos(pos)) {
								nbt.removeTag("KakashiPos");
								nbt.setBoolean("ModeSettingKakashi", false);
								stack.setTagCompound(nbt);
								return true;
							}
						}

//						Kakashi kakashiTarget = (Kakashi)blockStateRemain.getBlock();
//						if(kakashiTarget.setTargetPos(world, pos, posKakashi)) {
//
//						}
					}
				}
			}
		}
		return false;
	}
}
