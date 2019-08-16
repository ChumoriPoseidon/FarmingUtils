package cmpsd.farmingutils.item;

import cmpsd.farmingutils.ModConfig;
import cmpsd.farmingutils.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class FarmersWateringCan extends FarmersTool {

	private int time = 0;
	private boolean canUse = false;

	public FarmersWateringCan() {
		this.setRegistryName("item_farmers_watering_can");
		this.setUnlocalizedName("farmersWateringCan");
		this.setCreativeTab(CreativeTabs.TOOLS);

		ModItem.ITEMS.add(this);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

//	@Override
//	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
//		Main.LOGGER.info(String.format("Update by %s", entityIn.toString()));
//		if(entityIn instanceof FakePlayer) {
//			FakePlayer fakePlayer = (FakePlayer)entityIn;
//		}
//		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
//	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(playerIn.isSneaking()) {
			ItemStack itemStack = playerIn.getHeldItem(handIn);
			return this.refillWater(worldIn, playerIn, itemStack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	private ActionResult<ItemStack> refillWater(World world, EntityPlayer player, ItemStack stack) {
		RayTraceResult rayTraceResult = this.rayTrace(world, player, stack.getItemDamage() > 0);
		if(rayTraceResult == null) {
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		if(rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		BlockPos pos = rayTraceResult.getBlockPos();
		if(!player.canPlayerEdit(pos.offset(rayTraceResult.sideHit), rayTraceResult.sideHit, stack)) {
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		IBlockState blockState = world.getBlockState(pos);
		Material material = blockState.getMaterial();
		if(material == Material.WATER && ((Integer)blockState.getValue(BlockLiquid.LEVEL)).intValue() == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
			stack.setItemDamage(0);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//		worldIn.spawnParticle(EnumParticleTypes.WATER_DROP, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 0);
//		this.time++;
//		this.canUse = ((this.time % 4) == 0);
		this.canUse = true;

		if(this.canUse) {
			ItemStack itemStack = player.getHeldItem(hand);
			if(this.getDamage(itemStack) < this.getMaxDamage(itemStack)) {
				int range = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack) * 2 + 1;
				if(!this.spinkleRangeWater(worldIn, player, pos, itemStack, range)) {
					return EnumActionResult.FAIL;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	private boolean spinkleRangeWater(World world, EntityPlayer player, BlockPos pos, ItemStack stack, int range) {
		boolean result = false;
		for(int dx = 0; dx < range; dx++) {
			for(int dy = -1; dy <= 1; dy++) {
				for(int dz = 0; dz < range; dz++) {
					BlockPos posTarget = pos.add(-range / 2 + dx, dy, -range / 2 + dz);
					BlockPos posTargetDown = posTarget.down();
					IBlockState stateTarget = world.getBlockState(posTarget);
					IBlockState stateTargetDown = world.getBlockState(posTargetDown);
					result |= this.sprinkleWater(world, player, posTargetDown, stateTargetDown, stateTargetDown.getBlock());
					result |= this.accelerateBlock(world, player, posTarget, stateTarget, stateTarget.getBlock());
				}
			}
		}
		if(result) {
//			world.playSound(player, pos, SoundEvents.WEATHER_RAIN, SoundCategory.BLOCKS, 0.25F, 2.0F);
			if(!world.isRemote) {
				if(!ModConfig.unbreakbleTools || EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) < Enchantments.UNBREAKING.getMaxLevel()) {
					stack.damageItem(1, player);
				}
//				player.playSound(SoundEvents.WEATHER_RAIN, 0.25F, 2.0F);
			}
		}
		return result;
	}

	private boolean sprinkleWater(World world, EntityPlayer player, BlockPos pos, IBlockState state, Block block) {
		if(block == Blocks.FARMLAND) {
			if(state.getValue(BlockFarmland.MOISTURE) < 7) {
				if(this.itemRand.nextInt(2) == 0) {
					world.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, Integer.valueOf(7)), 4);
//					world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 2);
					return true;
				}
			}
		}
		return false;
	}

	private boolean accelerateBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, Block block) {
		if(block instanceof IGrowable || block instanceof IPlantable) {
			int rateAcceleration = ModConfig.rateGrowthAcceleration;
			if(this.itemRand.nextInt(10) < rateAcceleration) {
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
