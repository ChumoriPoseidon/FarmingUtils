package cmpsd.farmingutils.item;

import cmpsd.farmingutils.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class FarmersWateringCan extends Item implements IUpgradable {

	private int range = 1;

	public FarmersWateringCan(Tier tier) {
		this.setRegistryName("item_farmers_watering_can_" + tier.toString());
		this.setUnlocalizedName("farmersWateringCan(" + tier.toString() + ")");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);
		this.setMaxDamage(tier.getDurability());

		this.range = tier.getRange();

		ModItem.ITEMS.add(this);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = player.getHeldItem(hand);
		if(this.getDamage(itemStack) < this.getMaxDamage(itemStack)) {
			if(!this.spinkleRangeWater(worldIn, player, pos, itemStack, this.range)) {
				return EnumActionResult.FAIL;
			}
		}
		return EnumActionResult.PASS;
	}

	private boolean spinkleRangeWater(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {
		boolean result = false;
		for(int dx = 0; dx < range; dx++) {
			for(int dy = 0; dy < 3; dy++) {
				for(int dz = 0; dz < range; dz++) {
					BlockPos posTarget = pos.add(-range / 2 + dx, -1 + dy, -range / 2 + dz);
					IBlockState stateTarget = world.getBlockState(posTarget);
					result |= this.sprinkleWater(world, player, posTarget, stateTarget, stateTarget.getBlock());
					result |= this.accelerateBlock(world, player, posTarget, stateTarget, stateTarget.getBlock());
				}
			}
		}
		if(result) {
			world.playSound(player, pos, SoundEvents.WEATHER_RAIN, SoundCategory.BLOCKS, 0.25F, 2.0F);
			if(!world.isRemote) {
//				stack.damageItem(1, player);
			}
		}
		return result;
	}

	private boolean sprinkleWater(World world, EntityPlayer player, BlockPos pos, IBlockState state, Block block) {
		if(block == Blocks.FARMLAND) {
			if(this.itemRand.nextInt(3) <= 1) {
				world.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, Integer.valueOf(7)), 2);
				world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 2);
				world.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 0);
				return true;
			}
		}
		return false;
	}

	private boolean accelerateBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, Block block) {
		if(block instanceof IGrowable || block instanceof IPlantable) {
			if(this.itemRand.nextInt(3) <= 1) {
				if(!world.isRemote) {
					world.scheduleBlockUpdate(pos, block, 0, 1);
				}
				world.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 0);
				return true;
			}
		}
		return false;
	}
}
