package cmpsd.farmingutils.item;

import cmpsd.farmingutils.ModConfig;
import cmpsd.farmingutils.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class FarmersSickle extends FarmersTool {

	public FarmersSickle() {
		this.setRegistryName("item_farmers_sickle");
		this.setUnlocalizedName("farmersSickle");
		this.setCreativeTab(CreativeTabs.TOOLS);

		ModItem.ITEMS.add(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = player.getHeldItem(hand);
		int range = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack) * 2 + 1;
		if(this.harvestRangeBlock(worldIn, player, pos, itemStack, range)) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	private boolean harvestRangeBlock(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {
		boolean result = false;
		for (int dx = 0; dx < range; dx++) {
			for (int dy = 0; dy < range; dy++) {
				for (int dz = 0; dz < range; dz++) {
					BlockPos posTarget = pos.add(-range / 2 + dx, -range / 2 + dy, -range / 2 + dz);
					IBlockState stateTarget = world.getBlockState(posTarget);
					result |= this.harvestBlock(world, posTarget, stateTarget, stateTarget.getBlock());
				}
			}
		}
		if(result) {
			world.playSound(player, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.25F, 1.0F);
			if(!world.isRemote) {
				if(!ModConfig.unbreakbleTools || EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < Enchantments.UNBREAKING.getMaxLevel()) {
					stack.damageItem(1, player);
				}
			}
		}
		return result;
	}

	private boolean harvestBlock(World world, BlockPos pos, IBlockState state, Block block) {
		if(this.canHarvest(block)) {
			return this.harvestCrop(world, pos, state, block);
		}
		return false;
	}

	private boolean canHarvest(Block block) {
		if(block instanceof IPlantable || block instanceof IGrowable) {
			return true;
		}
		if(block == Blocks.PUMPKIN || block == Blocks.MELON_BLOCK) {
			return true;
		}
		return false;
	}

	private boolean harvestCrop(World world, BlockPos pos, IBlockState state, Block block) {
		if(block instanceof BlockCrops) {
			BlockCrops crop = (BlockCrops)block;
			if(crop.isMaxAge(state)) {
				NonNullList<ItemStack> drops = NonNullList.create();
				block.getDrops(drops, world, pos, state, 0);
				for(ItemStack drop : drops) {
					block.spawnAsEntity(world, pos, drop);
				}
				world.setBlockState(pos, block.getDefaultState(), 2);
				return true;
			}
			return false;
		}
		if(block == Blocks.PUMPKIN || block == Blocks.MELON_BLOCK) {
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				Block stem = world.getBlockState(pos.offset(facing)).getBlock();
				if(block == Blocks.PUMPKIN && stem == Blocks.PUMPKIN_STEM) {
					world.destroyBlock(pos, true);
					return true;
				}
				if(block == Blocks.MELON_BLOCK && stem == Blocks.MELON_STEM) {
					world.destroyBlock(pos, true);
					return true;
				}
			}
			return false;
		}
		if(block == Blocks.CACTUS || block == Blocks.REEDS) {
			if(/*block.canSustainPlant(state, world, pos.down(), EnumFacing.UP, (IPlantable)block) &&*/ world.getBlockState(pos.down()).getBlock() == block) {
				world.destroyBlock(pos, true);
				return true;
			}
			return false;
		}
		if(block == Blocks.COCOA) {
			if(((Integer)state.getValue(BlockCocoa.AGE)).intValue() >= 3) {
				NonNullList<ItemStack> drops = NonNullList.create();
				block.getDrops(drops, world, pos, state, 0);
				for(ItemStack drop : drops) {
					block.spawnAsEntity(world, pos, drop);
				}
				world.setBlockState(pos, block.getDefaultState().withProperty(BlockCocoa.FACING, (EnumFacing)state.getValue(BlockCocoa.FACING)), 2);
				return true;
			}
			return false;
		}
		if(block == Blocks.NETHER_WART) {
			if(((Integer)state.getValue(BlockNetherWart.AGE)).intValue() >= 3) {
				NonNullList<ItemStack> drops = NonNullList.create();
				block.getDrops(drops, world, pos, state, 0);
				for(ItemStack drop : drops) {
					block.spawnAsEntity(world, pos, drop);
				}
				world.setBlockState(pos, block.getDefaultState(), 2);
				return true;
			}
			return false;
		}
		return false;
	}

	//	private void harvestPlant(World world, BlockPos pos, IBlockState state, Block block) {
	//
	//	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
}
