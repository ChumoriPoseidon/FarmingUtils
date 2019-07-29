package cmpsd.farmingutils.item;

import com.google.common.base.MoreObjects;

import cmpsd.farmingutils.ModBlock;
import cmpsd.farmingutils.ModItem;
import cmpsd.farmingutils.block.Kakashi;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = player.getHeldItem(hand);
		if(player.isSneaking()) {
			if(this.setTargetPos(worldIn, pos, itemStack)) {
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

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
						Kakashi kakashiTarget = (Kakashi)blockStateRemain.getBlock();
						if(kakashiTarget.setTargetPos(world, pos, posKakashi)) {
							nbt.removeTag("KakashiPos");
							nbt.setBoolean("ModeSettingKakashi", false);
							stack.setTagCompound(nbt);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
