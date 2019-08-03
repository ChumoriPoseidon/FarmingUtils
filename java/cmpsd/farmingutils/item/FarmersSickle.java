package cmpsd.farmingutils.item;

import cmpsd.farmingutils.Main;
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
import net.minecraft.entity.item.EntityItem;
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


/*
 * TODO Enable to collect items harvested by Sickle.
 */


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
		NonNullList<ItemStack> dropList = NonNullList.create();
		Iterable<BlockPos> posList = BlockPos.getAllInBox(pos.add(-range / 2, -range / 2, -range / 2), pos.add(range / 2, range / 2, range / 2));
		for(BlockPos posTarget : posList) {
			IBlockState stateTarget = world.getBlockState(posTarget);
			if(stateTarget.getBlock() == Blocks.AIR) {
				continue;
			}
			dropList.addAll(this.harvestBlock(world, posTarget, stateTarget, stateTarget.getBlock()));
		}
		if(!dropList.isEmpty()) {
			world.playSound(player, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 0.25F, 1.0F);
			if(!world.isRemote) {
				if(!ModConfig.unbreakbleTools || EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < Enchantments.UNBREAKING.getMaxLevel()) {
					stack.damageItem(1, player);
				}
				for(ItemStack drop : dropList) {
					world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop));
				}
			}
		}
		return !dropList.isEmpty();
	}

	private NonNullList<ItemStack> harvestBlock(World world, BlockPos pos, IBlockState state, Block block) {
		if(this.canHarvest(block)) {
			return this.harvestCrop(world, pos, state, block);
		}
		return NonNullList.create();
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

	private NonNullList<ItemStack> harvestCrop(World world, BlockPos pos, IBlockState state, Block block) {
		NonNullList<ItemStack> drops = NonNullList.create();
		if(block instanceof BlockCrops) {
			BlockCrops crop = (BlockCrops)block;
			if(crop.isMaxAge(state)) {
				block.getDrops(drops, world, pos, state, 0);
				world.setBlockState(pos, block.getDefaultState(), 2);
			}
			return drops;
		}
		if(block == Blocks.PUMPKIN || block == Blocks.MELON_BLOCK) {
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				Block stem = world.getBlockState(pos.offset(facing)).getBlock();
				if(block == Blocks.PUMPKIN && stem == Blocks.PUMPKIN_STEM) {
					drops.add(new ItemStack(block));
					world.destroyBlock(pos, false);
					return drops;
				}
				if(block == Blocks.MELON_BLOCK && stem == Blocks.MELON_STEM) {
					drops.add(new ItemStack(block));
					world.destroyBlock(pos, false);
					return drops;
				}
			}
			return drops;
		}
		if(block == Blocks.CACTUS || block == Blocks.REEDS) {
			int count = 0;
			do {
				count++;
				pos = pos.up();
			}
			while(world.getBlockState(pos).getBlock() == block);
			if(count > 1) {
				ItemStack drop = new ItemStack(block.getItemDropped(state, itemRand, 0), count - 1);
				Main.LOGGER.info("drop: " + drop.getDisplayName());
				drops.add(drop);
			}
			do {
				world.destroyBlock(pos, false);
				pos = pos.down();
				count--;
			}
			while(count > 0);
			return drops;
		}
		if(block == Blocks.COCOA) {
			if(((Integer)state.getValue(BlockCocoa.AGE)).intValue() >= 3) {
				block.getDrops(drops, world, pos, state, 0);
				world.setBlockState(pos, block.getDefaultState().withProperty(BlockCocoa.FACING, (EnumFacing)state.getValue(BlockCocoa.FACING)), 2);
				return drops;
			}
			return drops;
		}
		if(block == Blocks.NETHER_WART) {
			if(((Integer)state.getValue(BlockNetherWart.AGE)).intValue() >= 3) {
				block.getDrops(drops, world, pos, state, 0);
				world.setBlockState(pos, block.getDefaultState(), 2);
				return drops;
			}
			return drops;
		}
		return drops;
	}

//	private boolean harvestCrop(World world, BlockPos pos, IBlockState state, Block block) {
//		if(block instanceof BlockCrops) {
//			BlockCrops crop = (BlockCrops)block;
//			if(crop.isMaxAge(state)) {
//				NonNullList<ItemStack> drops = NonNullList.create();
//				block.getDrops(drops, world, pos, state, 0);
//				for(ItemStack drop : drops) {
//					block.spawnAsEntity(world, pos, drop);
//				}
//				world.setBlockState(pos, block.getDefaultState(), 2);
//				return true;
//			}
//			return false;
//		}
//		if(block == Blocks.PUMPKIN || block == Blocks.MELON_BLOCK) {
//			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
//				Block stem = world.getBlockState(pos.offset(facing)).getBlock();
//				if(block == Blocks.PUMPKIN && stem == Blocks.PUMPKIN_STEM) {
//					world.destroyBlock(pos, true);
//					return true;
//				}
//				if(block == Blocks.MELON_BLOCK && stem == Blocks.MELON_STEM) {
//					world.destroyBlock(pos, true);
//					return true;
//				}
//			}
//			return false;
//		}
//		if(block == Blocks.CACTUS || block == Blocks.REEDS) {
//			if(world.getBlockState(pos.down()).getBlock() == block) {
//				world.destroyBlock(pos, true);
//				return true;
//			}
//			return false;
//		}
//		if(block == Blocks.COCOA) {
//			if(((Integer)state.getValue(BlockCocoa.AGE)).intValue() >= 3) {
//				NonNullList<ItemStack> drops = NonNullList.create();
//				block.getDrops(drops, world, pos, state, 0);
//				for(ItemStack drop : drops) {
//					block.spawnAsEntity(world, pos, drop);
//				}
//				world.setBlockState(pos, block.getDefaultState().withProperty(BlockCocoa.FACING, (EnumFacing)state.getValue(BlockCocoa.FACING)), 2);
//				return true;
//			}
//			return false;
//		}
//		if(block == Blocks.NETHER_WART) {
//			if(((Integer)state.getValue(BlockNetherWart.AGE)).intValue() >= 3) {
//				NonNullList<ItemStack> drops = NonNullList.create();
//				block.getDrops(drops, world, pos, state, 0);
//				for(ItemStack drop : drops) {
//					block.spawnAsEntity(world, pos, drop);
//				}
//				world.setBlockState(pos, block.getDefaultState(), 2);
//				return true;
//			}
//			return false;
//		}
//		return false;
//	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
}
