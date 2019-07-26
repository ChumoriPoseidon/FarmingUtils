package cmpsd.farmingutils.item;

import cmpsd.farmingutils.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class FarmersHoe extends Item {

	public FarmersHoe() {
		this.setRegistryName("item_farmers_hoe");
		this.setUnlocalizedName("farmersHoe");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);

		this.setMaxDamage(1024);

		ModItem.ITEMS.add(this);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 15;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if(enchantment == Enchantments.EFFICIENCY) return true;
		if(enchantment == Enchantments.UNBREAKING) return true;
		return false;
	}

	/**
	 * [XU2]mechanical user: (Use_Item_On_Block / Right Click)
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemStack = player.getHeldItem(hand);
		if(!player.canPlayerEdit(pos.offset(facing), facing, itemStack)) {
			return EnumActionResult.FAIL;
		}

		int hook = ForgeEventFactory.onHoeUse(itemStack, player, worldIn, pos);
		if(hook != 0) {
			return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		}
		if(facing != EnumFacing.DOWN) {
			if(player.isSneaking()) {
				if(this.tillRangeBlock(worldIn, player, pos, itemStack, 1)) {
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.PASS;
			}
			int range = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack) * 2 + 1;
			if(this.tillRangeBlock(worldIn, player, pos, itemStack, range)) {
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	private boolean tillRangeBlock(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {
		boolean result = false;
		for(int dx = 0; dx < range; dx++) {
			for(int dz = 0; dz < range; dz++) {
				BlockPos posTarget = pos.add(-range / 2 + dx, 0, -range / 2 + dz);
				IBlockState stateTarget = world.getBlockState(posTarget);
				if(world.isAirBlock(posTarget.up())) {
					result |= this.tillBlock(world, posTarget, stateTarget, stateTarget.getBlock());
				}
			}
		}
		if(result) {
			world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 0.5F, 1.0F);
			if(!world.isRemote) {
				if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < Enchantments.UNBREAKING.getMaxLevel()) {
					stack.damageItem(1, player);
				}
			}
		}
		return result;
	}

	private boolean tillBlock(World world, BlockPos pos, IBlockState state, Block block) {
		if(block == Blocks.GRASS) {
			this.till(world, pos, Blocks.FARMLAND.getDefaultState());
			return true;
		}
		if(block == Blocks.DIRT) {
			switch((BlockDirt.DirtType)state.getValue(BlockDirt.VARIANT)) {
			case DIRT:
				this.till(world, pos, Blocks.FARMLAND.getDefaultState());
				return true;
			case COARSE_DIRT:
				this.till(world, pos, Blocks.FARMLAND.getDefaultState());
				return true;
			case PODZOL:
				this.till(world, pos, Blocks.FARMLAND.getDefaultState());
				return true;
			}
		}
		if(block == Blocks.GRASS_PATH) {
			this.till(world, pos, Blocks.FARMLAND.getDefaultState());
		}
//		if(ModPlugin.loadedBotania) {
//			if(block == ModBlocks.enchantedSoil) {
//				this.till(world, player, pos, Blocks.FARMLAND.getDefaultState());
//				return true;
//			}
//		}
		return false;
	}

	private void till(World world, BlockPos pos, IBlockState state) {
		if(!world.isRemote) {
			world.setBlockState(pos, state, 11);
		}
	}
}
